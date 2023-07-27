package com.cj.studycirclebackend.util;

import com.cj.studycirclebackend.pojo.User;
import org.springframework.stereotype.Component;

@Component
public class UserUtil {
    private final ThreadLocal<User> users = new ThreadLocal<>();

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
