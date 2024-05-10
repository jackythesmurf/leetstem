package au.edu.sydney.elec5619.leetstem.service.jwt.impl;

import au.edu.sydney.elec5619.leetstem.constant.JwtAuthorisedAction;
import au.edu.sydney.elec5619.leetstem.service.jwt.StatefulJwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

@Service
public class SimpleStatefulJwtService implements StatefulJwtService {
    private final Key key;
    private final JwtParser parser;
    private final Map<Long, List<String>> invalidatedTokensByExpiration;
    private final Set<String> invalidatedTokens;

    public SimpleStatefulJwtService(@Value("${leetstem.jwt.secret}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.parser = Jwts.parserBuilder().setSigningKey(key).build();
        this.invalidatedTokensByExpiration = new TreeMap<>();
        this.invalidatedTokens = new HashSet<>();
    }

    @Override
    public String generateToken(String subjectId, JwtAuthorisedAction action, long expirationMillis) {
        return Jwts.builder()
                .setSubject(subjectId)
                .claim("action", action.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                .signWith(key)
                .compact();
    }

    @Override
    public String getSubjectIdIfAuthorisedTo(String token, JwtAuthorisedAction action)
            throws UnsupportedJwtException,
            MalformedJwtException,
            ExpiredJwtException,
            IllegalArgumentException {
        Jws<Claims> jwt = parser.parseClaimsJws(token);
        if (invalidatedTokens.contains(token)) {
            throw new ExpiredJwtException(jwt.getHeader(), jwt.getBody(), "Invalidated token");
        }
        Claims claims = jwt.getBody();
        if (action.equals(JwtAuthorisedAction.valueOf(claims.get("action").toString()))) {
            return claims.getSubject();
        }
        return null;
    }

    @Override
    public void invalidateToken(String token) {
        try {
            Claims claims = parser.parseClaimsJws(token).getBody();
            long expiration = claims.getExpiration().getTime();
            synchronized (invalidatedTokensByExpiration) {
                if (!invalidatedTokensByExpiration.containsKey(expiration)) {
                    invalidatedTokensByExpiration.put(expiration, new ArrayList<>());
                }
                invalidatedTokensByExpiration.get(expiration).add(token);
            }
            synchronized (invalidatedTokens) {
                invalidatedTokens.add(token);
            }
        } catch (Exception ignored) {
            // Do nothing
        }
    }

    @Override
    public void removeExpiredInvalidTokens() {
        long now = System.currentTimeMillis();
        synchronized (invalidatedTokensByExpiration) {
            for (Long expiration : invalidatedTokensByExpiration.keySet()) {
                if (expiration < now) {
                    List<String> expiredTokens = invalidatedTokensByExpiration.get(expiration);
                    synchronized (invalidatedTokens) {
                        expiredTokens.forEach(invalidatedTokens::remove);
                    }
                }
            }
            List<Long> emptyExpirations = new ArrayList<>();
            invalidatedTokensByExpiration.forEach((expiration, tokens) -> {
                if (tokens.isEmpty()) {
                    emptyExpirations.add(expiration);
                }
            });
            emptyExpirations.forEach(invalidatedTokensByExpiration::remove);
        }
    }
}
