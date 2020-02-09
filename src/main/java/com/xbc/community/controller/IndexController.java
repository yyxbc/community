package com.xbc.community.controller;

import com.github.pagehelper.PageInfo;
import com.xbc.community.bean.Question;
import com.xbc.community.service.QuestionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes(value = "user")
public class IndexController {
    @Autowired
    QuestionService questionService;

    @GetMapping("/")
    public String index(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo, @RequestParam(value = "pageSize", defaultValue = "5") int pageSize, @RequestParam(value = "search", required = false) String search, Model model) {
        PageInfo<Question> questions = null;
        if (StringUtils.isNotBlank(search)) {
            questions = questionService.findall(search, pageNo, pageSize);
            model.addAttribute("search", search);
        } else {
            questions = questionService.findall(pageNo, pageSize);
        }
        model.addAttribute("questions", questions);
        return "index";
    }
}
