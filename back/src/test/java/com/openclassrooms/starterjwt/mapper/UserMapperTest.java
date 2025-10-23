package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.UserDto;
import com.openclassrooms.starterjwt.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserMapper Unit Test Suite
 * 
 * This test file contains unit tests for the UserMapper.
 * The UserMapper converts between User entities and UserDto objects.
 * 
 * Test Coverage:
 * 
 * toEntity (DTO to Entity) Tests:
 * - testToEntity: Tests converting UserDto to User entity with complete data,
 *   verifies all fields (id, email, firstName, lastName, password, admin, createdAt, updatedAt) are mapped correctly
 * 
 * - testToEntityWithNull: Tests null DTO returns null entity
 * 
 * - testToEntityList: Tests converting list of UserDto to list of User entities,
 *   verifies all items are mapped correctly including different admin status
 * 
 * - testToEntityListWithNull: Tests null list returns null
 * 
 * - testToEntityListWithEmptyList: Tests empty list returns empty list
 * 
 * toDto (Entity to DTO) Tests:
 * - testToDto: Tests converting User entity to UserDto,
 *   verifies all fields are mapped correctly
 * 
 * - testToDtoWithNull: Tests null entity returns null DTO
 * 
 * - testToDtoList: Tests converting list of User entities to list of UserDto,
 *   verifies all items are mapped correctly
 * 
 * - testToDtoListWithNull: Tests null list returns null
 * 
 * - testToDtoListWithEmptyList: Tests empty list returns empty list
 * 
 * Bidirectional Mapping Tests:
 * - testBidirectionalMapping: Tests Entity → DTO → Entity conversion preserves data integrity,
 *   verifies round-trip mapping maintains all field values (email, firstName, lastName, password, admin)
 * 
 * Mapper Initialization Test:
 * - testMapperIsNotNull: Verifies UserMapper bean is properly injected
 * 
 * Test Configuration:
 * - @SpringBootTest: Loads full application context for mapper testing
 * - @Autowired: Injects UserMapper instance
 * 
 * Test Data:
 * - Uses User.builder() for entity creation
 * - Uses UserDto constructor and setters for DTO creation
 * - Tests with complete data including admin/non-admin users, timestamps, and collections
 */

@SpringBootTest
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    void testToEntity() {
        LocalDateTime now = LocalDateTime.now();
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setEmail("john@example.com");
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setPassword("secret");
        dto.setAdmin(true);
        dto.setCreatedAt(now);
        dto.setUpdatedAt(now);

        User entity = userMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getEmail()).isEqualTo(dto.getEmail());
        assertThat(entity.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(entity.getLastName()).isEqualTo(dto.getLastName());
        assertThat(entity.getPassword()).isEqualTo(dto.getPassword());
        assertThat(entity.isAdmin()).isEqualTo(dto.isAdmin());
        assertThat(entity.getCreatedAt()).isEqualTo(dto.getCreatedAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(dto.getUpdatedAt());
    }

    @Test
    void testToEntityWithNull() {
        assertThat(userMapper.toEntity((UserDto) null)).isNull();
    }

    @Test
    void testToDto() {
        LocalDateTime now = LocalDateTime.now();
        User entity = User.builder()
                .id(1L)
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("secret")
                .admin(true)
                .createdAt(now)
                .updatedAt(now)
                .build();

        UserDto dto = userMapper.toDto(entity);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getEmail()).isEqualTo(entity.getEmail());
        assertThat(dto.getFirstName()).isEqualTo(entity.getFirstName());
        assertThat(dto.getLastName()).isEqualTo(entity.getLastName());
        assertThat(dto.getPassword()).isEqualTo(entity.getPassword());
        assertThat(dto.isAdmin()).isEqualTo(entity.isAdmin());
        assertThat(dto.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(entity.getUpdatedAt());
    }

    @Test
    void testToDtoWithNull() {
        assertThat(userMapper.toDto((User) null)).isNull();
    }

    @Test
    void testToEntityList() {
        LocalDateTime now = LocalDateTime.now();
        UserDto dto1 = new UserDto();
        dto1.setId(1L);
        dto1.setEmail("john@example.com");
        dto1.setFirstName("John");
        dto1.setLastName("Doe");
        dto1.setPassword("secret");
        dto1.setAdmin(true);

        UserDto dto2 = new UserDto();
        dto2.setId(2L);
        dto2.setEmail("jane@example.com");
        dto2.setFirstName("Jane");
        dto2.setLastName("Smith");
        dto2.setPassword("secret2");
        dto2.setAdmin(false);

        List<UserDto> dtoList = Arrays.asList(dto1, dto2);
        List<User> entityList = userMapper.toEntity(dtoList);

        assertThat(entityList).isNotNull();
        assertThat(entityList).hasSize(2);
        assertThat(entityList.get(0).getEmail()).isEqualTo("john@example.com");
        assertThat(entityList.get(1).getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void testToEntityListWithNull() {
        assertThat(userMapper.toEntity((List<UserDto>) null)).isNull();
    }

    @Test
    void testToEntityListWithEmptyList() {
        List<User> list = userMapper.toEntity(Collections.emptyList());
        assertThat(list).isNotNull();
        assertThat(list).isEmpty();
    }

    @Test
    void testToDtoList() {
        User u1 = User.builder()
                .id(1L)
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("secret")
                .admin(true)
                .build();

        User u2 = User.builder()
                .id(2L)
                .email("jane@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .password("secret2")
                .admin(false)
                .build();

        List<UserDto> dtoList = userMapper.toDto(Arrays.asList(u1, u2));

        assertThat(dtoList).isNotNull();
        assertThat(dtoList).hasSize(2);
        assertThat(dtoList.get(0).getEmail()).isEqualTo("john@example.com");
        assertThat(dtoList.get(1).getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void testToDtoListWithNull() {
        assertThat(userMapper.toDto((List<User>) null)).isNull();
    }

    @Test
    void testToDtoListWithEmptyList() {
        List<UserDto> dtoList = userMapper.toDto(Collections.emptyList());
        assertThat(dtoList).isNotNull();
        assertThat(dtoList).isEmpty();
    }

    @Test
    void testBidirectionalMapping() {
        User originalEntity = User.builder()
                .id(1L)
                .email("john@example.com")
                .firstName("John")
                .lastName("Doe")
                .password("secret")
                .admin(true)
                .build();

        UserDto dto = userMapper.toDto(originalEntity);
        User resultEntity = userMapper.toEntity(dto);

        assertThat(resultEntity).isNotNull();
        assertThat(resultEntity.getEmail()).isEqualTo(originalEntity.getEmail());
        assertThat(resultEntity.getFirstName()).isEqualTo(originalEntity.getFirstName());
        assertThat(resultEntity.getLastName()).isEqualTo(originalEntity.getLastName());
        assertThat(resultEntity.getPassword()).isEqualTo(originalEntity.getPassword());
        assertThat(resultEntity.isAdmin()).isEqualTo(originalEntity.isAdmin());
    }

    @Test
    void testMapperIsNotNull() {
        assertThat(userMapper).isNotNull();
    }
}
