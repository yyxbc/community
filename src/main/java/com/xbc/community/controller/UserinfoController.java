package com.xbc.community.controller;

import com.xbc.community.bean.User;
import com.xbc.community.dto.ResultDTO;
import com.xbc.community.exception.CustomizeErrorCode;
import com.xbc.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class UserinfoController {
    @Autowired
    UserService userService;
    @PostMapping("/profile/userinfo")
    public ResultDTO userinfo(User user){
        user =userService.update(user);
        if(user==null){
            return ResultDTO.errorOf(CustomizeErrorCode.CHANGE_USERINFO_FAILED);
        }
        return ResultDTO.okOf();
    }

    @GetMapping("/members")
    public String getalluser(Model model){
        List<User> users = userService.findAll();
        model.addAttribute("users",users);
        model.addAttribute("section","members");
        return "members";
    }
}
