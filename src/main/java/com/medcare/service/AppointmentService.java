package com.medcare.service;
import com.medcare.model.Appointment;
import com.medcare.model.Doctor;
import com.medcare.model.MedicalService;
import com.medcare.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

//gestioneaza programrile
@Service
public class AppointmentService
{
    private final AppointmentRepository appointmentRepository;
    private final DoctorService doctorService;
    private final MedicalServiceService medicalServiceService;

    @Autowired
    public AppointmentService(
            AppointmentRepository appointmentRepository,
            DoctorService doctorService,
            MedicalServiceService medicalServiceService)
    {
        this.appointmentRepository = appointmentRepository;
        this.doctorService = doctorService;
        this.medicalServiceService = medicalServiceService;
    }

    public List<Appointment> findAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> findAppointmentById(Long id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> findAppointmentsByDateRange(LocalDateTime start, LocalDateTime end)
    {
        return appointmentRepository.findByStartDateTimeBetween(start, end);
    }

    public List<Appointment> findAppointmentsByPatientName(String patientName)
    {
        return appointmentRepository.findByPatientNameContainingIgnoreCase(patientName);
    }

    //creaza o programare
    public Appointment createAppointment(Appointment appointment) throws IllegalArgumentException
    {
        validateAppointment(appointment);

        appointment.setStatus(Appointment.AppointmentStatus.NEW);

        return appointmentRepository.save(appointment);
    }
    //actualizeaza statusul programarii pe care il avem ca o enumeratie interna in Appointment
    public Appointment updateAppointmentStatus(Long id, Appointment.AppointmentStatus status)
    {
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isPresent())
        {
            Appointment appointment = appointmentOpt.get();
            appointment.setStatus(status);
            return appointmentRepository.save(appointment);
        }
        else
        {
            throw new IllegalArgumentException("Appointment not found with id: " + id);
        }
    }
    //valideaza programarea in functie de
    private void validateAppointment(Appointment appointment)
    {
        Doctor doctor = appointment.getDoctor();
        MedicalService service = appointment.getMedicalService();
        LocalDateTime startDateTime = appointment.getStartDateTime();
        LocalDateTime endDateTime = appointment.getEndDateTime();

        //daca doctorul exista in clinica noastra
        if (doctor == null || !doctorService.findDoctorById(doctor.getId()).isPresent())
        {
            throw new IllegalArgumentException("Doctor does not exist");
        }
        //serviciu exista
        if (service == null || !medicalServiceService.findMedicalServiceById(service.getId()).isPresent())
        {
            throw new IllegalArgumentException("Medical service does not exist");
        }

        DayOfWeek dayOfWeek = startDateTime.getDayOfWeek();
        LocalTime startTime = startDateTime.toLocalTime();
        LocalTime endTime = endDateTime.toLocalTime();

        //daca doctorul dorit este disponibil in ziua dorita la ora dorita
        if (!doctor.isAvailable(dayOfWeek, startTime, endTime))
        {
            throw new IllegalArgumentException("Doctor is not available at the selected time");
        }

        List<Appointment> conflictingAppointments = appointmentRepository.findByDoctorAndStartDateTimeBetween(
                doctor,
                startDateTime.minusMinutes(1),
                endDateTime.plusMinutes(1)
        );

        if (!conflictingAppointments.isEmpty())
        {
            throw new IllegalArgumentException("The doctor already has an appointment at the selected time");
        }
    }
}

