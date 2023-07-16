package com.example.studycirclebackend.util;

import com.example.studycirclebackend.pojo.User;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
    private ThreadLocal<User> users = new ThreadLocal<User>();

    public void setUser(User user) {
        users.set(user);
    }
    public User getUser() {
        return users.get();
    }
    public void removeUser() {
        users.remove();
    }
}
