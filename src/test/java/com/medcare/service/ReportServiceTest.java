package com.medcare.service;
import com.medcare.model.Appointment;
import com.medcare.model.Doctor;
import com.medcare.model.MedicalService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest
{
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private DoctorService doctorService;
    @Mock
    private MedicalServiceService medicalServiceService;
    @InjectMocks
    private ReportService reportService;
    @TempDir
    Path tempDir;
    @Test
    public void testGetAppointmentsReport()
    {
        LocalDateTime startDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 1, 31, 23, 59);

        Appointment appointment1 = createTestAppointment(1L, "Patient 1");
        Appointment appointment2 = createTestAppointment(2L, "Patient 2");

        List<Appointment> mockAppointments = Arrays.asList(appointment1, appointment2);

        when(appointmentService.findAppointmentsByDateRange(startDate, endDate)).thenReturn(mockAppointments);

        List<Appointment> result = reportService.getAppointmentsReport(startDate, endDate);

        assertEquals(2, result.size());
        assertEquals("Patient 1", result.get(0).getPatientName());
        assertEquals("Patient 2", result.get(1).getPatientName());

        verify(appointmentService).findAppointmentsByDateRange(startDate, endDate);
    }
    @Test
    public void testGetMostRequestedDoctorsReport()
    {
        Object[] doctor1Data = new Object[]{1L, "Dr. Smith", 15L};
        Object[] doctor2Data = new Object[]{2L, "Dr. Jones", 10L};
        List<Object[]> mockData = Arrays.asList(doctor1Data, doctor2Data);

        when(doctorService.findMostRequestedDoctors()).thenReturn(mockData);

        List<Object[]> result = reportService.getMostRequestedDoctorsReport();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0)[0]);
        assertEquals("Dr. Smith", result.get(0)[1]);
        assertEquals(15L, result.get(0)[2]);

        verify(doctorService).findMostRequestedDoctors();
    }
    @Test
    public void testGetMostRequestedServicesReport()
    {
        Object[] service1Data = new Object[]{1L, "Consultation", 20L};
        Object[] service2Data = new Object[]{2L, "ECG", 15L};
        List<Object[]> mockData = Arrays.asList(service1Data, service2Data);

        when(medicalServiceService.findMostRequestedServices()).thenReturn(mockData);

        List<Object[]> result = reportService.getMostRequestedServicesReport();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0)[0]);
        assertEquals("Consultation", result.get(0)[1]);
        assertEquals(20L, result.get(0)[2]);

        verify(medicalServiceService).findMostRequestedServices();
    }
    @Test
    public void testExportAppointmentsToCSV() throws IOException
    {
        Appointment appointment1 = createTestAppointment(1L, "Patient 1");
        Appointment appointment2 = createTestAppointment(2L, "Patient 2");

        List<Appointment> appointments = Arrays.asList(appointment1, appointment2);

        File tempFile = tempDir.resolve("appointments.csv").toFile();
        String filePath = tempFile.getAbsolutePath();

        reportService.exportAppointmentsToCSV(appointments, filePath);

        assertTrue(tempFile.exists());
        List<String> lines = Files.readAllLines(tempFile.toPath());

        assertEquals(3, lines.size());
        assertTrue(lines.get(0).contains("Patient Name"));
        assertTrue(lines.get(1).contains("Patient 1"));
        assertTrue(lines.get(2).contains("Patient 2"));
    }
    private Appointment createTestAppointment(Long id, String patientName)
    {
        Appointment appointment = new Appointment();
        appointment.setId(id);
        appointment.setPatientName(patientName);

        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("Dr. Test");
        appointment.setDoctor(doctor);

        MedicalService service = new MedicalService();
        service.setId(1L);
        service.setName("Test Service");
        appointment.setMedicalService(service);

        appointment.setStartDateTime(LocalDateTime.of(2024, 1, 15, 10, 0));
        appointment.setEndDateTime(LocalDateTime.of(2024, 1, 15, 11, 0));
        appointment.setStatus(Appointment.AppointmentStatus.NEW);

        return appointment;
    }
}