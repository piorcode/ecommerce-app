package com.piorcode.cart_service.api;

import com.piorcode.cart_service.api.dto.TokenRequest;
import com.piorcode.cart_service.api.dto.TokenResponse;
import com.piorcode.cart_service.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    public TokenResponse createToken(@Valid @RequestBody TokenRequest request) {
        return new TokenResponse(jwtService.generateToken(request.userId()));
    }
}
