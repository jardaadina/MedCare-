package com.medcare.repository;
import com.medcare.model.Receptionist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//managemant pentru receptionisti
@Repository
public interface ReceptionistRepository extends JpaRepository<Receptionist, Long> {}
