package com.medcare.service;
import com.medcare.model.MedicalService;
import com.medcare.repository.MedicalServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

//gestionez serviciile medicale aici
//operatii CRUD pentru servicii
@Service
public class MedicalServiceService
{
    private final MedicalServiceRepository medicalServiceRepository;

    @Autowired
    public MedicalServiceService(MedicalServiceRepository medicalServiceRepository)
    {
        this.medicalServiceRepository = medicalServiceRepository;
    }

    public List<MedicalService> findAllMedicalServices()
    {
        return medicalServiceRepository.findAll();
    }

    public Optional<MedicalService> findMedicalServiceById(Long id)
    {
        return medicalServiceRepository.findById(id);
    }

    public MedicalService saveMedicalService(MedicalService medicalService)
    {
        return medicalServiceRepository.save(medicalService);
    }

    public void deleteMedicalService(Long id)
    {
        medicalServiceRepository.deleteById(id);
    }

    public List<Object[]> findMostRequestedServices()
    {
        return medicalServiceRepository.findMostRequestedServices();
    }
}