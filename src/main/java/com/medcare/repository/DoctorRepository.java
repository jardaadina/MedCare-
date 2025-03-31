package com.medcare.repository;
import com.medcare.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

//management pentru entitatile doctor
@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long>
{
    List<Doctor> findBySpecialization(String specialization);
    //metoda pentru gasirea celor mai solicitati doctori
    @Query("SELECT d.id, d.name, COUNT(a) as appointmentCount FROM Doctor d JOIN Appointment a ON a.doctor.id = d.id GROUP BY d.id, d.name ORDER BY appointmentCount DESC")
    List<Object[]> findMostRequestedDoctors();
}

