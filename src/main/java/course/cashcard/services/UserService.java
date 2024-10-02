package course.cashcard.services;

import course.cashcard.models.UserModel;
import course.cashcard.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder encoder;
    public void createUser(UserModel user){
        String pass = user.getPassword();
        //Encripting before store in database
        user.setPassword(encoder.encode(pass));
        List<String> roles = new ArrayList<>();
        roles.add("cashcard:read");
        roles.add("cashcard:write");
        user.setRoles(roles);
        repository.save(user);
    }
}