package com.huiun.fizzybudget.userservice.integration.service;

import com.huiun.fizzybudget.common.entity.Role;
import com.huiun.fizzybudget.common.entity.User;
import com.huiun.fizzybudget.common.repository.RoleRepository;
import com.huiun.fizzybudget.common.repository.UserRepository;
import com.huiun.fizzybudget.userservice.service.UserService;
import com.huiun.fizzybudget.userservice.utility.TestEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureMockMvc
public class UserServiceImplTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    AuthenticationManager authenticationManager;

    @Autowired
    UserService userService;

    private User testUser;

    private Role userRole;
    private Role managerRole;

    @BeforeEach
    public void setUp() {
        List<Role> roles = TestEntityFactory.createDefaultRoles();
        userRole = roleRepository.save(roles.get(0));
        managerRole = roleRepository.save(roles.get(1));

        testUser = TestEntityFactory.createDefaultUser(roles);
        testUser= userRepository.save(testUser);
    }

    @Test
    @Transactional
    public void testCreateUserIntegration() {
        User user = new User();
        user.setUsername("newUser");
        user.setPasswordHash("newUser");
        user.setEmail("newUser@gmail.com");
        user.setActivated(true);

        User createdUser = userService.createUser(user);

        Optional<User> retrievedUser = userRepository.findById(createdUser.getId());
        assertTrue(retrievedUser.isPresent());

        User savedUser = retrievedUser.get();
//        Should also create a role automatically
        assertEquals(1, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().stream()
                .anyMatch(role -> userRole.getRoleName().equals(role.getRoleName())));
    }

    @Test
    @Transactional
    public void testAddRoleToUserIntegration() {
        userService.addRoleToUser(testUser.getId(), managerRole.getId());

        Optional<User> retrievedUser = userRepository.findById(testUser.getId());
        assertTrue(retrievedUser.isPresent());

        User savedUser = retrievedUser.get();
        assertEquals(2, savedUser.getRoles().size());
        assertTrue(savedUser.getRoles().stream()
                .anyMatch(role -> managerRole.getRoleName().equals(role.getRoleName())));
    }

    @Test
    @Transactional
    public void testRemoveRoleFromUserIntegration() {
        userService.removeRoleFromUser(testUser.getId(), userRole.getId());

        Optional<User> retrievedUser = userRepository.findById(testUser.getId());
        assertTrue(retrievedUser.isPresent());

        User savedUser = retrievedUser.get();
        assertEquals(1, savedUser.getRoles().size());
    }
}
