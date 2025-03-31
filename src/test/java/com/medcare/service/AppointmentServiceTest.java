package com.medcare.service;
import com.medcare.model.Appointment;
import com.medcare.model.Doctor;
import com.medcare.model.MedicalService;
import com.medcare.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceTest
{
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorService doctorService;
    @Mock
    private MedicalServiceService medicalServiceService;
    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    public void testCreateAppointment_Success()
    {
        Doctor doctor = createTestDoctor();
        MedicalService service = createTestMedicalService();
        Appointment appointment = createTestAppointment(doctor, service);

        when(doctorService.findDoctorById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(medicalServiceService.findMedicalServiceById(service.getId())).thenReturn(Optional.of(service));
        when(appointmentRepository.findByDoctorAndStartDateTimeBetween(any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        Appointment result = appointmentService.createAppointment(appointment);

        assertNotNull(result);
        assertEquals(Appointment.AppointmentStatus.NEW, result.getStatus());
        verify(appointmentRepository).save(appointment);
    }
    @Test
    public void testCreateAppointment_DoctorNotAvailable()
    {
        Doctor doctor = createTestDoctor();
        MedicalService service = createTestMedicalService();
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setMedicalService(service);
        appointment.setPatientName("Test Patient");
        LocalDateTime tuesday = LocalDateTime.now();
        while (tuesday.getDayOfWeek() != DayOfWeek.TUESDAY)
        {
            tuesday = tuesday.plusDays(1);
        }
        appointment.setStartDateTime(tuesday.with(LocalTime.of(10, 0)));
        appointment.setEndDateTime(tuesday.with(LocalTime.of(11, 0)));
        when(doctorService.findDoctorById(doctor.getId())).thenReturn(Optional.of(doctor));
        when(medicalServiceService.findMedicalServiceById(service.getId())).thenReturn(Optional.of(service));
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            appointmentService.createAppointment(appointment);
        });
        assertTrue(exception.getMessage().contains("not available"));
        verify(appointmentRepository, never()).save(any());
    }
    private Doctor createTestDoctor()
    {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Test");
        doctor.setSpecialization("Test Specialization");
        com.medcare.model.WorkingHours mondayHours =
                new com.medcare.model.WorkingHours(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));

        HashSet<com.medcare.model.WorkingHours> workingHours = new HashSet<>();
        workingHours.add(mondayHours);
        doctor.setWorkingHours(workingHours);
        return doctor;
    }
    private MedicalService createTestMedicalService()
    {
        MedicalService service = new MedicalService();
        service.setId(1L);
        service.setName("Test Service");
        service.setPrice(java.math.BigDecimal.valueOf(100));
        service.setDuration(java.time.Duration.ofMinutes(60));
        return service;
    }
    private Appointment createTestAppointment(Doctor doctor, MedicalService service)
    {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDoctor(doctor);
        appointment.setMedicalService(service);
        appointment.setPatientName("Test Patient");
        LocalDateTime monday = LocalDateTime.now();
        while (monday.getDayOfWeek() != DayOfWeek.MONDAY)
        {
            monday = monday.plusDays(1);
        }
        appointment.setStartDateTime(monday.with(LocalTime.of(10, 0)));
        appointment.setEndDateTime(monday.with(LocalTime.of(11, 0)));
        appointment.setStatus(Appointment.AppointmentStatus.NEW);
        return appointment;
    }
}