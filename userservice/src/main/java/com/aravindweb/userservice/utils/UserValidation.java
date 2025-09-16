package com.aravindweb.userservice.utils;

import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.aravindweb.userservice.entities.User;
import com.aravindweb.userservice.exceptions.InvalidFieldException;
import com.aravindweb.userservice.exceptions.UserAlreadyExistsException;
import com.aravindweb.userservice.exceptions.UserNotFoundException;
import com.aravindweb.userservice.exceptions.UserServiceException;
import com.aravindweb.userservice.repos.UserRepository;

@Component
public class UserValidation {

    @Autowired
    UserRepository userRepo;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public void validateAdd(User user) throws UserServiceException{
        if(user.getId()!=null) {user.setId(null);}
        if(!StringUtils.hasText(user.getEmail()) || !Pattern.matches(EMAIL_REGEX, user.getEmail())) throw new InvalidFieldException("Invalid Email!");
        if(!StringUtils.hasText(user.getCountry())) throw new InvalidFieldException("Invalid Country!");
        Optional<User> userDb = userRepo.findByEmail(user.getEmail());
        if(!userDb.isEmpty()) throw new UserAlreadyExistsException("User Already Exists!");
         
    }

    public User validateUpdate(User user) throws UserServiceException{
        if(StringUtils.hasText(user.getEmail())) throw new InvalidFieldException("Email Update Not Allowed!");
        User userDb = userRepo.findById(user.getId()).orElseThrow(()->new UserNotFoundException("User Not Found!"));
        return userDb;
    }
}
