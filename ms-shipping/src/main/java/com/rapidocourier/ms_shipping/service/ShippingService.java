package com.rapidocourier.ms_shipping.service;

import com.rapidocourier.ms_shipping.client.CustomerClient;
import com.rapidocourier.ms_shipping.dto.AssignCategoriesRequest;
import com.rapidocourier.ms_shipping.dto.ShippingRequest;
import com.rapidocourier.ms_shipping.dto.ShippingResponse;
import com.rapidocourier.ms_shipping.dto.ShippingUpdateRequest;
import com.rapidocourier.ms_shipping.dto.StatusHistoryResponse;
import com.rapidocourier.ms_shipping.dto.StatusUpdateRequest;
import com.rapidocourier.ms_shipping.entity.PackageCategory;
import com.rapidocourier.ms_shipping.entity.Shipping;
import com.rapidocourier.ms_shipping.entity.ShippingStatus;
import com.rapidocourier.ms_shipping.repository.PackageCategoryRepository;
import com.rapidocourier.ms_shipping.repository.ShippingRepository;
import com.rapidocourier.ms_shipping.repository.ShippingStatusHistoryRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ShippingService {
    private static final Logger log = LoggerFactory.getLogger(ShippingService.class);
    private static final BigDecimal BASE_TARIFF = BigDecimal.valueOf(8);
    private static final BigDecimal WEIGHT_RATE = BigDecimal.valueOf(4.5);
    private static final BigDecimal DECLARED_VALUE_RATE = BigDecimal.valueOf(0.01);

    private static final Map<ShippingStatus, Set<ShippingStatus>> VALID_TRANSITIONS = Map.of(
            ShippingStatus.REGISTRADO, Set.of(ShippingStatus.EN_TRANSITO, ShippingStatus.CANCELADO),
            ShippingStatus.EN_TRANSITO, Set.of(ShippingStatus.EN_REPARTO, ShippingStatus.CANCELADO),
            ShippingStatus.EN_REPARTO, Set.of(ShippingStatus.ENTREGADO, ShippingStatus.CANCELADO),
            ShippingStatus.ENTREGADO, Set.of(),
            ShippingStatus.CANCELADO, Set.of()
    );

    private final ShippingRepository repository;
    private final PackageCategoryRepository categoryRepository;
    private final ShippingStatusHistoryRepository historyRepository;
    private final CustomerClient customerClient;

    public ShippingService(ShippingRepository repository, PackageCategoryRepository categoryRepository,
                           ShippingStatusHistoryRepository historyRepository, CustomerClient customerClient) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.historyRepository = historyRepository;
        this.customerClient = customerClient;
    }

    @Transactional
    @CircuitBreaker(name = "customerServiceCB", fallbackMethod = "fallbackCreateShipping")
    @Retry(name = "customerServiceRetry")
    public Shipping createShipping(ShippingRequest request, String username) {
        Boolean senderExists = customerClient.checkCustomerExists(request.getSenderDni()).getData();

        if (!Boolean.TRUE.equals(senderExists)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Remitente no registrado");
        }

        Shipping shipping = Shipping.builder()
                .trackingCode(generateTrackingCode())
                .description(request.getDescription())
                .weightKg(scaleMoney(request.getWeightKg()))
                .declaredValue(scaleMoney(request.getDeclaredValue()))
                .tariff(calculateTariff(request))
                .originBranch(request.getOriginBranch())
                .destinationBranch(request.getDestinationBranch())
                .senderDni(request.getSenderDni())
                .recipientDni(request.getRecipientDni())
                .customerDni(request.getSenderDni())
                .status(ShippingStatus.REGISTRADO.name())
                .build();

        shipping.addHistory(ShippingStatus.REGISTRADO, username);
        return repository.save(shipping);
    }

    public Shipping fallbackCreateShipping(ShippingRequest request, String username, Throwable error) {
        if (error instanceof ResponseStatusException responseStatusException) {
            throw responseStatusException;
        }

        log.error("Fallo la comunicacion con ms-customer", error);
        throw new ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "El servicio de validacion de clientes no esta disponible. No se puede procesar el envio."
        );
    }

    @Transactional
    public ShippingResponse updateStatus(UUID id, StatusUpdateRequest request, String username) {
        Shipping shipping = findShipping(id);
        ShippingStatus newStatus = ShippingStatus.valueOf(request.getNewStatus());
        ShippingStatus currentStatus = parseCurrentStatus(shipping.getStatus());

        if (!VALID_TRANSITIONS.get(currentStatus).contains(newStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Transicion invalida de " + currentStatus + " a " + newStatus
            );
        }

        shipping.setStatus(newStatus.name());
        shipping.addHistory(newStatus, username);
        return toResponse(repository.save(shipping));
    }

    public List<StatusHistoryResponse> getHistory(UUID shippingId) {
        if (!repository.existsById(shippingId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Envio no encontrado");
        }

        return historyRepository.findByShippingIdOrderByChangedAtAsc(shippingId)
                .stream()
                .map(history -> StatusHistoryResponse.builder()
                        .id(history.getId())
                        .status(history.getStatus())
                        .changedAt(history.getChangedAt())
                        .changedBy(history.getChangedBy())
                        .build())
                .toList();
    }

    public ShippingResponse getById(UUID id) {
        return toResponse(findShipping(id));
    }

    @Transactional
    public ShippingResponse updateShipping(UUID id, ShippingUpdateRequest request) {
        Shipping shipping = findShipping(id);
        shipping.setDescription(request.getDescription());
        shipping.setWeightKg(scaleMoney(request.getWeightKg()));
        shipping.setDeclaredValue(scaleMoney(request.getDeclaredValue()));
        shipping.setOriginBranch(request.getOriginBranch());
        shipping.setDestinationBranch(request.getDestinationBranch());
        shipping.setTariff(calculateTariff(
                request.getWeightKg(),
                request.getDeclaredValue(),
                request.getOriginBranch(),
                request.getDestinationBranch()
        ));
        return toResponse(repository.save(shipping));
    }

    public void deleteShipping(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Envio no encontrado");
        }
        repository.deleteById(id);
    }

    public List<ShippingResponse> find(String search, String branch, String status) {
        List<Shipping> shippings;

        if (hasText(search)) {
            shippings = repository.searchByText(search);
        } else if (hasText(branch)) {
            String parsedStatus = hasText(status) ? ShippingStatus.valueOf(status).name() : null;
            shippings = repository.findByBranchAndStatus(branch, parsedStatus);
        } else {
            shippings = repository.findAll();
        }

        return shippings.stream().map(this::toResponse).toList();
    }

    @Transactional
    public ShippingResponse assignCategories(UUID id, AssignCategoriesRequest request) {
        Shipping shipping = findShipping(id);

        Set<PackageCategory> categories = request.getCategoryNames().stream()
                .map(this::normalizeCategory)
                .map(name -> categoryRepository.findByNameIgnoreCase(name)
                        .orElseGet(() -> categoryRepository.save(PackageCategory.builder().name(name).build())))
                .collect(Collectors.toSet());

        shipping.getCategories().addAll(categories);
        return toResponse(repository.save(shipping));
    }

    private Shipping findShipping(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Envio no encontrado"));
    }

    private String generateTrackingCode() {
        return "RC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private BigDecimal calculateTariff(ShippingRequest request) {
        return calculateTariff(
                request.getWeightKg(),
                request.getDeclaredValue(),
                request.getOriginBranch(),
                request.getDestinationBranch()
        );
    }

    private BigDecimal calculateTariff(BigDecimal weightKg, BigDecimal declaredValue, String originBranch, String destinationBranch) {
        BigDecimal weightCharge = weightKg.multiply(WEIGHT_RATE);
        BigDecimal declaredCharge = declaredValue.multiply(DECLARED_VALUE_RATE);
        BigDecimal routeCharge = routeCharge(originBranch, destinationBranch);
        return BASE_TARIFF.add(weightCharge).add(declaredCharge).add(routeCharge).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal routeCharge(String origin, String destination) {
        if (origin.equalsIgnoreCase(destination)) {
            return BigDecimal.valueOf(5);
        }

        String route = Set.of(origin.toUpperCase(), destination.toUpperCase()).stream()
                .sorted()
                .collect(Collectors.joining("-"));

        return switch (route) {
            case "AREQUIPA-LIMA" -> BigDecimal.valueOf(12);
            case "CUSCO-LIMA" -> BigDecimal.valueOf(15);
            case "AREQUIPA-CUSCO" -> BigDecimal.valueOf(10);
            default -> BigDecimal.valueOf(18);
        };
    }

    private ShippingResponse toResponse(Shipping shipping) {
        return ShippingResponse.builder()
                .id(shipping.getId())
                .trackingCode(shipping.getTrackingCode())
                .description(shipping.getDescription())
                .weightKg(shipping.getWeightKg())
                .declaredValue(shipping.getDeclaredValue())
                .tariff(shipping.getTariff())
                .originBranch(shipping.getOriginBranch())
                .destinationBranch(shipping.getDestinationBranch())
                .senderDni(shipping.getSenderDni())
                .senderName(shipping.getSenderName())
                .recipientDni(shipping.getRecipientDni())
                .recipientName(shipping.getRecipientName())
                .status(shipping.getStatus())
                .categories(shipping.getCategories() != null
                        ? shipping.getCategories().stream().map(PackageCategory::getName).collect(Collectors.toSet())
                        : Set.of())
                .createdAt(shipping.getCreatedAt())
                .updatedAt(shipping.getUpdatedAt())
                .build();
    }

    private BigDecimal scaleMoney(BigDecimal value) {
        return value != null ? value.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String normalizeCategory(String name) {
        return name.trim().toUpperCase();
    }

    private ShippingStatus parseCurrentStatus(String status) {
        try {
            return ShippingStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Estado actual invalido: " + status);
        }
    }
}
