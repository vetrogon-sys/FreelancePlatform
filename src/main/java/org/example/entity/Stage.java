package org.example.entity;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum  Stage {
    POSTED("Размещено"),
    IN_DEVELOPING("В разработке"),
    DECLINED("Отклонено"),
    COMPLETED("Завершено");

    private String name;
}
