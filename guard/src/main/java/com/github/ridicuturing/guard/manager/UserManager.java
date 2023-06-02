package com.github.ridicuturing.guard.manager;

import com.github.ridicuturing.guard.mapper.ChatUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserManager {

    @Autowired
    private ChatUserMapper userMapper;

}
