package com.medcare.repository;
import com.medcare.model.MedicalService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

//managemant pentru serviciu medical
@Repository
public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long>
{
    //metoda pentru a agasi care este cel mai solicitat serviciu
    @Query("SELECT ms.id, ms.name, COUNT(a) as appointmentCount FROM MedicalService ms JOIN Appointment a ON a.medicalService.id = ms.id GROUP BY ms.id, ms.name ORDER BY appointmentCount DESC")
    List<Object[]> findMostRequestedServices();
}
