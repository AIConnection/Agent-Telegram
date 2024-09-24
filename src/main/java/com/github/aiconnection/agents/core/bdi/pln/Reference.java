package com.github.aiconnection.agents.core.bdi.pln;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reference {
    private String entityName;
    private String pronoun;
}