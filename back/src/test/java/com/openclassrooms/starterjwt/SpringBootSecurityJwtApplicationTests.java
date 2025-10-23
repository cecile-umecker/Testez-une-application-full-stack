package com.openclassrooms.starterjwt;

import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.security.jwt.JwtUtils;
import com.openclassrooms.starterjwt.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SpringBootSecurityJwtApplication Integration Test Suite
 * 
 * This test file contains integration tests for the main Spring Boot application.
 * Tests verify that the application context loads correctly, all critical beans are present,
 * configuration is properly injected, and database connectivity is established.
 * 
 * Test Coverage:
 * 
 * Application Context Tests:
 * - contextLoads: Tests Spring Boot application context loads without errors,
 *   verifies all auto-configuration and component scanning works correctly
 * 
 * - testMain: Tests main() method can be executed without exceptions,
 *   verifies application entry point is valid
 * 
 * Security Beans Tests:
 * - criticalSecurityBeansAreLoaded: Tests all security-related beans are present,
 *   verifies JwtUtils bean is loaded and not null,
 *   confirms UserDetailsServiceImpl bean is loaded,
 *   validates authenticationJwtTokenFilter bean exists in context
 * 
 * Repository Beans Tests:
 * - repositoryBeansAreLoaded: Tests all repository beans are autowired correctly,
 *   verifies UserRepository is not null,
 *   confirms SessionRepository is not null,
 *   validates TeacherRepository is not null
 * 
 * Configuration Tests:
 * - jwtConfigurationIsLoaded: Tests JWT configuration properties are injected,
 *   verifies jwtSecret is loaded from application.properties and is not empty,
 *   confirms jwtExpirationMs is loaded and is a positive number,
 *   validates configuration values are properly bound to JwtUtils bean
 * 
 * Database Connectivity Tests:
 * - databaseConnectionIsEstablished: Tests database connection is active,
 *   verifies UserRepository can query database (count operation),
 *   confirms SessionRepository can query database,
 *   validates TeacherRepository can query database,
 *   ensures all counts are >= 0 indicating successful connection
 * 
 * Beans Verified:
 * - JwtUtils: JWT token generation and validation utilities
 * - UserDetailsServiceImpl: Spring Security user details loading service
 * - AuthTokenFilter: JWT authentication filter (bean name: "authenticationJwtTokenFilter")
 * - UserRepository: User entity database operations
 * - SessionRepository: Session entity database operations
 * - TeacherRepository: Teacher entity database operations
 * 
 * Configuration Properties Verified:
 * - bezkoder.app.jwtSecret: JWT signing secret key
 * - bezkoder.app.jwtExpirationMs: JWT token expiration time in milliseconds
 * 
 * Test Configuration:
 * - @SpringBootTest: Loads full application context with all configurations
 * - @Autowired: Injects beans and application context for testing
 * - Uses ReflectionTestUtils to access private configuration fields in JwtUtils
 * - Uses AssertJ for fluent assertions with descriptive messages
 */

@SpringBootTest
class SpringBootSecurityJwtApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Environment environment;

    @Autowired(required = false)
    private UserRepository userRepository;

    @Autowired(required = false)
    private SessionRepository sessionRepository;

    @Autowired(required = false)
    private TeacherRepository teacherRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    void contextLoads() {
    }

    @Test
    void testMain() {
        SpringBootSecurityJwtApplication.main(new String[]{});
    }

    @Test
    void criticalSecurityBeansAreLoaded() {
        assertThat(applicationContext.getBean(JwtUtils.class))
                .as("JwtUtils bean should be loaded")
                .isNotNull();

        assertThat(applicationContext.getBean(UserDetailsServiceImpl.class))
                .as("UserDetailsServiceImpl bean should be loaded")
                .isNotNull();

        assertThat(applicationContext.containsBean("authenticationJwtTokenFilter"))
                .as("AuthTokenFilter bean should be present")
                .isTrue();
    }

    @Test
    void repositoryBeansAreLoaded() {
        assertThat(userRepository)
                .as("UserRepository should be autowired and not null")
                .isNotNull();

        assertThat(sessionRepository)
                .as("SessionRepository should be autowired and not null")
                .isNotNull();

        assertThat(teacherRepository)
                .as("TeacherRepository should be autowired and not null")
                .isNotNull();
    }

    @Test
    void jwtConfigurationIsLoaded() {
        String jwtSecret = (String) ReflectionTestUtils.getField(jwtUtils, "jwtSecret");
        Integer jwtExpirationMs = (Integer) ReflectionTestUtils.getField(jwtUtils, "jwtExpirationMs");

        assertThat(jwtSecret)
                .as("JWT secret should be injected into JwtUtils bean")
                .isNotNull()
                .isNotEmpty();

        assertThat(jwtExpirationMs)
                .as("JWT expiration time should be injected into JwtUtils bean")
                .isNotNull()
                .isPositive();
    }

    @Test
    void databaseConnectionIsEstablished() {
        long userCount = userRepository.count();
        long sessionCount = sessionRepository.count();
        long teacherCount = teacherRepository.count();

        assertThat(userCount)
                .as("UserRepository should be able to query the database")
                .isGreaterThanOrEqualTo(0);

        assertThat(sessionCount)
                .as("SessionRepository should be able to query the database")
                .isGreaterThanOrEqualTo(0);

        assertThat(teacherCount)
                .as("TeacherRepository should be able to query the database")
                .isGreaterThanOrEqualTo(0);
    }
}