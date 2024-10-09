package com.huiun.fizzybudget.userservice.unit.controller;

import com.huiun.fizzybudget.common.entity.Role;
import com.huiun.fizzybudget.common.entity.User;
import com.huiun.fizzybudget.common.security.JWTAuthenticationFilter;
import com.huiun.fizzybudget.common.security.JWTTokenProvider;
import com.huiun.fizzybudget.userservice.controller.UserController;
import com.huiun.fizzybudget.userservice.service.UserService;
import com.huiun.fizzybudget.userservice.utility.TestEntityFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
public class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JWTTokenProvider jwtTokenProvider;

    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    private User testUser;
    private Role userRole;
    private Role managerRole;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        // create default user role
        List<Role> roles = TestEntityFactory.createDefaultRoles();
        userRole = roles.get(0);
        managerRole = roles.get(1);

        testUser = TestEntityFactory.createDefaultUser(roles);
    }

    @Test
    public void testGetUserByUserId_UserExists_ReturnUser() throws Exception {
        when(userService.findUserByUserId(testUser.getId())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("123");

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        // maybe also verify the content of the json
    }

    @Test
    public void testGetUserByUserId_UserNotFound_ReturnsNotFound() throws Exception {
        when(userService.findUserByUserId(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound());
    }
}
