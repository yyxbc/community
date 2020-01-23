package com.xbc.community.controller;

import com.xbc.community.bean.User;
import com.xbc.community.dto.AccessTokenDTO;
import com.xbc.community.dto.GitHubUser;
import com.xbc.community.provider.GitHubProbider;
import com.xbc.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@SessionAttributes(value = "user")
public class AuthorizeController {

    @Autowired
    UserService userService;

    @Autowired
    GitHubProbider githubProbider;

    @GetMapping("/callback")
    public String callback(@RequestParam(name = "code") String code,
                           @RequestParam(name = "state") String state, Model model,
                           HttpServletResponse response) {
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setClient_id("8dbc6e07750093b1bc32");
        accessTokenDTO.setClient_secret("06ec7d12fd2b87fde5e327a4aaa2b38af61d2164");
        accessTokenDTO.setCode(code);
        accessTokenDTO.setRedirect_uri("http://localhost:8080/callback");
        accessTokenDTO.setState(state);
        String accessToken = githubProbider.getAccessToken(accessTokenDTO);
        GitHubUser gitHubUser = githubProbider.getUser(accessToken);
        if(gitHubUser!=null){
            User user = new User();
            String token =UUID.randomUUID().toString();
            user.setToken(token);
            user.setName(gitHubUser.getName());
            user.setAccountId(String.valueOf(gitHubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setAvatarUrl(gitHubUser.getAvatar_url());
            System.out.println(user);
            userService.insert(user);
            model.addAttribute("user",gitHubUser);
            response.addCookie(new Cookie("token",token));
            return "redirect:/";
        }else {
            return "redirect:/";
        }

    }
}
