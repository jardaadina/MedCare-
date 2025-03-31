package com.medcare.service;
import com.medcare.model.User;
import com.medcare.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest
{
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;
    @Test
    public void testCheckCredentials_Success()
    {
        User mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn("encodedPassword");

        when(userRepository.findByUsername("validUser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("validPassword", "encodedPassword")).thenReturn(true);

        boolean result = userService.checkCredentials("validUser", "validPassword");

        assertTrue(result);
        verify(userRepository).findByUsername("validUser");
        verify(passwordEncoder).matches("validPassword", "encodedPassword");
    }
    @Test
    public void testCheckCredentials_InvalidUsername()
    {
        when(userRepository.findByUsername("invalidUser")).thenReturn(Optional.empty());

        boolean result = userService.checkCredentials("invalidUser", "anyPassword");

        assertFalse(result);
        verify(userRepository).findByUsername("invalidUser");
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }
    @Test
    public void testCheckCredentials_InvalidPassword()
    {
        User mockUser = mock(User.class);
        when(mockUser.getPassword()).thenReturn("encodedPassword");

        when(userRepository.findByUsername("validUser")).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("invalidPassword", "encodedPassword")).thenReturn(false);

        boolean result = userService.checkCredentials("validUser", "invalidPassword");

        assertFalse(result);
        verify(userRepository).findByUsername("validUser");
        verify(passwordEncoder).matches("invalidPassword", "encodedPassword");
    }
    @Test
    public void testExistsByUsername()
    {
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);
        when(userRepository.existsByUsername("newUser")).thenReturn(false);

        assertTrue(userService.existsByUsername("existingUser"));
        assertFalse(userService.existsByUsername("newUser"));

        verify(userRepository).existsByUsername("existingUser");
        verify(userRepository).existsByUsername("newUser");
    }
}