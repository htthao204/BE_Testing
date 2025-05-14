package com.example.YummyDaily.service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
public class JwtUtil {
    private static final String SECRET_KEY = "mySecretKey"; // Nên lưu trong biến môi trường

    public static String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 giờ
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
}
