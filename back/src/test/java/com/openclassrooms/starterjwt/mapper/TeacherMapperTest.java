package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.models.Teacher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TeacherMapperTest {

    @Autowired
    private TeacherMapper teacherMapper;

    @Test
    void testToEntity() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        TeacherDto dto = new TeacherDto(
                1L,
                "Doe",
                "John",
                now,
                now
        );

        // Act
        Teacher entity = teacherMapper.toEntity(dto);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(dto.getId());
        assertThat(entity.getLastName()).isEqualTo(dto.getLastName());
        assertThat(entity.getFirstName()).isEqualTo(dto.getFirstName());
        assertThat(entity.getCreatedAt()).isEqualTo(dto.getCreatedAt());
        assertThat(entity.getUpdatedAt()).isEqualTo(dto.getUpdatedAt());
    }

    @Test
    void testToEntityWithNull() {
        // Act
        Teacher entity = teacherMapper.toEntity((TeacherDto) null);

        // Assert
        assertThat(entity).isNull();
    }

    @Test
    void testToDto() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Teacher entity = Teacher.builder()
                .id(1L)
                .lastName("Doe")
                .firstName("John")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act
        TeacherDto dto = teacherMapper.toDto(entity);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(entity.getId());
        assertThat(dto.getLastName()).isEqualTo(entity.getLastName());
        assertThat(dto.getFirstName()).isEqualTo(entity.getFirstName());
        assertThat(dto.getCreatedAt()).isEqualTo(entity.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(entity.getUpdatedAt());
    }

    @Test
    void testToDtoWithNull() {
        // Act
        TeacherDto dto = teacherMapper.toDto((Teacher) null);

        // Assert
        assertThat(dto).isNull();
    }

    @Test
    void testToEntityList() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        TeacherDto dto1 = new TeacherDto(1L, "Doe", "John", now, now);
        TeacherDto dto2 = new TeacherDto(2L, "Smith", "Jane", now, now);
        List<TeacherDto> dtoList = Arrays.asList(dto1, dto2);

        // Act
        List<Teacher> entityList = teacherMapper.toEntity(dtoList);

        // Assert
        assertThat(entityList).isNotNull();
        assertThat(entityList).hasSize(2);
        assertThat(entityList.get(0).getId()).isEqualTo(1L);
        assertThat(entityList.get(0).getLastName()).isEqualTo("Doe");
        assertThat(entityList.get(0).getFirstName()).isEqualTo("John");
        assertThat(entityList.get(1).getId()).isEqualTo(2L);
        assertThat(entityList.get(1).getLastName()).isEqualTo("Smith");
        assertThat(entityList.get(1).getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testToEntityListWithNull() {
        // Act
        List<Teacher> entityList = teacherMapper.toEntity((List<TeacherDto>) null);

        // Assert
        assertThat(entityList).isNull();
    }

    @Test
    void testToEntityListWithEmptyList() {
        // Act
        List<Teacher> entityList = teacherMapper.toEntity(Collections.emptyList());

        // Assert
        assertThat(entityList).isNotNull();
        assertThat(entityList).isEmpty();
    }

    @Test
    void testToDtoList() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Teacher entity1 = Teacher.builder()
                .id(1L)
                .lastName("Doe")
                .firstName("John")
                .createdAt(now)
                .updatedAt(now)
                .build();
        Teacher entity2 = Teacher.builder()
                .id(2L)
                .lastName("Smith")
                .firstName("Jane")
                .createdAt(now)
                .updatedAt(now)
                .build();
        List<Teacher> entityList = Arrays.asList(entity1, entity2);

        // Act
        List<TeacherDto> dtoList = teacherMapper.toDto(entityList);

        // Assert
        assertThat(dtoList).isNotNull();
        assertThat(dtoList).hasSize(2);
        assertThat(dtoList.get(0).getId()).isEqualTo(1L);
        assertThat(dtoList.get(0).getLastName()).isEqualTo("Doe");
        assertThat(dtoList.get(0).getFirstName()).isEqualTo("John");
        assertThat(dtoList.get(1).getId()).isEqualTo(2L);
        assertThat(dtoList.get(1).getLastName()).isEqualTo("Smith");
        assertThat(dtoList.get(1).getFirstName()).isEqualTo("Jane");
    }

    @Test
    void testToDtoListWithNull() {
        // Act
        List<TeacherDto> dtoList = teacherMapper.toDto((List<Teacher>) null);

        // Assert
        assertThat(dtoList).isNull();
    }

    @Test
    void testToDtoListWithEmptyList() {
        // Act
        List<TeacherDto> dtoList = teacherMapper.toDto(Collections.emptyList());

        // Assert
        assertThat(dtoList).isNotNull();
        assertThat(dtoList).isEmpty();
    }

    @Test
    void testToEntityWithPartialData() {
        // Arrange
        TeacherDto dto = new TeacherDto();
        dto.setId(1L);
        dto.setFirstName("John");

        // Act
        Teacher entity = teacherMapper.toEntity(dto);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(1L);
        assertThat(entity.getFirstName()).isEqualTo("John");
        assertThat(entity.getLastName()).isNull();
        assertThat(entity.getCreatedAt()).isNull();
        assertThat(entity.getUpdatedAt()).isNull();
    }

    @Test
    void testToDtoWithPartialData() {
        // Arrange
        Teacher entity = Teacher.builder()
                .id(1L)
                .firstName("John")
                .build();

        // Act
        TeacherDto dto = teacherMapper.toDto(entity);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getFirstName()).isEqualTo("John");
        assertThat(dto.getLastName()).isNull();
        assertThat(dto.getCreatedAt()).isNull();
        assertThat(dto.getUpdatedAt()).isNull();
    }

    @Test
    void testBidirectionalMapping() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Teacher originalEntity = Teacher.builder()
                .id(1L)
                .lastName("Doe")
                .firstName("John")
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act
        TeacherDto dto = teacherMapper.toDto(originalEntity);
        Teacher resultEntity = teacherMapper.toEntity(dto);

        // Assert
        assertThat(resultEntity).isNotNull();
        assertThat(resultEntity.getId()).isEqualTo(originalEntity.getId());
        assertThat(resultEntity.getLastName()).isEqualTo(originalEntity.getLastName());
        assertThat(resultEntity.getFirstName()).isEqualTo(originalEntity.getFirstName());
        assertThat(resultEntity.getCreatedAt()).isEqualTo(originalEntity.getCreatedAt());
        assertThat(resultEntity.getUpdatedAt()).isEqualTo(originalEntity.getUpdatedAt());
    }

    @Test
    void testMapperIsNotNull() {
        assertThat(teacherMapper).isNotNull();
    }
}