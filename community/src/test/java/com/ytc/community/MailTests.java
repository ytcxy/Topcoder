package com.ytc.community;

import com.ytc.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@SpringBootTest
@ContextConfiguration(classes = DemoApplication.class)
@RunWith(SpringRunner.class)
public class MailTests {
    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void testMailSender(){
        mailClient.sendMail("yutao862238585@gmail.com", "test", "I'm ytc"); // 直接发送文本邮件。
    }


    @Test
    public void testMailSendHtml(){
        Context context = new Context(); // 为了参数给thymeleaf，所以用这个。
        context.setVariable("username", "ytc");
        String content = templateEngine.process("/mail/demo", context); // 自动注入了模板引擎。找到路径喝传入参数。
        mailClient.sendMail("yutao862238585@gmail.com", "HTML", content);
    }
}
