package com.abhedyam.dto;

import lombok.Data;

@Data
public class ProductSearchRequest {
    private String searchTerm;
    private Boolean isActive;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}

