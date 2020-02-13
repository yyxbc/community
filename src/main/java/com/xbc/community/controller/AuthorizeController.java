package com.xbc.community.controller;

import com.xbc.community.bean.User;
import com.xbc.community.dto.AccessTokenDTO;
import com.xbc.community.dto.GitHubUser;
import com.xbc.community.provider.GitHubProbider;
import com.xbc.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@Slf4j

public class AuthorizeController {

    @Autowired
    UserService userService;

    @Autowired
    GitHubProbider githubProbider;
    @Value("${github.clientid}")
    String clientid;
    @Value("${github.clientsecret}")
    String clientsecret;
    @Value("${github.redirecturi}")
    String redirecturi;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state, Model model,
                           HttpServletResponse response) {
        AccessTokenDTO accessTokenDTO =new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientid);
        accessTokenDTO.setClient_secret(clientsecret);
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri(redirecturi);
        accessTokenDTO.setState(state);
        System.out.println(accessTokenDTO);
        String accessToken = githubProbider.getAccessToken(accessTokenDTO);
        GitHubUser gitHubUser = githubProbider.getUser(accessToken);
        System.out.println(gitHubUser);
        System.out.println(accessToken);
        if(gitHubUser!=null){
            User user = new User();
            String token =UUID.randomUUID().toString();
            user.setToken(token);
            user.setUsername(gitHubUser.getName());
            user.setPassword(String.valueOf(gitHubUser.getId()));
            user.setAvatarUrl(gitHubUser.getAvatar_url());
            System.out.println(user);
            userService.insertOrUpdate(user);
            response.addCookie(new Cookie("token",token));
            return "redirect:/";
        }else {
            log.error("callback get github error,{}",gitHubUser);
            return "redirect:/";
        }

    }
    @GetMapping("/logout")
    public String logout(HttpServletResponse response, HttpServletRequest request){
        request.getSession().removeAttribute("user");
        Cookie cookie = new Cookie("token",null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    return "redirect:/";
    }

    @GetMapping("/login")
    public String login(){
        return "login";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }
}
