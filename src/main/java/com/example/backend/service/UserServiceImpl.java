package com.example.backend.service;

import com.example.backend.config.JwtProvider;
import com.example.backend.model.User;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class UserServiceImpl implements UserService{
    private UserRepository userRepository;
    private JwtProvider jwtProvider;

    @Autowired
    public UserServiceImpl(JwtProvider jwtProvider, UserRepository userRepository) {
        this.jwtProvider = jwtProvider;
        this.userRepository = userRepository;
    }

    @Override
    public User findUserByJwtToken(String jwt) throws Exception {
        String email = jwtProvider.getEmailFromJwtToken(jwt);
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new Exception("User not found");
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);

        if(user == null){
            throw new Exception("User not found");
        }
        return user;
    }

    @Override
    public String validateToken(String verificationToken) throws Exception {

        User user = findUserByJwtToken(verificationToken);

//        Calendar calendar = Calendar.getInstance();
//
//        if(token.getExpirationTime().before(calendar.getTime())){
////            verificationTokenRepository.delete(token);
//            return "Token expired";
//        }

        user.setEnabled(true);
        userRepository.save(user);

        return "valid";
    }
}
