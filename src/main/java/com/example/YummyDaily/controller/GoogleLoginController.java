package com.example.YummyDaily.controller;

import com.example.YummyDaily.dto.response.ApiResponse;
import com.example.YummyDaily.dto.response.AuthenticationResponse;
import com.example.YummyDaily.service.GoogleAuthService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth/google")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GoogleLoginController {
    GoogleAuthService googleAuthService;

    @PostMapping("/login")
    public ApiResponse<AuthenticationResponse> googleLogin(@RequestBody Map<String, String> payload) {
        String idToken = payload.get("idToken");

        // Kiểm tra idToken có tồn tại không
        if (idToken == null || idToken.isEmpty()) {
            return ApiResponse.<AuthenticationResponse>builder()
                    .result(null)
                    .code(400)
                    .message("Google ID Token is missing")
                    .build();
        }

        try {
            // Gọi service để xác thực ID Token và tạo JWT
            String jwtToken = googleAuthService.authenticateAndGenerateJwt(idToken);

            // Trả về thông tin xác thực (JWT)
            AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                    .token(jwtToken)
                    .authenticated(true)
                    .build();

            return ApiResponse.<AuthenticationResponse>builder()
                    .result(authenticationResponse)
                    .code(200)
                    .message("Google login successful")
                    .build();
        } catch (Exception e) {
            // Xử lý lỗi khi xác thực không thành công
            return ApiResponse.<AuthenticationResponse>builder()
                    .result(null)
                    .code(400)
                    .message("Invalid Google ID Token: " + e.getMessage())
                    .build();
        }
    }

}
