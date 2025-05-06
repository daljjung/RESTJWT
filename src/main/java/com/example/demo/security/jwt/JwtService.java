package com.example.demo.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtService {
    public static final String SECRET = "ccc75dd8dd6cfe9c24e4bca58c240a4bbcb2e2e1ccc295f90f4fe5f5e5947850ac63ab05862fd212809e8a0e916de804c0c1f50190c0dcf51f30b568e639b6a5be375c7e1a8eea69e972e4f23c6c488b03797d6a3e74636c45d9b7c1554d0756846a8f40537c3cff26ce04bb37ceeceaf72eb2ed288237dce94041e30fb63080b460c9367eab474e2cde5b853e9112d8ed7142645ab43c42cba6d909279fd6cb0661b6cdb036d0b876218c4343044f8a9750f043d00d3fef9048c347e4de8e2f246c5c2b48b7998164ddb6ae6ab6d640275ceb48faa4d941b683aa909d3b6f76bbb25fb0b8a2f668c5b9d0cf11d01035a116e5d6a1dbcfd6bd451ce3d20a3870";
    public static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;
    public static final int ACCESS_EXPIRE = 3600;

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            throw new AuthenticationCredentialsNotFoundException(
                    "JWT was exprired or incorrect",
                    ex.fillInStackTrace());
        }
    }

    public String generateToken(String userName){

        System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@generateToken");

        // ACCESS_EXPIRE 3600초 => 60분
        Date exprireDate = Date.from(Instant.now().plusSeconds(ACCESS_EXPIRE));

        return Jwts.builder()
                .signWith(KEY, ALGORITHM)
                .subject(userName)
                .issuedAt(new Date())
                .expiration(exprireDate)
                .compact();
    }
}
