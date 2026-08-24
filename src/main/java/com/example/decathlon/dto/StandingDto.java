package com.example.decathlon.dto;

import java.util.Map;

public record StandingDto(String name, Map<String,Integer> scores, int total) {}