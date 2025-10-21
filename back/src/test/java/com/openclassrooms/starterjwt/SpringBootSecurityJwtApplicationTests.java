package com.openclassrooms.starterjwt;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringBootSecurityJwtApplicationTests {

    @Test
    void contextLoads() {
        // Test minimal pour le contexte
    }

    @Test
    void testMain() {
        // Appelle explicitement la méthode main pour couvrir la ligne SpringApplication.run
        SpringBootSecurityJwtApplication.main(new String[]{});
    }
}
