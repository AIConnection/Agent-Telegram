package com.github.connectionai.agents.core.bdi.pln;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    private String entityType;
    private String entityName;
}