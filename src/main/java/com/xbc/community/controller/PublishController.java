package com.xbc.community.controller;

import com.xbc.community.bean.Question;
import com.xbc.community.bean.User;
import com.xbc.community.service.QuestionService;
import com.xbc.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;
    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(Question question, @CookieValue(value = "token",required=false)String token
    , Model model){
        model.addAttribute("question",question);
        if(question.getTitle()==null||question.getTitle().equals("")){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if(question.getDescription()==null||question.getDescription().equals("")){
            model.addAttribute("error","问题补充不能为空");
            return "publish";
        }
        if(question.getTag()==null||question.getTag().equals("")){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }
        if(token!=null){
            User user = userService.findByToken(token);
            model.addAttribute("user",user);
            question.setCreator(user.getId());
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            System.out.println(question);
            questionService.create(question);
        }

        return "redirect:/";
    }
}
