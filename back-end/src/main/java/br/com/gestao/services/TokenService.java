package br.com.gestao.services;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

@Service
public class TokenService {

	private static final String SECRET_KEY = "minhaChaveSecreta";
	
	public String gerarToken(UserDetails user) {
        return JWT.create()
                .withIssuer("API-Auth")
                .withSubject(user.getUsername())
                .withClaim("role", user.getAuthorities().toString())
                .withExpiresAt(LocalDateTime.now()
						.plusDays(1)
						.toInstant(ZoneOffset.of("-03:00")))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }
	
	public String extrairUsername(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY))
                    .withIssuer("API-Auth")
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token.replace("Bearer ", ""));
            return decodedJWT.getSubject();
        } catch (JWTVerificationException e) {
            return null; // Token inválido
        }
    }

	public boolean validarToken(String token) {
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET_KEY)).withIssuer("API-Auth").build();
			verifier.verify(token.replace("Bearer ", ""));
			return true;
		} catch (JWTVerificationException e) {
			return false; // Token inválido
		}
	}
}
