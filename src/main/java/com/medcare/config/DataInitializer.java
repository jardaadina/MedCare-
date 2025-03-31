package com.medcare.config;
import com.medcare.model.*;
import com.medcare.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;

//populeaz baza de date cu date initiale la porniea aplicatiei
//am creat cativa utilizatori, un admin si un receptionist 2 medici si servicii medicale
@Component
public class DataInitializer implements CommandLineRunner
{
    private final AdministratorRepository administratorRepository;
    private final ReceptionistRepository receptionistRepository;
    private final DoctorRepository doctorRepository;
    private final MedicalServiceRepository medicalServiceRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(
            AdministratorRepository administratorRepository,
            ReceptionistRepository receptionistRepository,
            DoctorRepository doctorRepository,
            MedicalServiceRepository medicalServiceRepository,
            PasswordEncoder passwordEncoder)
    {
        this.administratorRepository = administratorRepository;
        this.receptionistRepository = receptionistRepository;
        this.doctorRepository = doctorRepository;
        this.medicalServiceRepository = medicalServiceRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args)
    {
        if (administratorRepository.count() == 0)
        {
            Administrator admin = new Administrator();
            admin.setName("Admin User");
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            administratorRepository.save(admin);
            System.out.println("Created default admin user");
        }

        if (receptionistRepository.count() == 0)
        {
            Receptionist receptionist = new Receptionist();
            receptionist.setName("Reception Staff");
            receptionist.setUsername("reception");
            receptionist.setPassword(passwordEncoder.encode("reception123"));
            receptionistRepository.save(receptionist);
            System.out.println("Created default receptionist user");
        }

        if (doctorRepository.count() == 0)
        {
            Doctor cardiologist = new Doctor();
            cardiologist.setName("Dr. John Smith");
            cardiologist.setSpecialization("Cardiology");

            WorkingHours mondayHours = new WorkingHours(DayOfWeek.MONDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
            WorkingHours tuesdayHours = new WorkingHours(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
            WorkingHours wednesdayHours = new WorkingHours(DayOfWeek.WEDNESDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
            WorkingHours thursdayHours = new WorkingHours(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));
            WorkingHours fridayHours = new WorkingHours(DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(17, 0));

            cardiologist.setWorkingHours(new HashSet<>(Arrays.asList(
                    mondayHours, tuesdayHours, wednesdayHours, thursdayHours, fridayHours
            )));

            doctorRepository.save(cardiologist);

            Doctor neurologist = new Doctor();
            neurologist.setName("Dr. Jane Doe");
            neurologist.setSpecialization("Neurology");

            neurologist.setWorkingHours(new HashSet<>(Arrays.asList(
                    new WorkingHours(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)),
                    new WorkingHours(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(16, 0)),
                    new WorkingHours(DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(16, 0))
            )));

            doctorRepository.save(neurologist);
            System.out.println("Created default doctors");
        }

        if (medicalServiceRepository.count() == 0)
        {
            MedicalService consultation = new MedicalService();
            consultation.setName("General Consultation");
            consultation.setPrice(new BigDecimal("100.00"));
            consultation.setDuration(Duration.ofMinutes(30));
            medicalServiceRepository.save(consultation);

            MedicalService ecg = new MedicalService();
            ecg.setName("ECG");
            ecg.setPrice(new BigDecimal("150.00"));
            ecg.setDuration(Duration.ofMinutes(45));
            medicalServiceRepository.save(ecg);

            MedicalService mri = new MedicalService();
            mri.setName("MRI Scan");
            mri.setPrice(new BigDecimal("500.00"));
            mri.setDuration(Duration.ofMinutes(60));
            medicalServiceRepository.save(mri);

            System.out.println("Created default medical services");
        }
    }
}