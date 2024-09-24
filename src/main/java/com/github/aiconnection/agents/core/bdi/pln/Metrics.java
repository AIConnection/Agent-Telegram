package com.github.aiconnection.agents.core.bdi.pln;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Metrics {
    private double precision;
    private double recall;
    private double f1Score;
    private double accuracy;
}