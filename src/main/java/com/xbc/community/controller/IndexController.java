package com.xbc.community.controller;

import com.github.pagehelper.PageInfo;
import com.xbc.community.Cache.HotTagCache;
import com.xbc.community.bean.Question;
import com.xbc.community.dto.HotTagDTO;
import com.xbc.community.service.QuestionService;
import com.xbc.community.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
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
                        @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                        @RequestParam(value = "search", required = false) String search,
                        @RequestParam(value = "tag", required = false) String tag,
                        @RequestParam(name = "sort", required = false , defaultValue = "new") String sort,
                        Model model) {
        PageInfo<Question> questions = null;
        List<String> tags = hotTagCache.getHots();
        if (StringUtils.isNotBlank(tag)) {
            model.addAttribute("tag", tag);
            questions = questionService.findByTag(tag, pageNo, pageSize);
        } else if (StringUtils.isNotBlank(search)) {
            questions = questionService.findall(search, pageNo, pageSize);
            model.addAttribute("search", search);
        } else if (StringUtils.isNotBlank(sort)) {
            System.out.println(sort);
            questions = questionService.findBySort(sort, pageNo, pageSize);
            model.addAttribute("sort", sort);
        } else {
            questions = questionService.findall(pageNo, pageSize);
        }
        if (tags.size() > 0) {
            List<HotTagDTO> hots2 = new ArrayList<>();
            for (String hot : tags) {
                System.out.println(hot);
                HotTagDTO hotTagDTO = new HotTagDTO();
                hotTagDTO.setName(hot);
                hotTagDTO.setViewCount(questionService.findviewCountByTag(hot));
                hotTagDTO.setQuestionCount(questionService.findQuestionCountByTag(hot));
                hotTagDTO.setCommentCount(questionService.findCommentCountByTag(hot));
                hots2.add(hotTagDTO);
            }
            for (HotTagDTO t : hots2) {
                System.out.println(t);
            }
            model.addAttribute("tags", hots2);
        }

        model.addAttribute("questions", questions);
        model.addAttribute("tagslist", tagService.getAll());
        return "index";
    }
}
