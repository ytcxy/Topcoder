package com.ytc.community.service;

import com.ytc.community.dao.AlphaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Service
public class AlphaService {

    @Autowired
    private AlphaDao alphaDao;

    public String find(){
        return alphaDao.select();
    }

    public AlphaService(){
//        System.out.println("Alpha Model");
    }
//    @PostConstruct
    public void init(){
        System.out.println("init Alpha");
    }

//    @PreDestroy
    public void destroy(){
        System.out.println("destroy Alpha");
    }
}
