package com.medcare.repository;
import com.medcare.model.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//managemantul pentru entitatile administrator
@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long> {}