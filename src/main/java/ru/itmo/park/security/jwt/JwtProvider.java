package ru.itmo.park.security.jwt;

import io.jsonwebtoken.*;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.itmo.park.model.entity.UserModel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class JwtProvider {

    @Value("$(jwt.secret)")
    private String jwtSecret;

    public String generateToken(UserModel user) {
        Date date = Date.from(LocalDate.now().plusDays(15).atStartOfDay(ZoneId.systemDefault()).toInstant());
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("email", user.getEmail());
        map.put("role", user.getRole().getName());
        return Jwts.builder()
                .addClaims(map)
                .setExpiration(date)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException expEx) {
            log.info("Token expired");
        } catch (UnsupportedJwtException unsEx) {
            log.info("Unsupported jwt");
        } catch (MalformedJwtException mjEx) {
            log.info("Malformed jwt");
        } catch (SignatureException sEx) {
            log.info("Invalid signature");
        } catch (Exception e) {
            log.info("invalid token");
        }
        return false;
    }

    public Integer getCurrentUser(String token) {
        token = token.replace("Bearer ", "");
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        JSONObject jsonObject = new JSONObject(claims);
        return jsonObject.getInt("userId");
    }

    public String getLoginFromToken(String token) {
        token = token.replace("Bearer ", "");
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        JSONObject jsonObject = new JSONObject(claims);
        return jsonObject.getString("email");
    }
}
