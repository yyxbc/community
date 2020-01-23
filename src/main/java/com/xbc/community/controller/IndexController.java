package com.xbc.community.controller;

import com.xbc.community.bean.User;
import com.xbc.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.HttpServletRequest;

@Controller
@SessionAttributes(value = "user")
public class IndexController {
    @Autowired
    UserService userService;
    @GetMapping("/")
    public String index(@CookieValue(value = "token",required = false)String token,HttpServletRequest request, Model model){

//        Cookie[] cookies =  request.getCookies();
//        if(cookies != null){
//            for(Cookie cookie : cookies){
//                if(cookie.getName().equals("token")){
//                    String token = cookie.getValue();
//                    System.out.println(token);
//                    User user = userService.findByToken(token);
//                    System.out.println(user);
//                    model.addAttribute("user",user);
//                }
//            }
//        }
        if(token!=null){
            User user = userService.findByToken(token);
            model.addAttribute("user",user);
        }


        return "index";
    }
}
