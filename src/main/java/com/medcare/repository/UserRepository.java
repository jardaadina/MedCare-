package com.medcare.repository;
import com.medcare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

//managemant pentru clasa user
@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    //metoda pentru a gasi un utilizator dupa nume
    Optional<User> findByUsername(String username);
    //si metoda care verifica daca exista un username sau nu
    boolean existsByUsername(String username);
}
