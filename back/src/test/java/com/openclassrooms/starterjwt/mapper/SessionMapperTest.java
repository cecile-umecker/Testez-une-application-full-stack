package com.openclassrooms.starterjwt.mapper;

import com.openclassrooms.starterjwt.dto.SessionDto;
import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.services.TeacherService;
import com.openclassrooms.starterjwt.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * SessionMapper Unit Test Suite
 * 
 * This test file contains unit tests for the SessionMapper.
 * The SessionMapper converts between Session entities and SessionDto objects,
 * handling relationships with Teacher and User entities.
 * 
 * Test Coverage:
 * 
 * toEntity (DTO to Entity) Tests:
 * - testToEntity: Tests converting SessionDto to Session entity with complete data,
 *   verifies teacher and users are properly mapped from IDs
 * 
 * - testToEntityWithNull: Tests null DTO returns null entity
 * 
 * - testToEntityWithPartialData: Tests DTO with null teacher_id and users,
 *   verifies entity has null teacher and empty users list
 * 
 * - testToEntityList: Tests converting list of SessionDto to list of Session entities,
 *   verifies all items are mapped correctly including partial data
 * 
 * - testToEntityListWithNullOrEmpty: Tests null list returns null and empty list returns empty
 * 
 * toDto (Entity to DTO) Tests:
 * - testToDto: Tests converting Session entity to SessionDto,
 *   verifies teacher ID and user IDs are extracted correctly
 * 
 * - testToDtoWithNullOrEmpty: Tests null entity returns null DTO and empty list returns empty DTO list
 * 
 * - testToDtoList: Tests converting list of Session entities to list of SessionDto,
 *   verifies all items are mapped including entities with null teacher
 * 
 * Bidirectional Mapping Tests:
 * - testBidirectionalMapping: Tests DTO → Entity → DTO conversion preserves data integrity
 * 
 * Branch Coverage Tests:
 * - testBranchCoverage: Tests all conditional branches in mapper:
 *   • Session with null teacher returns DTO with null teacher_id
 *   • Session with teacher returns DTO with teacher_id
 *   • DTO with null users list creates entity with empty users list
 *   • DTO with empty users list creates entity with empty users list
 *   • DTO with users list including invalid ID (null user returned from service)
 * 
 * Mapper Initialization Test:
 * - testMapperIsNotNull: Verifies SessionMapper bean is properly injected
 * 
 * Mocked Dependencies:
 * - TeacherService: Provides Teacher entities by ID
 * - UserService: Provides User entities by ID
 * 
 * Test Configuration:
 * - @SpringBootTest: Loads full application context for mapper testing
 * - @MockBean: Creates mock instances of services
 * 
 * Test Data:
 * - teacher1: Mock Teacher with id=1, firstName="John", lastName="Doe"
 * - user1: Mock User with id=1, email="user1@test.com"
 * - user2: Mock User with id=2, email="user2@test.com"
 */

@SpringBootTest
class SessionMapperTest {

    @Autowired
    private SessionMapper sessionMapper;

    @MockBean
    private TeacherService teacherService;

    @MockBean
    private UserService userService;

    private Teacher teacher1;
    private User user1;
    private User user2;

    @BeforeEach
    void setup() {
        teacher1 = Teacher.builder().id(1L).firstName("John").lastName("Doe").build();
        user1 = User.builder().id(1L).email("user1@test.com").firstName("User").lastName("One").password("pwd").admin(false).build();
        user2 = User.builder().id(2L).email("user2@test.com").firstName("User").lastName("Two").password("pwd").admin(false).build();
    }

    @Test
    void testToEntity() {
        SessionDto dto = new SessionDto();
        dto.setDescription("Session Test");
        dto.setTeacher_id(teacher1.getId());
        dto.setUsers(Arrays.asList(user1.getId(), user2.getId()));

        Mockito.when(teacherService.findById(teacher1.getId())).thenReturn(teacher1);
        Mockito.when(userService.findById(user1.getId())).thenReturn(user1);
        Mockito.when(userService.findById(user2.getId())).thenReturn(user2);

        Session entity = sessionMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getDescription()).isEqualTo("Session Test");
        assertThat(entity.getTeacher()).isEqualTo(teacher1);
        assertThat(entity.getUsers()).containsExactly(user1, user2);
    }

    @Test
    void testToEntityWithNull() {
        Session entity = sessionMapper.toEntity((SessionDto) null);
        assertThat(entity).isNull();
    }

    @Test
    void testToEntityWithPartialData() {
        SessionDto dto = new SessionDto();
        dto.setTeacher_id(null);
        dto.setUsers(null);
        dto.setDescription("Partial");

        Session entity = sessionMapper.toEntity(dto);

        assertThat(entity).isNotNull();
        assertThat(entity.getDescription()).isEqualTo("Partial");
        assertThat(entity.getTeacher()).isNull();
        assertThat(entity.getUsers()).isEmpty();
    }

    @Test
    void testToEntityList() {
        SessionDto dto1 = new SessionDto();
        dto1.setDescription("S1");
        dto1.setTeacher_id(teacher1.getId());
        dto1.setUsers(Collections.singletonList(user1.getId()));

        SessionDto dto2 = new SessionDto();
        dto2.setDescription("S2");
        dto2.setTeacher_id(null);
        dto2.setUsers(Collections.singletonList(user2.getId()));

        Mockito.when(teacherService.findById(teacher1.getId())).thenReturn(teacher1);
        Mockito.when(userService.findById(user1.getId())).thenReturn(user1);
        Mockito.when(userService.findById(user2.getId())).thenReturn(user2);

        List<Session> entities = sessionMapper.toEntity(Arrays.asList(dto1, dto2));

        assertThat(entities).hasSize(2);
        assertThat(entities.get(0).getDescription()).isEqualTo("S1");
        assertThat(entities.get(0).getTeacher()).isEqualTo(teacher1);
        assertThat(entities.get(0).getUsers()).containsExactly(user1);
        assertThat(entities.get(1).getDescription()).isEqualTo("S2");
        assertThat(entities.get(1).getTeacher()).isNull();
        assertThat(entities.get(1).getUsers()).containsExactly(user2);
    }

    @Test
    void testToEntityListWithNullOrEmpty() {
        List<Session> nullList = sessionMapper.toEntity((List<SessionDto>) null);
        assertThat(nullList).isNull();

        List<Session> emptyList = sessionMapper.toEntity(Collections.emptyList());
        assertThat(emptyList).isEmpty();
    }

    @Test
    void testToDto() {
        Session session = new Session();
        session.setDescription("DTO Test");
        session.setTeacher(teacher1);
        session.setUsers(Arrays.asList(user1, user2));

        SessionDto dto = sessionMapper.toDto(session);

        assertThat(dto).isNotNull();
        assertThat(dto.getDescription()).isEqualTo("DTO Test");
        assertThat(dto.getTeacher_id()).isEqualTo(teacher1.getId());
        assertThat(dto.getUsers()).containsExactly(user1.getId(), user2.getId());
    }

    @Test
    void testToDtoWithNullOrEmpty() {
        SessionDto dto = sessionMapper.toDto((Session) null);
        assertThat(dto).isNull();

        List<SessionDto> dtoList = sessionMapper.toDto(Collections.emptyList());
        assertThat(dtoList).isEmpty();
    }

    @Test
    void testToDtoList() {
        Session s1 = new Session();
        s1.setDescription("S1");
        s1.setTeacher(teacher1);
        s1.setUsers(Collections.singletonList(user1));

        Session s2 = new Session();
        s2.setDescription("S2");
        s2.setTeacher(null);
        s2.setUsers(Collections.singletonList(user2));

        List<SessionDto> dtoList = sessionMapper.toDto(Arrays.asList(s1, s2));

        assertThat(dtoList).hasSize(2);
        assertThat(dtoList.get(0).getDescription()).isEqualTo("S1");
        assertThat(dtoList.get(0).getTeacher_id()).isEqualTo(teacher1.getId());
        assertThat(dtoList.get(0).getUsers()).containsExactly(user1.getId());
        assertThat(dtoList.get(1).getDescription()).isEqualTo("S2");
        assertThat(dtoList.get(1).getTeacher_id()).isNull();
        assertThat(dtoList.get(1).getUsers()).containsExactly(user2.getId());
    }

    @Test
    void testBidirectionalMapping() {
        SessionDto dto = new SessionDto();
        dto.setDescription("Bidirectional");
        dto.setTeacher_id(teacher1.getId());
        dto.setUsers(Arrays.asList(user1.getId(), user2.getId()));

        Mockito.when(teacherService.findById(teacher1.getId())).thenReturn(teacher1);
        Mockito.when(userService.findById(user1.getId())).thenReturn(user1);
        Mockito.when(userService.findById(user2.getId())).thenReturn(user2);

        Session entity = sessionMapper.toEntity(dto);
        SessionDto resultDto = sessionMapper.toDto(entity);

        assertThat(resultDto.getDescription()).isEqualTo(dto.getDescription());
        assertThat(resultDto.getTeacher_id()).isEqualTo(dto.getTeacher_id());
        assertThat(resultDto.getUsers()).containsExactlyElementsOf(dto.getUsers());
    }

    @Test
    void testBranchCoverage() {
        Session sessionNullTeacher = new Session();
        sessionNullTeacher.setTeacher(null);
        SessionDto dtoNullTeacher = sessionMapper.toDto(sessionNullTeacher);
        assertThat(dtoNullTeacher.getTeacher_id()).isNull();

        Teacher teacher2 = Teacher.builder().id(5L).firstName("T").lastName("Teacher").build();
        Session sessionWithTeacher = new Session();
        sessionWithTeacher.setTeacher(teacher2);
        SessionDto dtoWithTeacher = sessionMapper.toDto(sessionWithTeacher);
        assertThat(dtoWithTeacher.getTeacher_id()).isEqualTo(5L);

        SessionDto dtoUsersNull = new SessionDto();
        dtoUsersNull.setUsers(null);
        Session entityUsersNull = sessionMapper.toEntity(dtoUsersNull);
        assertThat(entityUsersNull.getUsers()).isEmpty();

        SessionDto dtoUsersEmpty = new SessionDto();
        dtoUsersEmpty.setUsers(Collections.emptyList());
        Session entityUsersEmpty = sessionMapper.toEntity(dtoUsersEmpty);
        assertThat(entityUsersEmpty.getUsers()).isEmpty();

        SessionDto dtoUsersList = new SessionDto();
        dtoUsersList.setUsers(Arrays.asList(user1.getId(), 99L)); 
        Mockito.when(userService.findById(user1.getId())).thenReturn(user1);
        Mockito.when(userService.findById(99L)).thenReturn(null);

        Session entityUsersList = sessionMapper.toEntity(dtoUsersList);
        assertThat(entityUsersList.getUsers()).containsExactly(user1, null);
    }

    @Test
    void testMapperIsNotNull() {
        assertThat(sessionMapper).isNotNull();
    }
}
