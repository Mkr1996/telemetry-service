package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MachineResponse {
    private Long id;
    private String name;
    private String location;
}
