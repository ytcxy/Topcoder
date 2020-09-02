package com.ytc.community.service;

import com.ytc.community.dao.UserMapper;
import com.ytc.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    UserMapper userMapper;

    public User findById(int id){
        return userMapper.selectById(id);
    }

}
