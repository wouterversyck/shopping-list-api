package be.wouterversyck.shoppinglistapi.security.filters;

import be.wouterversyck.shoppinglistapi.security.models.LoginRequest;
import be.wouterversyck.shoppinglistapi.security.utils.SecurityConstants;
import be.wouterversyck.shoppinglistapi.security.services.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(final AuthenticationManager authenticationManager, final JwtService jwtService) {
        setFilterProcessesUrl(SecurityConstants.AUTH_LOGIN_URL);
        setAuthenticationManager(authenticationManager);
        setPostOnly(true);

        this.jwtService = jwtService;
    }

    @Override
    public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response) throws AuthenticationException {
        final LoginRequest loginRequest = getLoginRequestFromHttpRequest(request);

        return getAuthenticationManager().authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword(),
                        Collections.emptyList())
        );
    }

    @Override
    protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
                                            final FilterChain filterChain, final Authentication authentication) {
        final var user = ((User) authentication.getPrincipal());
        final String token = jwtService.generateToken(user);
        response.addHeader(SecurityConstants.RESPONSE_TOKEN_HEADER, token);
    }

    private LoginRequest getLoginRequestFromHttpRequest(final HttpServletRequest request) {
        try {
            return new ObjectMapper()
                    .readValue(request.getInputStream(), LoginRequest.class);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
