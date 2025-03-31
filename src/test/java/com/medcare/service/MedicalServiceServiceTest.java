package com.medcare.service;
import com.medcare.model.MedicalService;
import com.medcare.repository.MedicalServiceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalServiceServiceTest
{
    @Mock
    private MedicalServiceRepository medicalServiceRepository;
    @InjectMocks
    private MedicalServiceService medicalServiceService;
    @Test
    public void testFindAllMedicalServices()
    {
        MedicalService service1 = new MedicalService();
        service1.setId(1L);
        service1.setName("Consultation");
        service1.setPrice(new BigDecimal("100.00"));
        service1.setDuration(Duration.ofMinutes(30));

        MedicalService service2 = new MedicalService();
        service2.setId(2L);
        service2.setName("ECG");
        service2.setPrice(new BigDecimal("150.00"));
        service2.setDuration(Duration.ofMinutes(45));

        List<MedicalService> mockServices = Arrays.asList(service1, service2);

        when(medicalServiceRepository.findAll()).thenReturn(mockServices);

        List<MedicalService> result = medicalServiceService.findAllMedicalServices();

        assertEquals(2, result.size());
        assertEquals("Consultation", result.get(0).getName());
        assertEquals("ECG", result.get(1).getName());

        verify(medicalServiceRepository).findAll();
    }
    @Test
    public void testFindMedicalServiceById()
    {
        MedicalService service = new MedicalService();
        service.setId(1L);
        service.setName("Consultation");
        service.setPrice(new BigDecimal("100.00"));
        service.setDuration(Duration.ofMinutes(30));

        when(medicalServiceRepository.findById(1L)).thenReturn(Optional.of(service));
        when(medicalServiceRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<MedicalService> found = medicalServiceService.findMedicalServiceById(1L);
        assertTrue(found.isPresent());
        assertEquals("Consultation", found.get().getName());

        Optional<MedicalService> notFound = medicalServiceService.findMedicalServiceById(2L);
        assertFalse(notFound.isPresent());

        verify(medicalServiceRepository).findById(1L);
        verify(medicalServiceRepository).findById(2L);
    }
    @Test
    public void testSaveMedicalService()
    {
        MedicalService service = new MedicalService();
        service.setName("New Service");
        service.setPrice(new BigDecimal("200.00"));
        service.setDuration(Duration.ofMinutes(60));

        when(medicalServiceRepository.save(any(MedicalService.class))).thenAnswer(invocation -> {
            MedicalService savedService = invocation.getArgument(0);
            savedService.setId(1L);
            return savedService;
        });

        MedicalService savedService = medicalServiceService.saveMedicalService(service);

        assertNotNull(savedService);
        assertEquals(1L, savedService.getId());
        assertEquals("New Service", savedService.getName());

        verify(medicalServiceRepository).save(service);
    }
    @Test
    public void testDeleteMedicalService()
    {
        medicalServiceService.deleteMedicalService(1L);

        verify(medicalServiceRepository).deleteById(1L);
    }
    @Test
    public void testFindMostRequestedServices()
    {
        Object[] service1Data = new Object[]{1L, "Consultation", 15L};
        Object[] service2Data = new Object[]{2L, "ECG", 10L};
        List<Object[]> mockData = Arrays.asList(service1Data, service2Data);

        when(medicalServiceRepository.findMostRequestedServices()).thenReturn(mockData);

        List<Object[]> result = medicalServiceService.findMostRequestedServices();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0)[0]);
        assertEquals("Consultation", result.get(0)[1]);
        assertEquals(15L, result.get(0)[2]);

        verify(medicalServiceRepository).findMostRequestedServices();
    }
}