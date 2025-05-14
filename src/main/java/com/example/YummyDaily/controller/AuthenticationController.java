package com.example.YummyDaily.controller;
import com.example.YummyDaily.dto.request.AuthenticationRequest;
import com.example.YummyDaily.dto.request.IntrospectRequest;
import com.example.YummyDaily.dto.response.ApiResponse;
import com.example.YummyDaily.dto.response.AuthenticationResponse;
import com.example.YummyDaily.dto.response.IntrospectResponse;
import com.example.YummyDaily.service.AuthenticationService;
import com.fasterxml.jackson.core.io.JsonEOFException;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);
    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        log.info("Login request received for user: {}", request.getUsername());  // Log yêu cầu

        var result = authenticationService.authenticate(request);

        log.info("Generated token: {}", result.getToken());  // Log token được tạo

        return ApiResponse.<AuthenticationResponse>builder().result(result).build();
    }


    @PostMapping("/introspect")
    public ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException, JsonEOFException {

        var result = authenticationService.introspectResponse(request);  // Lấy kết quả từ introspectResponse

        ApiResponse<IntrospectResponse> response = ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();

        if (!result.isValid()) {
            // Trả về lỗi, không sử dụng ResponseEntity, chỉ trả về ApiResponse
            response.setCode(403);  // Mã lỗi 403 nếu không hợp lệ
            response.setMessage("Token không hợp lệ");
            return response;
        }

        // Trả về kết quả khi hợp lệ
        response.setCode(200);  // Mã thành công 200
        response.setMessage("Token hợp lệ");
        return response;
    }


}
