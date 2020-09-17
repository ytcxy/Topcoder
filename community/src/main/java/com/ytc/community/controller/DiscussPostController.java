package com.ytc.community.controller;

import com.ytc.community.entity.*;
import com.ytc.community.event.EventProducer;
import com.ytc.community.service.CommentService;
import com.ytc.community.service.DiscussPostService;
import com.ytc.community.service.LikeService;
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

    @Autowired
    private LikeService likeService;

    @Autowired
    private EventProducer eventProducer;


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

        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(user.getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(post.getId());
        eventProducer.fireEvent(event);


        // 报错的情况下，以后统一处理。
        return CommunityUtil.getJSONString(0, "发布成功");
    }

    @GetMapping("/detail/{postId}")
    public String postId(@PathVariable("postId") int postId, Model model, Page page){
        // 帖子
        DiscussPost post =  discussPostService.findById(postId);
        User user = userService.findById(post.getUserId());
        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, postId);

        int status = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_POST, postId);

        // 作者

        model.addAttribute("post",post);
        model.addAttribute("user",user);
        model.addAttribute("likeCount",likeCount);
        model.addAttribute("status",status);


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
                // 赞的数量。
                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);
                // 自己对赞的状态。
                status = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("status", status);

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
                        likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);
                        status = hostHolder.getUser() == null ? 0 : likeService.findEntityLikeStatus(hostHolder.getUser().getId(), ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("status",status);
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

    // 置顶
    @PostMapping("/top")
    @ResponseBody
    public String setTop(int id){
        discussPostService.updateType(id, 1);
        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }
    // 加精
    @PostMapping("/wonderful")
    @ResponseBody
    public String setWonderful(int id){
        discussPostService.updateStatus(id, 1);
        // 触发发帖事件
        Event event = new Event()
                .setTopic(TOPIC_PUBLISH)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }
    // 加精
    @PostMapping("/delete")
    @ResponseBody
    public String setDelete(int id){
        discussPostService.updateStatus(id, 2);
        // 触发删帖事件
        Event event = new Event()
                .setTopic(TOPIC_DELETE)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(ENTITY_TYPE_POST)
                .setEntityId(id);
        eventProducer.fireEvent(event);
        return CommunityUtil.getJSONString(0);
    }
}
