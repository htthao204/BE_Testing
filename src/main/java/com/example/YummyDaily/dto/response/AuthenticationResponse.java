package com.example.YummyDaily.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.Builder;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationResponse {
    String token;
    boolean authenticated;

}
