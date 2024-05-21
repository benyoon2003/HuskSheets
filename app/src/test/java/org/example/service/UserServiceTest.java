package org.example.service;

import org.example.model.AppUser;
import org.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private AppUser user;

    @BeforeEach
    public void setUp() {
        user = new AppUser();
        user.setUsername("testUser");
        user.setPassword("testPassword");
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        userService.registerUser(user);

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testRegisterUserFailure() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerUser(user);
        });

        assertEquals("Username already exists!", exception.getMessage());
        verify(userRepository, never()).save(user);
    }

    @Test
    public void testFindUserByUsername() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        AppUser foundUser = userService.findUserByUsername("testUser");

        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
        verify(userRepository, times(1)).findById("testUser");
    }

    @Test
    public void testFindUserByUsernameNotFound() {
        when(userRepository.findById(anyString())).thenReturn(Optional.empty());

        AppUser foundUser = userService.findUserByUsername("nonexistentUser");

        assertNull(foundUser);
        verify(userRepository, times(1)).findById("nonexistentUser");
    }
}
