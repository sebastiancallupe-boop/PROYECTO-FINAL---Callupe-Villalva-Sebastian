package com.rapidocourier.ms_shipping.client;

import com.rapidocourier.ms_shipping.dto.ApiResponse;
import com.rapidocourier.ms_shipping.dto.CustomerInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-customer")
public interface CustomerClient {
    @GetMapping("/api/v1/customers/exists/{dni}")
    ApiResponse<Boolean> checkCustomerExists(@PathVariable("dni") String dni);

    @GetMapping("/api/v1/customers/dni/{dni}")
    ApiResponse<CustomerInfo> getCustomerByDni(@PathVariable("dni") String dni);
}
