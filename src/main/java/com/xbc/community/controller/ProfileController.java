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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

    @Autowired
    UserService userService;
    @Autowired
    QuestionService questionService;

    @GetMapping("/profile/{action}")
    public String profile(@CookieValue("token")String token, @RequestParam(value = "pageNo", defaultValue = "1") int pageNo, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize, @PathVariable(name = "action") String section, Model model) {
        User user = userService.findByToken(token);
        PageInfo<Question> questions=null;
        if (section.equals("questions")) {
            questions = questionService.findall(user.getId(),pageNo,pageSize);

        } else if (section.equals("replies")) {
            questions = questionService.findall(user.getId(),pageNo,pageSize);
        }
        model.addAttribute("questions", questions);
        model.addAttribute("section", section);
        return "profile";
    }
}
