package com.xbc.community.controller;

import com.xbc.community.bean.User;
import com.xbc.community.dto.NotificationDTO;
import com.xbc.community.enums.NotificationTypeEnum;
import com.xbc.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller
public class NotificationController {

    @Autowired
    NotificationService notificationService;

    @GetMapping("/notification/{id}")
    public String profile(  @PathVariable(name = "id") Integer id, Model model, @SessionAttribute(value = "user",required = false) User user) {
        if(user==null){
            return "redirect:/";
        }

        NotificationDTO notificationDTO =notificationService.read(id,user);
        System.out.println(notificationDTO);
        if(NotificationTypeEnum.REPLY_COMMENT.getType()==notificationDTO.getType()||
        NotificationTypeEnum.REPLY_QUESTION.getType()==notificationDTO.getType()){
            return "redirect:/question/"+notificationDTO.getOuterid();
        }else{
            return "redirect/";
        }

    }
}
