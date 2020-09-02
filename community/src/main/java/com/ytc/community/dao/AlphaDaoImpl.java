package com.ytc.community.dao;

import org.springframework.stereotype.Repository;

@Repository("alpha") // alpha is the name of Bean, We can use this name to find this class.
public class AlphaDaoImpl implements AlphaDao{

    @Override
    public String select() {
        return "AlphaImpl";
    }

}
