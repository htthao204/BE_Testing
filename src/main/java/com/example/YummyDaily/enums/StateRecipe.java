package com.example.YummyDaily.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum StateRecipe {
    PENDING("Đang chờ"),
    APPROVED("Đã duyệt"),
    REJECTED("Bị từ chối");

    String value;

}
