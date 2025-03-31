package com.medcare.repository;
import com.medcare.model.Appointment;
import com.medcare.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

//managemantul pentru programarile facute la policlinica
//metode scrise in clasa appointment din pachetul model si apelate aici
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long>
{
    //metoda de cautare a mediciului
    List<Appointment> findByDoctorAndStartDateTimeBetween(Doctor doctor, LocalDateTime start, LocalDateTime end);
    //metoda pentru intervalul de timp dorit
    List<Appointment> findByStartDateTimeBetween(LocalDateTime start, LocalDateTime end);
    //metoda pentru numele pacientului
    List<Appointment> findByPatientNameContainingIgnoreCase(String patientName);
}