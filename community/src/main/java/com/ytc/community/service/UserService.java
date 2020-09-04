package com.ytc.community.service;

import com.ytc.community.dao.LoginTicketMapper;
import com.ytc.community.dao.UserMapper;
import com.ytc.community.entity.LoginTicket;
import com.ytc.community.entity.User;
import com.ytc.community.util.CommunityConstant;
import com.ytc.community.util.CommunityUtil;
import com.ytc.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Value("${community.path.domain}")
    private String domain;

    public User findById(int id){
        return userMapper.selectById(id);
    }

    public Map<String, Object> register(User user){
        Map<String, Object> map = new HashMap<>();
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (StringUtils.isBlank(user.getUsername())){
            map.put("usernameMsg", "账号不能为空");
            return map;
        }

        if (StringUtils.isBlank(user.getPassword())){
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())){
            map.put("mailMsg", "邮箱不能为空");
            return map;
        }
        User u = userMapper.selectByName(user.getUsername());
        if (u != null){
            map.put("emailMsg", "该账号已存在");
            return map;
        }

        u = userMapper.selectByEmail(user.getEmail());
        if (u != null){
            map.put("emailMsg", "该邮箱已存在");
            return map;
        }
        // 这个时候就可以注册用户了。
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userMapper.insertUser(user);

        // 激活邮件
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/activation/101/code
        String url = domain + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "激活账号", content);

        return map;
    }

    public int activation(int userId, String code){
        User user = userMapper.selectById(userId);
        System.out.println(userId);
        System.out.println(user);
        if (user.getStatus() == 1){
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)){
            userMapper.updateStatus(userId, 1);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILURE;
        }

    }

    public Map<String, Object> login(String username, String password, int expiredSecond){
        Map<String, Object> map = new HashMap<>();
        // 空值处理
        if (StringUtils.isBlank(username)){
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password)){
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        // 验证账号
        User user = userMapper.selectByName(username);
        if (user == null){
            map.put("usernameMsg", "账号不存在");
            return map;
        }
        if (user.getStatus() == 0){
            map.put("usernameMsg", "该账号未激活");
            return map;
        }

        // 验证密码；
        password = CommunityUtil.md5(password + user.getSalt());
        if (!password.equals(user.getPassword())){
            map.put("passwordMsg", "密码错误");
            return map;
        }

        // 生成登录的凭证。
        LoginTicket loginTicket = new LoginTicket();
        loginTicket = loginTicketMapper.selectByUserID(user.getId());

        if (loginTicket == null){
            loginTicket.setUserId(user.getId());
            loginTicket.setTicket(CommunityUtil.generateUUID());
            loginTicket.setStatus(0);
            loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSecond*1000));
            loginTicketMapper.insertLoginTicket(loginTicket);
        } else {
            loginTicketMapper.updateStatus(loginTicket.getTicket(), 0);
            loginTicketMapper.updateExpired(loginTicket.getTicket(), new Date(loginTicket.getExpired().getTime()+expiredSecond*1000));
        }

        map.put("ticket", loginTicket.getTicket());
        return map;
    }
    public void logout(String ticket){
        loginTicketMapper.updateStatus(ticket, 1);
    }
    public LoginTicket findLoginTicket(String ticket){
        return loginTicketMapper.selectByTicket(ticket);
    }

    public int updateHeaderUrl(int userId, String headerUrl){
        return userMapper.updateHeader(userId, headerUrl);
    }

    public Map<String, Object> changePassword(int id, String oldPassword, String newPassword){
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isBlank(oldPassword)){
            map.put("oldPasswordMsg", "旧密码不能为空");
            return map;
        }
        if (StringUtils.isBlank(newPassword)){
            map.put("newPasswordMsg", "新密码不能为空");
            return map;
        }
        User user = userMapper.selectById(id);
        if (!user.getPassword().equals(CommunityUtil.md5(oldPassword+user.getSalt()))){
            map.put("oldPasswordMsg", "旧密码不正确");
            return map;
        }
        if (oldPassword.equals(newPassword)){
            map.put("newPasswordMsg","新旧密码不能一样");
            return map;
        }
        newPassword = CommunityUtil.md5(newPassword+user.getSalt());
        userMapper.updatePassword(user.getId(), newPassword);
        return map;
    }
}
