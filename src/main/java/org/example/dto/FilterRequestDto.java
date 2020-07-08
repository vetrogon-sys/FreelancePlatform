package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.FilterType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterRequestDto {
    private FilterType filterType;
    private String value;
}
