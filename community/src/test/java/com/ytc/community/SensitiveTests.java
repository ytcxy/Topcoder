package com.ytc.community;

import com.ytc.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@ContextConfiguration(classes = DemoApplication.class)
@RunWith(SpringRunner.class)
public class SensitiveTests {
    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveWords(){
        String str = "可以赌博，可以嫖娼，可以吸毒，啧啧啧!!!";
        String filter = sensitiveFilter.filter(str);
        System.out.println(filter);
    }
}
