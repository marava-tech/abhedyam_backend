package com.abhedyam.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageUtil {
    
    public static Pageable createPageable(Integer page, Integer size, String sortBy, String sortDirection) {
        if (page == null || page < 0) {
            page = 0;
        }
        if (size == null || size < 1) {
            size = 20;
        }
        if (size > 100) {
            size = 100;
        }
        
        Sort sort = Sort.by(
            "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC,
            sortBy != null ? sortBy : "createdAt"
        );
        
        return PageRequest.of(page, size, sort);
    }
    
    public static Pageable createPageable(Integer page, Integer size) {
        return createPageable(page, size, null, null);
    }
}

