package com.xbc.community.controller;

import com.xbc.community.bean.Comment;
import com.xbc.community.bean.Question;
import com.xbc.community.dto.ResultDTO;
import com.xbc.community.exception.CustomizeErrorCode;
import com.xbc.community.exception.CustomizeException;
import com.xbc.community.service.CommentService;
import com.xbc.community.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@Slf4j
public class QuestionController {
    @Autowired
    QuestionService questionService;
    @Autowired
    CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id, Model model) {

        Question question = questionService.findById(id);
        if(question==null){
            log.error("问题未找到，{}",id);
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        List<Comment> comments = commentService.listByQuestionId(id);
        questionService.incView(question);
        for(Comment q:comments){
            System.out.println(q);
        }
        System.out.println(question);
        model.addAttribute("question", question);
        model.addAttribute("comments", comments);
        System.out.println(question);
        List<Question> relatedQuestions = questionService.selectRelated(question);
        if(relatedQuestions!=null){
            model.addAttribute("relatedQuestions",relatedQuestions);
        }

        return "question";
    }

    @PostMapping("/question/{id}")
    @ResponseBody
    public ResultDTO delete(@PathVariable(name="id") String id) {
        log.info("要删除的是："+id);
        int num=1;
        //int num =questionService.delete(parseInt(id));
        if(num==1){
            return ResultDTO.okOf();
        }else{
            throw new CustomizeException(CustomizeErrorCode.SYS_ERROR);
        }

    }
}
