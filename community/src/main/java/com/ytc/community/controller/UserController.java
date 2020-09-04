package com.ytc.community.controller;
import com.ytc.community.annotation.LoginRequired;
import com.ytc.community.dao.LoginTicketMapper;
import com.ytc.community.entity.User;
import com.ytc.community.service.UserService;
import com.ytc.community.util.CommunityUtil;
import com.ytc.community.util.CookieUtil;
import com.ytc.community.util.HostHolder;
import lombok.extern.java.Log;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Multipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${community.path.domain}")
    private String domain;

    @Value("${community.path.upload}")
    private String uploadPath;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(){
        return "/site/setting";
    }

    // 上传图片
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model){
        if (headerImage == null){
            model.addAttribute("error", "您还没有选择文件");
            return "/site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        if (StringUtils.isBlank(suffix)){
            model.addAttribute("error", "图片格式错误");
            return "/site/setting";
        }
        // 生成随机文件名
        fileName = CommunityUtil.generateUUID()+suffix;
        File dest = new File(uploadPath+"/"+fileName);

        // 确定文件的存放位置。
        try {
            // 存储文件
            headerImage.transferTo(dest);
        } catch (IOException e) {
            logger.error("上传文件失败: "+ e.getMessage());
            throw new RuntimeException("上传文件失败，服务器异常！",e);
        }

        // 更新当前用户头像的存放路径（Web访问路径）
        // http://localhost:8080/user/header/xxx.jpg
        User user = hostHolder.getUser();
        String headerUrl = domain + "/user/header/" + fileName;
        userService.updateHeaderUrl(user.getId(), headerUrl);
        return "redirect:/index";
    }

    // 获取图片
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response){
        // 服务器存放路径
        fileName = uploadPath + "/" + fileName;
        // 文件后缀
        String suffix = fileName.substring(fileName.lastIndexOf('.'));
        // 响应图片
        response.setContentType("image/"+suffix);

        try (
                FileInputStream fis = new FileInputStream(fileName); // 输入流
                OutputStream os = response.getOutputStream();      // 输出流,Web 服务器的输出。
                // 小括号的意思是自动  close。
                ){
            byte[] buffer = new byte[1024]; // 每次有一个缓冲区， 读1024 个字节。
            int b = 0;
            while((b = fis.read(buffer)) != -1){  // 如果缓冲区有值，那么就写出来。
                os.write(buffer,0, b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/updatePassword")
    public String updatePassword(String oldPassword, String newPassword, Model model, HttpServletRequest request){
        User user = hostHolder.getUser();
        Map<String, Object> map = new HashMap<>();
        map = userService.changePassword(user.getId(), oldPassword, newPassword);
        if (map.isEmpty()){
            // 成功则跳转到登录页面， 然后重新登录
            String ticket = CookieUtil.getValue(request, "ticket");
            loginTicketMapper.updateStatus(ticket, 1);
            return "redirect:/login";
        } else {
            // 失败跳转回原页面。
            model.addAttribute("oldPasswordMsg", map.get("oldPasswordMsg"));
            model.addAttribute("newPasswordMsg", map.get("newPasswordMsg"));
            return "/site/setting";
        }
    }

}
