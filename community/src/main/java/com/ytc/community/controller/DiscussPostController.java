package com.ytc.community.controller;

import com.ytc.community.entity.Comment;
import com.ytc.community.entity.DiscussPost;
import com.ytc.community.entity.Page;
import com.ytc.community.entity.User;
import com.ytc.community.service.CommentService;
import com.ytc.community.service.DiscussPostService;
import com.ytc.community.service.UserService;
import com.ytc.community.util.CommunityConstant;
import com.ytc.community.util.CommunityUtil;
import com.ytc.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

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
    public String postId(@PathVariable("postId") int postId, Model model, Page page){
        // 帖子
        DiscussPost post =  discussPostService.findById(postId);
        User user = userService.findById(post.getUserId());
        // 作者

        model.addAttribute("post",post);
        model.addAttribute("user",user);

        // 评论分页信息
        page.setLimit(5);
        page.setPath("/discuss/detail/" + postId);
        page.setRows(post.getCommentCount());

        // 评论： 给帖子的评论
        // 回复： 给评论的评论
        // 评论的列表
        List<Comment> commentsList = commentService.findCommentsByEntity(ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());
        // 包含 评论 和 用户 的列表
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentsList != null){
            // 取出来 单条评论
            for (Comment comment: commentsList){
                Map<String, Object> commentVo = new HashMap<>();
                // 单条评论
                commentVo.put("comment", comment);
                // 单条评论的用户。
                commentVo.put("user", userService.findById(comment.getUserId()));
                //  对单条评论 回复的列表。
                List<Comment> replyList = commentService.findCommentsByEntity(ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);
                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null){
                    for (Comment reply : replyList){
                        Map<String, Object> replyVo = new HashMap<>();
                        replyVo.put("reply", reply);
                        replyVo.put("user", userService.findById(reply.getUserId()));
                        User target = reply.getTargetId() == 0 ? null : userService.findById(reply.getTargetId());
                        replyVo.put("target", target);
                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replies", replyVoList);
                // 单个评论的回复数量。
                int replyCount = commentService.findCountByEntity(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }

        model.addAttribute("comments", commentVoList);

        return "/site/discuss-detail";
    }
}
