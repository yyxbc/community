package com.xbc.community.controller;

import com.github.pagehelper.PageInfo;
import com.xbc.community.Cache.HotTagCache;
import com.xbc.community.bean.Question;
import com.xbc.community.service.QuestionService;
import com.xbc.community.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.List;

@Controller
@SessionAttributes(value = "user")
public class IndexController {
    @Autowired
    QuestionService questionService;

    @Autowired
    HotTagCache hotTagCache;
    @Autowired
    TagService tagService;

    @GetMapping("/")
    public String index(@RequestParam(value = "pageNo", defaultValue = "1") int pageNo,
                        @RequestParam(value = "pageSize", defaultValue = "5") int pageSize,
                        @RequestParam(value = "search", required = false) String search,
                        @RequestParam(value = "tag", required = false) String tag,
                        Model model) {
        PageInfo<Question> questions = null;
        List<String> tags =hotTagCache.getHots();
        if(StringUtils.isNotBlank(tag)){
            questions = questionService.findByTag(tag, pageNo, pageSize);
        }else if (StringUtils.isNotBlank(search)) {
            questions = questionService.findall(search, pageNo, pageSize);
            model.addAttribute("search", search);
        } else {
            questions = questionService.findall(pageNo, pageSize);
        }
        model.addAttribute("tags", tags);
        model.addAttribute("questions", questions);
        model.addAttribute("tagslist", tagService.getAll());
        return "index";
    }
}
