package com.ytc.community.controller;

import com.ytc.community.dao.DiscussPostMapper;
import com.ytc.community.dao.UserMapper;
import com.ytc.community.entity.DiscussPost;
import com.ytc.community.entity.Page;
import com.ytc.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    UserMapper userMapper;

    @Autowired
    DiscussPostMapper discussPostMapper;

    @GetMapping("/index")
    public String index(Model model, Page page){
        page.setRows(discussPostMapper.selectDiscussPostRows(0));
        page.setPath("index");
        System.out.println(page.getCurrent() + "ytrcccccccc");
        List<DiscussPost> lists = discussPostMapper.selectDiscussPosts(0, page.getOffset(), 10);
        List<Map<String, Object>> discussPosts = new ArrayList<>();

        if (lists != null){
            for (DiscussPost post : lists){
                Map<String, Object> map = new HashMap<>();
                User user = userMapper.selectById(post.getUserId());
                map.put("post", post);
                map.put("user", user);
                System.out.println(post);
                System.out.println(user);
                discussPosts.add(map);
            }
         }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }
}
