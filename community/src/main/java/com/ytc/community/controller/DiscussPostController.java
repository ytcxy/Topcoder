package com.ytc.community.controller;

import com.ytc.community.entity.DiscussPost;
import com.ytc.community.entity.User;
import com.ytc.community.service.DiscussPostService;
import com.ytc.community.service.UserService;
import com.ytc.community.util.CommunityUtil;
import com.ytc.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;
    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content){
        User user = hostHolder.getUser();
        if (user == null){
            return CommunityUtil.getJSONString(403, "您还没有登录");
        }
        DiscussPost post = new DiscussPost();
        post.setUserId(user.getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        // 报错的情况下，以后统一处理。
        return CommunityUtil.getJSONString(0, "发布成功");
    }

    @GetMapping("/detail/{postId}")
    public String postId(@PathVariable("postId") int postId, Model model){
        DiscussPost post =  discussPostService.findById(postId);
        User user = userService.findById(post.getUserId());

        model.addAttribute("post",post);
        model.addAttribute("user",user);

        return "/site/discuss-detail";
    }
}
