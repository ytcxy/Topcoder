package com.ytc.community;

import com.ytc.community.dao.AlphaDao;
import com.ytc.community.dao.DiscussPostMapper;
import com.ytc.community.dao.UserMapper;
import com.ytc.community.entity.DiscussPost;
import com.ytc.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = DemoApplication.class)
@RunWith(SpringRunner.class)
public class MapperTest {
    @Autowired
    UserMapper userMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;
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


}
