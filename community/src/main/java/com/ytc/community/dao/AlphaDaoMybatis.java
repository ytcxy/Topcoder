package com.ytc.community.dao;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

@Repository
@Primary
public class AlphaDaoMybatis implements AlphaDao{
    @Override
    public String select() {
        return "MyBatis";
    }
}
