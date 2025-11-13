package com.abhedyam.dto;

import lombok.Data;

@Data
public class CustomerSearchRequest {
    private String searchTerm;
    private Integer page = 0;
    private Integer size = 20;
    private String sortBy = "createdAt";
    private String sortDirection = "DESC";
}

