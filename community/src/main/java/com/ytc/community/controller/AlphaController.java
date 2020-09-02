package com.ytc.community.controller;

import com.ytc.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/alpha")
public class AlphaController {
    @Autowired
    private AlphaService alphaService;
    @RequestMapping("/hello")
    @ResponseBody
    public String Hello(){
        return "Hello ytc!!!";
    }
    @RequestMapping("/data")
    @ResponseBody
    public String findData(){
        return alphaService.find();
    }

    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();
        while(enumeration.hasMoreElements()){
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            System.out.println(name + " " + value);
        }
        System.out.println(request.getParameter("code"));
        // 返回响应数据
        response.setContentType("text/html;charset=utf-8");

        try (
                PrintWriter writer = response.getWriter();
                ){
            writer.write("<h1>nowcode</h1>");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    // GET request, 两种方式获得get方式中的参数。
    // /student?current=1&limit=20
    @RequestMapping(path="/student", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(
            // 获得路径中参数为 current 的参数， 但这个并不是必要的，这个参数可以没有，没有的话会有默认值，然后赋值给函数中的参数。
            @RequestParam(name="current", required = false, defaultValue = "1") int current,
            @RequestParam(name="limit", required = false, defaultValue = "10") int limit  ){
        System.out.println(current);
        System.out.println(limit);
        return "some student";
    }
    // /student/{id}
    @RequestMapping(path="/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student";

    }
    // POST 请求。
    @RequestMapping(path = "student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success";
    }
    // 网页渲染数据
    @GetMapping("/school")
    public String getSchool(Model model){
        model.addAttribute("name", "ytc");
        model.addAttribute("age", 123);
        return "/demo/teacher";
    }
    // 响应 json 数据。（异步请求。）
    // Java 对象 -》 JSON 字符串  -》 JS 对象
    @GetMapping("/emp")
    @ResponseBody
    public Map<String, Object> getEmp(){
        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "ytc");
        emp.put("age", 1);
        emp.put("salary", 20000);
        return emp;
    }

    @GetMapping("/emps")
    @ResponseBody
    public List<Map<String, Object>> getEmps(){
        List<Map<String, Object>> list = new ArrayList<>();

        Map<String, Object> emp = new HashMap<>();
        emp.put("name", "ytc");
        emp.put("age", 1);
        emp.put("salary", 20000);
        list.add(emp);

        emp = new HashMap<>();
        emp.put("name", "ytc1");
        emp.put("age", 12);
        emp.put("salary", 200000);
        list.add(emp);
        return list;
    }

}
