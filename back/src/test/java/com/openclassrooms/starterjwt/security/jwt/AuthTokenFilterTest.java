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

/**
 * AuthTokenFilter Unit Test Suite
 * 
 * This test file contains unit tests for the AuthTokenFilter.
 * The AuthTokenFilter is a Spring Security filter that intercepts HTTP requests to validate JWT tokens
 * and establish authentication context for authenticated users.
 * 
 * Test Coverage:
 * 
 * doFilterInternal Tests:
 * - testDoFilterInternal_WithValidToken: Tests successful JWT validation and authentication,
 *   verifies JWT is extracted from Authorization header, validated, username extracted,
 *   UserDetails loaded, Authentication set in SecurityContext, and filter chain continues
 * 
 * - testDoFilterInternal_WithInvalidToken: Tests handling of invalid JWT token,
 *   verifies authentication is not set in SecurityContext when token validation fails,
 *   confirms filter chain continues despite invalid token
 * 
 * - testDoFilterInternal_WithNoToken: Tests request without Authorization header,
 *   verifies authentication is not set when no token is present,
 *   confirms filter chain continues normally
 * 
 * - testDoFilterInternal_WithException: Tests exception handling during token validation,
 *   verifies authentication is not set when exception occurs,
 *   confirms filter chain continues even after exception (graceful degradation)
 * 
 * parseJwt Tests:
 * - testParseJwt: Tests private parseJwt() method using reflection,
 *   verifies JWT token is correctly extracted from "Bearer " prefix,
 *   tests null is returned when Authorization header is missing,
 *   tests null is returned when Authorization header has invalid format (no "Bearer " prefix)
 * 
 * Filter Responsibilities:
 * 1. Extract JWT token from Authorization header (Bearer scheme)
 * 2. Validate JWT token using JwtUtils
 * 3. Extract username from valid token
 * 4. Load UserDetails using UserDetailsServiceImpl
 * 5. Create and set Authentication in SecurityContext
 * 6. Continue filter chain execution
 * 7. Handle exceptions gracefully without blocking requests
 * 
 * Mocked Dependencies:
 * - JwtUtils: Validates JWT tokens and extracts username
 * - UserDetailsServiceImpl: Loads user details by username
 * - FilterChain: Represents remaining filter chain execution
 * 
 * Test Configuration:
 * - Uses Mockito for mocking dependencies (@Mock, @InjectMocks)
 * - Uses MockHttpServletRequest and MockHttpServletResponse for HTTP simulation
 * - Uses SecurityContextHolder for authentication context testing
 * - Uses Java Reflection to test private parseJwt() method
 * - Clears SecurityContext before each test to ensure isolation
 */

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

        authTokenFilter.doFilterInternal(request, response, filterChain);

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
