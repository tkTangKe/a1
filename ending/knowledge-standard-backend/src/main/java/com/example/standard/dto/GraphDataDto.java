package com.example.standard.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class GraphDataDto {
    private List<Map<String, Object>> nodes;
    private List<Map<String, Object>> edges;
}