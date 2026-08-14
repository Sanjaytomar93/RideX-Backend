package com.ridex.security;

import com.ridex.entity.Driver;
import com.ridex.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    public static final String ACCOUNT_TYPE_USER = "USER";
    public static final String ACCOUNT_TYPE_DRIVER = "DRIVER";

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("accountId", user.getId());
        claims.put("accountType", ACCOUNT_TYPE_USER);
        claims.put("role", user.getRole().name());

        return buildToken(claims, user.getMobileNumber());
    }

    public String generateToken(Driver driver) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("accountId", driver.getId());
        claims.put("accountType", ACCOUNT_TYPE_DRIVER);

        return buildToken(claims, driver.getMobileNumber());
    }

    private String buildToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractAccountType(String token) {
        return extractClaim(token, claims -> claims.get("accountType", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isTokenValid(String token, User user) {
        return ACCOUNT_TYPE_USER.equals(extractAccountType(token))
                && extractUsername(token).equals(user.getMobileNumber())
                && !isTokenExpired(token);
    }

    public boolean isTokenValid(String token, Driver driver) {
        return ACCOUNT_TYPE_DRIVER.equals(extractAccountType(token))
                && extractUsername(token).equals(driver.getMobileNumber())
                && !isTokenExpired(token);
    }
}
