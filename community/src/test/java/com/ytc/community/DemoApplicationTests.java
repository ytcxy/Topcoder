package com.ytc.community;

import com.ytc.community.dao.AlphaDao;
import com.ytc.community.service.AlphaService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;


@SpringBootTest
@ContextConfiguration(classes = DemoApplication.class)
@RunWith(SpringRunner.class)
// 一开始没有加 public ， 死活报错。加了就没有问题了。
public class DemoApplicationTests implements ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        // 这个是最原始的一种方法， 后来就要习惯用 @Autowired
    }

    @Test
    public void testApplicationContext(){
        System.out.println("ytc===" + applicationContext);
        AlphaDao alphaDao = applicationContext.getBean(AlphaDao.class);
        // get Bean from class type.
        System.out.println(alphaDao.select());
        alphaDao = applicationContext.getBean("alpha", AlphaDao.class);
        // get Bean from class name. because there are more than one Beans.

        System.out.println(alphaDao.select());

    }
    @Test
    public void testBeanManagement(){
        AlphaService alphaService = applicationContext.getBean(AlphaService.class);
        System.out.println(alphaService);
    }
    @Test
    public void testSimpleDateFormat(){
        SimpleDateFormat simpleDateFormat = applicationContext.getBean(SimpleDateFormat.class);
        System.out.println(simpleDateFormat.format(new Date()));
    }
    @Autowired
    @Qualifier("alpha")  // 为Bean命名， 然后就可以通过名字来注入。 一般用在同一种类型，在Bean中有多个。
    private AlphaDao alphaDao;
    @Test
    public void testAuto(){
        System.out.println(alphaDao.select());
    }
}
