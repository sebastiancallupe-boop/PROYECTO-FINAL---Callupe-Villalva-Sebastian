package com.rapidocourier.ms_shipping.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class AssignCategoriesRequest {
    @NotEmpty(message = "Debe enviar al menos una categoria")
    @Size(max = 5, message = "No se pueden asignar mas de 5 categorias")
    private List<@NotEmpty(message = "La categoria no puede estar vacia") String> categoryNames;

    public List<String> getCategoryNames() { return categoryNames; }
    public void setCategoryNames(List<String> categoryNames) { this.categoryNames = categoryNames; }
}
