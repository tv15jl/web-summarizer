package com.websummarizer.Web.Summarizer.services;

import com.websummarizer.Web.Summarizer.model.User;
import com.websummarizer.Web.Summarizer.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserRepo userRepo;

    @Override
    public User createUser(User user) {
        return userRepo.save(user);
    }

    @Override
    public void resetPassword(User user) {
        userRepo.setPassword(user.getPassword(), user.getEmail());
    }

    @Override
    public User getUserByPasswordResetToken(String token){
        return userRepo.getUserByResetToken(token);
    }
    @Override
    public int setPasswordResetToken(String token, User user){
        return userRepo.setRequestToken(token, user.getEmail());
    }
}
