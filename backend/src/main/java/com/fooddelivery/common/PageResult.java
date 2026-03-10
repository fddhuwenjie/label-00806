package com.fooddelivery.common;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private Long total;
    private List<T> records;

    public static <T> PageResult<T> of(Long total, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(total);
        result.setRecords(records);
        return result;
    }
}
