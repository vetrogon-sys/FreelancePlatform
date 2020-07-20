package org.example.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum  State {
    EXPECTATION("Ожидание"),
    REJECTED("Отказано"),
    CONFIRMED("Подтверждено");

    private String name;
}
