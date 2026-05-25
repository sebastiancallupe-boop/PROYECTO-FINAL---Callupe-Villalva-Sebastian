package com.rapidocourier.ms_customer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReniecResponse {
    @JsonProperty("first_name")
    private String nombres;

    @JsonProperty("first_last_name")
    private String apellidoPaterno;

    @JsonProperty("second_last_name")
    private String apellidoMaterno;

    @JsonProperty("document_number")
    private String numeroDocumento;
}
