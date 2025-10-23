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