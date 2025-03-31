package com.medcare.service;
import com.medcare.model.Administrator;
import com.medcare.model.Receptionist;
import com.medcare.repository.AdministratorRepository;
import com.medcare.repository.ReceptionistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

//impplementeaza logica bll
//gestioneaza administratorii si receptionistii
//creaza cauta atata admini cat si receptionisti
@Service
public class AdministratorService
{
    private final AdministratorRepository administratorRepository;
    private final ReceptionistRepository receptionistRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public AdministratorService(
            AdministratorRepository administratorRepository,
            ReceptionistRepository receptionistRepository,
            PasswordEncoder passwordEncoder,
            UserService userService)
    {
        this.administratorRepository = administratorRepository;
        this.receptionistRepository = receptionistRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }
    public Receptionist createReceptionist(Receptionist receptionist)
    {
        if (userService.existsByUsername(receptionist.getUsername()))
        {
            throw new IllegalArgumentException("Username already exists");
        }

        receptionist.setPassword(passwordEncoder.encode(receptionist.getPassword()));
        return receptionistRepository.save(receptionist);
    }
    public void deleteReceptionist(Long id) {
        receptionistRepository.deleteById(id);
    }
    public Receptionist updateReceptionist(Receptionist receptionist)
    {
        if (receptionist.getId() == null)
        {
            throw new IllegalArgumentException("Cannot update receptionist without ID");
        }

        if (receptionist.getPassword() == null || receptionist.getPassword().isEmpty())
        {
            Optional<Receptionist> existingReceptionist = receptionistRepository.findById(receptionist.getId());
            if (existingReceptionist.isPresent())
            {
                receptionist.setPassword(existingReceptionist.get().getPassword());
            }
            else
            {
                throw new IllegalArgumentException("Receptionist with ID " + receptionist.getId() + " not found");
            }
        }
        else
        {
            receptionist.setPassword(passwordEncoder.encode(receptionist.getPassword()));
        }

        return receptionistRepository.save(receptionist);
    }
    public List<Receptionist> findAllReceptionists() {
        return receptionistRepository.findAll();
    }
}

