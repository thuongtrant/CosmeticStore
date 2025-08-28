package com.ttt.CosmeticStore.Util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PaginationUtil {
    public static Pageable createPageable(int page, int size, String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "id";
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if ("DESC".equalsIgnoreCase(sortDirection)) {
            direction = Sort.Direction.DESC;
        }

        // Validate sortBy field
        switch (sortBy.toLowerCase()) {
            case "price":
            case "name":
            case "createdat":
            case "id":
                break;
            default:
                sortBy = "id";
        }

        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }
    public static Pageable createPageablePS(int page, int size) {
        return PageRequest.of(page, size);
    }
}
