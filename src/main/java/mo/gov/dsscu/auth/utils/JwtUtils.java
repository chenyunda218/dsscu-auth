package mo.gov.dsscu.auth.utils;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;

@Component
public class JwtUtils {

  @Value("${JWT_SECRET}")
  private String secret;

  public String getSecret() {
    return secret;
  }

  public String createJwt(String subject, long expirationMs) {
    return Jwts.builder()
        .signWith(getSigningKey())
        .setSubject(subject)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
        .compact();
  }

  public String createJwt(String subject, long expirationMs, Map<String, Object> cliams) {
    JwtBuilder builder = Jwts.builder()
        .signWith(getSigningKey())
        .setSubject(subject)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + expirationMs));
    for (Map.Entry<String, Object> entry : cliams.entrySet()) {
      builder.claim(entry.getKey(), entry.getValue());
    }
    return builder.compact();
  }

  public boolean validateJwt(String jwt) {
    try {
      parserBuilder()
          .build()
          .parseClaimsJws(jwt);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String extractSubject(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public Claims extractClaims(String token) {
    return extractAllClaims(token);
  }

  private Claims extractAllClaims(String token) {
    return parserBuilder()
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private JwtParserBuilder parserBuilder() {
    return Jwts.parserBuilder().setSigningKey(getSigningKey());
  }

  private Key getSigningKey() {
    return io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes());
  }
}
