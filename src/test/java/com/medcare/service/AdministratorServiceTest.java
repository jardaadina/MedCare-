package com.medcare.service;
import com.medcare.model.Receptionist;
import com.medcare.repository.AdministratorRepository;
import com.medcare.repository.ReceptionistRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdministratorServiceTest
{
    @Mock
    private ReceptionistRepository receptionistRepository;
    @Mock
    private UserService userService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private AdministratorService administratorService;
    @Test
    public void testCreateReceptionist_Success() {
        Receptionist receptionist = new Receptionist();
        receptionist.setName("New Receptionist");
        receptionist.setUsername("reception1");
        receptionist.setPassword("password");

        when(userService.existsByUsername("reception1")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(receptionistRepository.save(any(Receptionist.class))).thenReturn(receptionist);

        Receptionist result = administratorService.createReceptionist(receptionist);

        assertNotNull(result);
        assertEquals("New Receptionist", result.getName());
        assertEquals("encodedPassword", result.getPassword());

        verify(userService).existsByUsername("reception1");
        verify(passwordEncoder).encode("password");
        verify(receptionistRepository).save(receptionist);
    }

    @Test
    public void testFindAllReceptionists() {
        Receptionist receptionist1 = new Receptionist();
        receptionist1.setId(1L);
        receptionist1.setName("Receptionist 1");

        Receptionist receptionist2 = new Receptionist();
        receptionist2.setId(2L);
        receptionist2.setName("Receptionist 2");

        List<Receptionist> mockReceptionists = Arrays.asList(receptionist1, receptionist2);

        when(receptionistRepository.findAll()).thenReturn(mockReceptionists);

        List<Receptionist> result = administratorService.findAllReceptionists();

        assertEquals(2, result.size());
        assertEquals("Receptionist 1", result.get(0).getName());
        assertEquals("Receptionist 2", result.get(1).getName());

        verify(receptionistRepository).findAll();
    }
}