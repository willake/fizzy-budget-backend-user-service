package com.huiun.fizzybudget.userservice.unit.service;

import com.huiun.fizzybudget.common.entity.Role;
import com.huiun.fizzybudget.common.entity.User;
import com.huiun.fizzybudget.userservice.exception.RoleNotFoundException;
import com.huiun.fizzybudget.userservice.exception.UserNotFoundException;
import com.huiun.fizzybudget.common.repository.RoleRepository;
import com.huiun.fizzybudget.common.repository.UserRepository;
import com.huiun.fizzybudget.userservice.service.UserServiceImpl;
import com.huiun.fizzybudget.userservice.utility.TestEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Role userRole;
    private Role managerRole;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        List<Role> roles = TestEntityFactory.createDefaultRoles();
        userRole = roles.get(0);
        managerRole = roles.get(1);

        testUser = TestEntityFactory.createDefaultUser(roles);
        testUser.getRoles().removeIf(role -> role.getRoleName().equals(managerRole.getRoleName()));
    }

    @Test
    public void testFindUserByUserId_UserExists_ReturnUser() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        Optional<User> retrievedUser = userService.findUserByUserId(testUser.getId());

        assertTrue(retrievedUser.isPresent());
        assertEquals(testUser.getId(), retrievedUser.get().getId());
    }

    @Test
    public void testFindUserByUserId_UserNotFound_ReturnEmptyOptional() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Optional<User> retrievedUser = userService.findUserByUserId(2L);

        assertFalse(retrievedUser.isPresent());
    }

    @Test
    public void testAddRoleToUser_UserAndRoleExist_Success() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(managerRole.getId())).thenReturn(Optional.of(managerRole));

        userService.addRoleToUser(testUser.getId(), managerRole.getId());

        Optional<User> retrievedUser = userRepository.findById(testUser.getId());
        assertTrue(retrievedUser.isPresent());

        User savedUser = retrievedUser.get();
        assertEquals(2, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().stream()
                .anyMatch(role -> managerRole.getRoleName().equals(role.getRoleName())));
    }

    @Test
    public void testAddRoleToUser_UserNotFound_ThrowsUserNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.addRoleToUser(999L, managerRole.getId())
        );
    }

    @Test
    public void testAddRoleToUser_RoleNotFound_ThrowsRoleNotFoundException() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));

        RoleNotFoundException exception = assertThrows(
                RoleNotFoundException.class,
                () -> userService.addRoleToUser(testUser.getId(), 999L)
        );
    }
}
