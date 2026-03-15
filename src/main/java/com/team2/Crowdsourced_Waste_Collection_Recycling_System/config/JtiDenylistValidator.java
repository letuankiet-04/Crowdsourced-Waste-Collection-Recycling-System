package com.team2.Crowdsourced_Waste_Collection_Recycling_System.config;

import com.team2.Crowdsourced_Waste_Collection_Recycling_System.service.security.TokenDenylistService;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;

public class JtiDenylistValidator implements OAuth2TokenValidator<Jwt> {
    private final TokenDenylistService tokenDenylistService;

    public JtiDenylistValidator(TokenDenylistService tokenDenylistService) {
        this.tokenDenylistService = tokenDenylistService;
    }

    @Override
    public OAuth2TokenValidatorResult validate(Jwt token) {
        String jti = token.getId();
        if (tokenDenylistService.isTokenRevoked(jti, token.getExpiresAt())) {
            return OAuth2TokenValidatorResult.failure(new OAuth2Error("invalid_token", "revoked", null));
        }
        return OAuth2TokenValidatorResult.success();
    }
}

