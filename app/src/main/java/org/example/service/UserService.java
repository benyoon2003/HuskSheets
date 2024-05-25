package org.example.service;

import org.example.model.AppUser;
import org.example.model.Spreadsheet;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public void registerUser(AppUser user) throws Exception {
        logger.info("Attempting to register user: {}", user.getUsername());

        if (userRepository.findById(user.getUsername()).isPresent()) {
            logger.error("Username already exists: {}", user.getUsername());
            throw new Exception("Username already exists!");
        }

        userRepository.save(user);
        logger.info("User registered successfully: {}", user.getUsername());
    }

    public AppUser findUserByUsername(String username) {
        logger.info("Looking for user by username: {}", username);
        return userRepository.findById(username).orElse(null);
    }

    public List<AppUser> getPublishers() {
        logger.info("Getting all users");
        List<AppUser> users = userRepository.findAll();

        for(AppUser u : users){
            if(u.getPublished().size() <= 0){
                users.remove(u);
            }
        }
        return users;
    }

    public List<Spreadsheet> getSheets(AppUser user) throws Exception {
        logger.info("Attempting to find all sheets by: {}", user.getUsername());

        if (!userRepository.findById(user.getUsername()).isPresent()) {
            logger.error("Username does not exist: {}", user.getUsername());
            throw new Exception("Username not found");
        }

        List<Spreadsheet> sheets = new ArrayList<Spreadsheet>();

        for(Spreadsheet s : user.getPublished()){
            sheets.add(s);
        }

        for(Spreadsheet s : user.getSubscribed()){
            sheets.add(s);
        }
        return sheets;
    }
}
