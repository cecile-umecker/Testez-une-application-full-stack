package com.openclassrooms.starterjwt.security.services;

import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserDetailsServiceImplTest {

    private UserRepository userRepository;
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userDetailsService = new UserDetailsServiceImpl(userRepository);
    }

    @Test
    void loadUserByUsername_UserFound_ReturnsUserDetails() {
        // Arrange
        User user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("password")
                .admin(false)
                .build();

        Mockito.when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@example.com");

        // Assert
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user.getEmail());
        assertThat(userDetails.getPassword()).isEqualTo(user.getPassword());
        assertThat(userDetails).isInstanceOf(UserDetailsImpl.class);
        UserDetailsImpl impl = (UserDetailsImpl) userDetails;
        assertThat(impl.getId()).isEqualTo(user.getId());
        assertThat(impl.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(impl.getLastName()).isEqualTo(user.getLastName());
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        // Arrange
        Mockito.when(userRepository.findByEmail("unknown@example.com"))
                .thenReturn(Optional.empty());

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown@example.com")
        );

        assertThat(exception.getMessage()).isEqualTo("User Not Found with email: unknown@example.com");
    }
}
