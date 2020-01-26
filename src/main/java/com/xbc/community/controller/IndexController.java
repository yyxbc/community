package com.xbc.community.controller;

import com.github.pagehelper.PageInfo;
import com.xbc.community.bean.Question;
import com.xbc.community.bean.User;
import com.xbc.community.service.QuestionService;
import com.xbc.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes(value = "user")
public class IndexController {
    @Autowired
    QuestionService questionService;
    @Autowired
    UserService userService;

    @GetMapping("/")
    public String index(@RequestParam(value="pageNo",defaultValue="1")int pageNo, @RequestParam(value="pageSize",defaultValue="5")int pageSize, @CookieValue(value = "token", required = false) String token, Model model) {
        if (token != null) {
            User user = userService.findByToken(token);
            model.addAttribute("user", user);
        }
        PageInfo<Question> questions = questionService.findall(pageNo,pageSize);
        System.out.println(pageNo+"--"+pageSize);
     for(Question q:questions.getList()){
         System.out.println(q);
     }
        model.addAttribute("questions", questions);
        return "index";
    }
}
