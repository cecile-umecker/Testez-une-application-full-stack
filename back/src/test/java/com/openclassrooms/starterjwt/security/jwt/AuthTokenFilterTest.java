package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;

import javax.servlet.FilterChain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AuthTokenFilterTest {

    @InjectMocks
    private AuthTokenFilter authTokenFilter;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_WithValidToken() throws Exception {
        String token = "valid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("user@example.com")
                .password("password")
                .admin(false)
                .build();

        when(jwtUtils.validateJwtToken(token)).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(token)).thenReturn("user@example.com");
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isEqualTo(userDetails);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithInvalidToken() throws Exception {
        String token = "invalid.jwt.token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtUtils.validateJwtToken(token)).thenReturn(false);

        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithNoToken() throws Exception {
        authTokenFilter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithException() throws Exception {
        String token = "token";
        request.addHeader("Authorization", "Bearer " + token);

        when(jwtUtils.validateJwtToken(token)).thenThrow(new RuntimeException("JWT error"));

        // Le test ne doit pas lancer, juste logger et continuer
        authTokenFilter.doFilterInternal(request, response, filterChain);

        // Toujours aucun auth dans le contexte
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testParseJwt() throws Exception {
        request.addHeader("Authorization", "Bearer mytoken");
        java.lang.reflect.Method method = AuthTokenFilter.class.getDeclaredMethod("parseJwt", javax.servlet.http.HttpServletRequest.class);
        method.setAccessible(true);
        String jwt = (String) method.invoke(authTokenFilter, request);
        assertThat(jwt).isEqualTo("mytoken");

        request.removeHeader("Authorization");
        String nullJwt = (String) method.invoke(authTokenFilter, request);
        assertThat(nullJwt).isNull();

        request.addHeader("Authorization", "InvalidHeader");
        String stillNull = (String) method.invoke(authTokenFilter, request);
        assertThat(stillNull).isNull();
    }
}
