package com.medcare.service;
import com.medcare.model.Doctor;
import com.medcare.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {
    @Mock
    private DoctorRepository doctorRepository;
    @InjectMocks
    private DoctorService doctorService;

    @Test
    public void testFindAllDoctors()
    {
        Doctor doctor1 = new Doctor();
        doctor1.setId(1L);
        doctor1.setName("Dr. Marita");
        doctor1.setSpecialization("Cardiology");

        Doctor doctor2 = new Doctor();
        doctor2.setId(2L);
        doctor2.setName("Dr. Jarda");
        doctor2.setSpecialization("Neurology");

        List<Doctor> mockDoctors = Arrays.asList(doctor1, doctor2);

        when(doctorRepository.findAll()).thenReturn(mockDoctors);

        List<Doctor> result = doctorService.findAllDoctors();

        assertEquals(2, result.size());
        assertEquals("Dr. Marita", result.get(0).getName());
        assertEquals("Dr. Jarda", result.get(1).getName());

        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    public void testFindDoctorById()
    {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Marita");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));
        when(doctorRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Doctor> foundDoctor = doctorService.findDoctorById(1L);
        assertTrue(foundDoctor.isPresent());
        assertEquals("Dr. Marita", foundDoctor.get().getName());

        Optional<Doctor> notFoundDoctor = doctorService.findDoctorById(2L);
        assertFalse(notFoundDoctor.isPresent());

        verify(doctorRepository, times(1)).findById(1L);
        verify(doctorRepository, times(1)).findById(2L);
    }

    @Test
    public void testSaveDoctor()
    {
        Doctor doctor = new Doctor();
        doctor.setName("Dr. Slavescu");
        doctor.setSpecialization("Pediatrics");

        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor savedDoctor = invocation.getArgument(0);
            savedDoctor.setId(1L);
            return savedDoctor;
        });
        Doctor savedDoctor = doctorService.saveDoctor(doctor);
        assertNotNull(savedDoctor);
        assertEquals(1L, savedDoctor.getId());
        assertEquals("Dr. Slavescu", savedDoctor.getName());

        verify(doctorRepository, times(1)).save(doctor);
    }
}