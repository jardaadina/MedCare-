package com.medcare.service;
import com.medcare.model.User;
import com.medcare.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

//serviciu pentru autentificarea utilizatorilor
@Service
public class UserService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    //verific credentialele si gestionez utilizatorii daca exista sau nu
    public boolean checkCredentials(String username, String rawPassword)
    {
        Optional<User> userOpt = userRepository.findByUsername(username);
        return userOpt.filter(user -> passwordEncoder.matches(rawPassword, user.getPassword())).isPresent();
    }
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}

