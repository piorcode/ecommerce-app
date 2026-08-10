package com.piorcode.cart_service.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    
    private final Algorithm algorithm;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.algorithm = Algorithm.HMAC256(jwtProperties.secret());
    }

    public String generateToken(String userId) {
        Instant now = Instant.now();

        return JWT
            .create()
            .withSubject(userId)
            .withIssuer(jwtProperties.issuer())
            .withIssuedAt(now)
            .withExpiresAt(now.plusSeconds(jwtProperties.expirationMinutes() * 60))
            .sign(algorithm);
    }

    public String validateAndGetUserId(String token) {
        DecodedJWT decodedJWT = JWT
            .require(algorithm)
            .withIssuer(jwtProperties.issuer())
            .build()
            .verify(token);

        return decodedJWT.getSubject();
    }
}
