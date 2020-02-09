package com.xbc.community.controller;

import com.github.pagehelper.PageInfo;
import com.xbc.community.bean.Question;
import com.xbc.community.bean.User;
import com.xbc.community.dto.NotificationDTO;
import com.xbc.community.service.NotificationService;
import com.xbc.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class ProfileController {

    @Autowired
    QuestionService questionService;

    @Autowired
    NotificationService notificationService;

    @GetMapping("/profile/{action}")
    public String profile(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize, @PathVariable(name = "action") String section, Model model, @SessionAttribute(value = "user",required = false)User user) {
        if(user==null){
            return "redirect:/";
        }
        PageInfo<Question> questions=null;
        if (section.equals("questions")) {
            questions = questionService.findall(user.getId(),pageNo,pageSize);
            model.addAttribute("section", section);
            model.addAttribute("sectionName", "我的问题");
            model.addAttribute("pageinfo", questions);
        } else if (section.equals("replies")) {
            PageInfo<NotificationDTO> notificationDTOS = notificationService.list(user.getId(),pageNo,pageSize);
            System.out.println(notificationDTOS.getList());
            model.addAttribute("section", section);
            model.addAttribute("sectionName", "最新回复");

            model.addAttribute("pageinfo", notificationDTOS);
        }
        return "profile";
    }
}
