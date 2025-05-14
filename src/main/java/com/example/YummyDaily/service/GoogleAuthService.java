package com.example.YummyDaily.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;
import java.util.Date;

@Service
public class GoogleAuthService {


    @Value("${jwt.signerKey}")
    private String jwtSecret;

    public String authenticateAndGenerateJwt(String idTokenString) throws Exception {
        // Khởi tạo GoogleIdTokenVerifier
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList("1058050958802-3rg7a591lnk89estf6bm7pa7ngh9nct0.apps.googleusercontent.com"))
                .build();

        // Kiểm tra ID Token
        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // TODO: Kiểm tra xem người dùng đã tồn tại trong DB chưa
            // Ví dụ: userRepository.findByEmail(email);

            // Nếu người dùng không tồn tại, có thể tạo mới hoặc yêu cầu đăng ký.

            // Sinh JWT
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + 86400000); // 1 ngày

            return Jwts.builder()
                    .setSubject(email)
                    .claim("name", name)
                    .setIssuedAt(now)
                    .setExpiration(expiryDate)
                    .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS256)
                    .compact();
        } else {
            throw new IllegalArgumentException("Invalid ID Token");
        }
    }
}
