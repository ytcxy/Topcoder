package com.ytc.community;

import com.ytc.community.dao.*;
import com.ytc.community.entity.DiscussPost;
import com.ytc.community.entity.LoginTicket;
import com.ytc.community.entity.Message;
import com.ytc.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = DemoApplication.class)
@RunWith(SpringRunner.class)
public class MapperTest {
    @Autowired
    UserMapper userMapper;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    DiscussPostMapper discussPostMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;
    @Test
    public void testSelectUser(){
        User user =  userMapper.selectById(101);

        System.out.println(user.toString());
        user = userMapper.selectByName("guanyu");
        System.out.println(user.toString());

//        user = userMapper.selectByEmail("nowcoder103@sina.com");
//        System.out.println(user.toString());
//
//        userMapper.updatePassword(150, "123");
//        userMapper.updateStatus(150, 1);
//        user = userMapper.selectById(150);
//        System.out.println(user);
    }
    @Test
    public void testDiscussPost(){
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0,0,10);
        for (DiscussPost post : list)
            System.out.println(post.toString());
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);

    }

    @Test
    public void testLoginTicket(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(102);
        loginTicket.setTicket("cde");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 1000*60*10));
        loginTicketMapper.insertLoginTicket(loginTicket);
        System.out.println(loginTicket);
        loginTicketMapper.updateStatus(loginTicket.getTicket(), 1);
        loginTicket = loginTicketMapper.selectByTicket(loginTicket.getTicket());
        System.out.println(loginTicket);
    }
    @Test
    public void testLoginTicketSelectId(){
        LoginTicket loginTicket = new LoginTicket();
        loginTicket = loginTicketMapper.selectByUserID(155);
        System.out.println(loginTicket);
        loginTicketMapper.updateExpired(loginTicket.getTicket(), new Date(loginTicket.getExpired().getTime() + 3600 * 24 * 1000));
        loginTicket = loginTicketMapper.selectByUserID(155);
        System.out.println(loginTicket);
    }

    @Test
    public void testSelectDiscussPostById(){
        DiscussPost post = discussPostMapper.selectDiscussPostById(109);
        System.out.println(post);
    }

    @Test
    public void testMessage(){
        List<Message> list = messageMapper.selectConversations(111, 0, 20);
//        for (Message message: list)
//            System.out.println(message.toString());
        int count = messageMapper.selectConversationCount(111);
        System.out.println(count);
        list = messageMapper.selectLetters("111_112", 0, 10);
        for (Message message:list)
            System.out.println(message.toString());
        count = messageMapper.selectLetterCount("111_112");
        System.out.println(count);
        count = messageMapper.selectLetterUnreadCount(131, "111_131");
        System.out.println(count);

    }
}
