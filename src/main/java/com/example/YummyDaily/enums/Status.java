package com.example.YummyDaily.enums;

import lombok.Getter;

@Getter
public enum Status {
    ACTIVE("Đang hoạt động"),
    DISABLED("Đã vô hiệu hóa"),
    DELETED("Đã xóa");

    private final String value;

    Status(String value) {
        this.value = value;
    }
}
