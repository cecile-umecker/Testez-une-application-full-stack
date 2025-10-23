package com.openclassrooms.starterjwt.security.jwt;

import com.openclassrooms.starterjwt.security.services.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * JwtUtils Unit Test Suite
 * 
 * This test file contains unit tests for the JwtUtils class.
 * The JwtUtils class handles JWT token generation, validation, and username extraction
 * for authentication purposes in the application.
 * 
 * Test Coverage:
 * 
 * Token Generation Tests:
 * - testGenerateJwtToken: Tests JWT token generation from Authentication object,
 *   verifies token is not null/empty and contains correct username as subject
 * 
 * - testGenerateJwtToken_TokenContainsCorrectClaims: Tests generated token contains all required claims,
 *   verifies subject (username), issuedAt, expiration dates are present and valid,
 *   confirms expiration is after issuedAt timestamp
 * 
 * Username Extraction Tests:
 * - testGetUserNameFromJwtToken: Tests username extraction from valid JWT token,
 *   verifies correct username is returned from token's subject claim
 * 
 * Token Validation Tests:
 * - testValidateJwtToken_ValidToken: Tests validation succeeds for properly signed and non-expired token,
 *   verifies method returns true for valid token
 * 
 * - testValidateJwtToken_InvalidSignature: Tests validation fails when token signed with wrong secret,
 *   verifies method returns false for signature mismatch
 * 
 * - testValidateJwtToken_MalformedToken: Tests validation fails for malformed JWT string,
 *   verifies method returns false and handles MalformedJwtException
 * 
 * - testValidateJwtToken_ExpiredToken: Tests validation fails for expired token,
 *   verifies method returns false and handles ExpiredJwtException
 * 
 * - testValidateJwtToken_EmptyToken: Tests validation fails for empty string token,
 *   verifies method returns false and handles IllegalArgumentException
 * 
 * - testValidateJwtToken_NullToken: Tests validation fails for null token,
 *   verifies method returns false and handles IllegalArgumentException
 * 
 * - testValidateJwtToken_UnsupportedJwtException: Tests validation fails for unsigned JWT token,
 *   verifies method returns false and handles UnsupportedJwtException
 * 
 * - testValidateJwtToken_IllegalArgumentException: Tests validation fails for invalid argument,
 *   verifies method handles IllegalArgumentException gracefully
 * 
 * JwtUtils Methods Tested:
 * - generateJwtToken(Authentication): Creates JWT token with username, issuedAt, and expiration
 * - getUserNameFromJwtToken(String): Extracts username from token's subject claim
 * - validateJwtToken(String): Validates token signature, expiration, and format
 * 
 * Exception Handling:
 * - SignatureException: Invalid token signature
 * - MalformedJwtException: Invalid JWT format
 * - ExpiredJwtException: Token past expiration date
 * - UnsupportedJwtException: Unsupported JWT format (e.g., unsigned)
 * - IllegalArgumentException: Empty or invalid token string
 * 
 * Test Configuration:
 * - Uses Mockito for mocking Authentication (@Mock)
 * - Uses ReflectionTestUtils to inject test values for jwtSecret and jwtExpirationMs
 * - jwtSecret: "testSecretKeyForJwtTokenGenerationAndValidation"
 * - jwtExpirationMs: 3600000 (1 hour)
 * - Uses io.jsonwebtoken library for token generation and parsing
 */

class JwtUtilsTest {

    @InjectMocks
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    private String jwtSecret = "testSecretKeyForJwtTokenGenerationAndValidation";
    private int jwtExpirationMs = 3600000; 

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", jwtSecret);
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", jwtExpirationMs);
    }

    @Test
    void testGenerateJwtToken() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("test@example.com")
                .firstName("Test")
                .lastName("User")
                .password("password")
                .admin(false)
                .build();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        assertThat(token).isNotNull().isNotEmpty();

        String username = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        assertThat(username).isEqualTo("test@example.com");
    }

    @Test
    void testGetUserNameFromJwtToken() {
        String username = "test@example.com";
        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        String extractedUsername = jwtUtils.getUserNameFromJwtToken(token);
        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void testValidateJwtToken_ValidToken() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isTrue();
    }

    @Test
    void testValidateJwtToken_InvalidSignature() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, "wrongSecretKey")
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateJwtToken_MalformedToken() {
        String token = "this.is.not.a.valid.jwt.token";
        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateJwtToken_ExpiredToken() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date(System.currentTimeMillis() - 10000))
                .setExpiration(new Date(System.currentTimeMillis() - 5000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();

        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateJwtToken_EmptyToken() {
        String token = "";
        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateJwtToken_NullToken() {
        String token = null;
        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateJwtToken_UnsupportedJwtException() {
        String token = Jwts.builder()
                .setSubject("test@example.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .compact(); 

        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testValidateJwtToken_IllegalArgumentException() {
        String token = "";
        boolean isValid = jwtUtils.validateJwtToken(token);
        assertThat(isValid).isFalse();
    }

    @Test
    void testGenerateJwtToken_TokenContainsCorrectClaims() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .id(1L)
                .username("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(true)
                .build();

        when(authentication.getPrincipal()).thenReturn(userDetails);
        String token = jwtUtils.generateJwtToken(authentication);

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        assertThat(claims.getSubject()).isEqualTo("john@example.com");
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
        assertThat(claims.getExpiration().getTime()).isGreaterThan(claims.getIssuedAt().getTime());
    }
}
