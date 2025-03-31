package com.medcare.service;
import com.medcare.model.Doctor;
import com.medcare.repository.DoctorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

//gestioneaza medicii
@Service
public class DoctorService
{
    private final DoctorRepository doctorRepository;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository)
    {
        this.doctorRepository = doctorRepository;
    }

    //am adaugat tranzactional pentru a mentine sesiunea deschisa pentru ca nu imi rula aplicatia
    //din cauza ca se foloseau niste date care nu puteau fi folosite
    @Transactional(readOnly = true)//mai exact pt lazy collections
    public List<Doctor> findAllDoctors()
    {
        List<Doctor> doctors = doctorRepository.findAll();
        doctors.forEach(doctor -> doctor.getWorkingHours().size());//intializez orele pt fiecare doctor
        return doctors;
    }
    @Transactional(readOnly = true)
    public Optional<Doctor> findDoctorById(Long id)
    {
        Optional<Doctor> doctorOpt = doctorRepository.findById(id);
        doctorOpt.ifPresent(doctor -> doctor.getWorkingHours().size());//initializez daca doctorul este prezent
        return doctorOpt;
    }

    @Transactional
    public Doctor saveDoctor(Doctor doctor)
    {
        return doctorRepository.save(doctor);
    }

    @Transactional
    public void deleteDoctor(Long id)
    {
        doctorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Doctor> findDoctorsBySpecialization(String specialization)
    {
        List<Doctor> doctors = doctorRepository.findBySpecialization(specialization);
        doctors.forEach(doctor -> doctor.getWorkingHours().size());
        return doctors;
    }

    @Transactional(readOnly = true)
    public List<Object[]> findMostRequestedDoctors()
    {
        return doctorRepository.findMostRequestedDoctors();
    }
}