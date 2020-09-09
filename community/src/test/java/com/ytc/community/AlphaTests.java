package com.ytc.community;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = DemoApplication.class)
@RunWith(SpringRunner.class)
public class AlphaTests {


    @Test
    public void Test(){
        Map<String, Object> map = new HashMap<>();
        map.put("ytc", 1111);
        System.out.println(map.get("ytc"));
        System.out.println(map.get("y"));
    }
}
