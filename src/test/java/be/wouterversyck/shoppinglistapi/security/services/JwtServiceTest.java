package be.wouterversyck.shoppinglistapi.security.services;

import be.wouterversyck.shoppinglistapi.security.utils.JwtService;
import be.wouterversyck.shoppinglistapi.security.config.SecurityProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;

public class JwtServiceTest {

    private static final String JWT_SECRET_KEY = "dddddddddddddddddddfffffffffffffffffffffffcccccccccccccccccccccceeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee";
    private JwtService jwtService;
    private SecurityProperties properties;

    @Before
    public void setup() {
        properties = new SecurityProperties();
        properties.setTokenHeader("Authorization");
        properties.setResponseTokenHeader("x-token");
        properties.setTokenPrefix("Bearer");
        properties.setTokenType("JWT");
        properties.setTokenIssuer("");
        properties.setTokenAudience("");
        jwtService = new JwtService(JWT_SECRET_KEY, properties);
    }


    // TODO add test for non alg
    // removed the current test because jwt expires obviously so need to generate a non alg token first
    private String NONE_ALG_JWT = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJub25lIn0K.eyJpc3MiOiJzZWN1cmUtYXBpIiwiYXVkIjoic2VjdXJlLWFwcCIsInN1YiI6InVzZXIiLCJleHAiOjE1NzQwOTAxNDMsInJvbGVzIjpbIlVTRVIiXX0.";

    private static final String USERNAME = "USERNAME";

    @Test(expected = MalformedJwtException.class)
    public void shouldThrowMalformedJwtException_WhenInvalidTokenIsProvided() {
        jwtService.parseToken("Bearer token");
    }

    @Test(expected = MalformedJwtException.class)
    public void shouldThrowMalformedJwtException_WhenNoTokenIsProvided() {
        jwtService.parseToken("");
    }

    @Test(expected = MalformedJwtException.class)
    public void shouldThrowMalformedJwtException_WhenTokenDoesNotStartWithPrefix() {
        jwtService.parseToken(generateRawToken());
    }

    @Test(expected = ExpiredJwtException.class)
    public void shouldThrowExpiredJwtException_WhenTokenIsExpired() {
        String token = generateExpiredToken();
        jwtService.parseToken(token);
    }

    @Test
    public void shouldCreateCorrectUsernamePasswordAuthenticationToken_WhenTokenStringIsPassed() {
        String token = generateValidToken();

        var authenticationToken = jwtService.parseToken(token);

        assertThat(authenticationToken.getPrincipal()).isEqualTo(USERNAME);
    }

    @Test
    public void shouldGenerateCorrectTokenString_WhenUserIsPassed() {
        String token = generateRawToken();

        var parsedToken = Jwts.parser()
                .setSigningKey(JWT_SECRET_KEY.getBytes())
                .parseClaimsJws(token);

        assertThat(parsedToken.getBody().getSubject()).isEqualTo(USERNAME);
    }

    private String generateRawToken() {
        return generateToken(new Date(System.currentTimeMillis() + 864000000));
    }

    private String generateValidToken() {
        String token = generateToken(new Date(System.currentTimeMillis() + 864000000));

        return format("Bearer %s", token);
    }

    private String generateExpiredToken() {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MONTH, -1);
        String token = generateToken(date.getTime());

        return format("Bearer %s", token);
    }

    private String generateToken(Date expirationDate) {
        return Jwts.builder()
                .signWith(Keys.hmacShaKeyFor(JWT_SECRET_KEY.getBytes()), SignatureAlgorithm.HS512)
                .setHeaderParam("typ", properties.getTokenType())
                .setIssuer(properties.getTokenIssuer())
                .setAudience(properties.getTokenAudience())
                .setSubject(USERNAME)
                .setExpiration(expirationDate)
                .claim("roles", Collections.emptyList())
                .compact();
    }
}
