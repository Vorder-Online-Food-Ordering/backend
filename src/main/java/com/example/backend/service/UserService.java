package com.example.backend.service;

import com.example.backend.model.User;

public interface UserService {
     User findUserByJwtToken(String jwt) throws Exception;
     User findUserByEmail(String email) throws Exception;

    String validateToken(String verificationToken) throws Exception;

}
