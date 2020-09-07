package com.ytc.community.dao;

import com.ytc.community.entity.LoginTicket;
import org.apache.ibatis.annotations.*;

import javax.xml.crypto.Data;
import java.util.Date;

@Mapper
@Deprecated
public interface LoginTicketMapper {
    @Insert({
            "insert into login_ticket (user_id, ticket, status, expired) ",
            "values(#{userId}, #{ticket}, #{status}, #{expired}) "
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertLoginTicket(LoginTicket loginTicket);

    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where ticket = #{ticket} "
    })
    LoginTicket selectByTicket(String ticket);

    @Select({
            "select id, user_id, ticket, status, expired ",
            "from login_ticket where user_id = #{id} "
    })
    LoginTicket selectByUserID(int id);

    @Update({
            "update login_ticket set expired = #{expired} where ticket = #{ticket} "
    })
    int updateExpired(String ticket, Date expired);
    @Update({
            "<script> ",
            "update login_ticket set status = #{status} where ticket = #{ticket} ",
            "<if test = \"ticket!=null\"> ",
            "and 1 = 1 ",
            "</if> ",
            "</script>"
    })
    int updateStatus(String ticket, int status);
}
