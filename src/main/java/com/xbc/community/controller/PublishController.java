package com.xbc.community.controller;

import com.xbc.community.Cache.TagCache;
import com.xbc.community.bean.Question;
import com.xbc.community.bean.User;
import com.xbc.community.dto.TagDTO;
import com.xbc.community.exception.CustomizeErrorCode;
import com.xbc.community.exception.CustomizeException;
import com.xbc.community.service.QuestionService;
import com.xbc.community.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;

@Controller
@Slf4j
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    TagService tagService;

    @GetMapping("/publish/{id}")
    public String publish(@PathVariable(name = "id") Integer id, Model model) {
        Question question = questionService.findById(id);
        if (question == null) {
            log.error("问题未找到,{}",id);
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        model.addAttribute("question", question);
        model.addAttribute("tags", tagService.getAll());
        return "publish";
    }

    @GetMapping("/publish")
    public String publish(Model model) {
        model.addAttribute("tags", tagService.getAll());
        return "publish";
    }

    @PostMapping("/publish")
    public String doPublish(Question question, @SessionAttribute(value = "user",required = false)User user
            , Model model) {
        List<TagDTO> tagDTOS =tagService.getAll();
        System.out.println(tagDTOS.toString());
        model.addAttribute("tags", tagDTOS);
        if (question == null) {
            log.error("问题未找到,{}",question);
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        model.addAttribute("question", question);
        if (question.getTitle() == null || question.getTitle().equals("")) {
            log.error("提问发生错误,标题不能为空");
            model.addAttribute("error", "标题不能为空");
            return "publish";
        }
        if (question.getDescription() == null || question.getDescription().equals("")) {
            model.addAttribute("error", "问题补充不能为空");
            log.error("提问发生错误,问题补充不能为空");
            return "publish";
        }
        if (question.getTag() == null || question.getTag().equals("")) {
            model.addAttribute("error", "标签不能为空");
            log.error("提问发生错误,标签不能为空");
            return "publish";
        }

        String invalid = TagCache.filterInvalid(question.getTag(),tagDTOS);
        if(StringUtils.isNotBlank(invalid)){
            model.addAttribute("error","输入非法标签："+invalid);
            log.error("提问发生错误,输入非法标签："+invalid);
            return "publish";
        }

        if(user==null){
            return "redirect:/";
        }
            question.setCreator(user.getId());
            questionService.createOrUpdate(question);


        return "redirect:/";
    }


}
