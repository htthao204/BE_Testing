package com.example.YummyDaily.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Difficultylevel {
    EASY("Dễ"),
    MEDIUM("Trung bình"),
    HARD("Khó");

    String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Difficultylevel fromValue(String value) {
        for (Difficultylevel level : Difficultylevel.values()) {
            if (level.value.equalsIgnoreCase(value) || level.name().equalsIgnoreCase(value)) {
                return level;
            }
        }
        throw new IllegalArgumentException("Không hợp lệ: " + value);
    }
}
