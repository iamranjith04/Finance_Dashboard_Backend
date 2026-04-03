package backend.Security.Component;

import org.springframework.stereotype.Component;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JWTutil {


    private static final String SECRET = "ZsWeJpuWnrRZfPi+DqzHrIjw0/SI70sOgcz7n+mb1JuSv8UdUMFxdeTpOc+06+L25sfND0Bwu6TIepxdImYPr4DuQx8uQT4ZMxoIXxorutir+LjDzdL9jZles9Dr2q2fqfbPvp3nD5NdIworiMssP7sPqoKZZg1Wq3K1UeBtPoEa4LCA/BBC4FREvtlQEgwPloRDP5Yd7mAVm1V8xjcFWHCBs02g0kFdg0r2UFyg9vgwoBaVCx4zewurpUr8BMWToTfKnQGGXyt9qcVMRVGIyylirf3WQml85OtUoPs2Z7fz6iINbAZ2sOzcLFDPUf60P/Cb+LdNkvzSdT6/DPdeyjbzxI1y+tigaCvPDmcIu/8=";

    private final SecretKey SECRET_KEY =
            Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET));

    private final long JWT_EXPIRATION = 24 * 60 * 60 * 1000;


    public String generateToken(String username, String role) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);

        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }


    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }


    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return extractedUsername.equals(username) && !isTokenExpired(token);
    }

    public boolean validateToken(String token, String username, String role) {

        final String extractedUsername = extractUsername(token);
        final String extractedRole = extractRole(token);

        return extractedUsername.equals(username)
                && extractedRole.equals(role)
                && !isTokenExpired(token);
    }
}
