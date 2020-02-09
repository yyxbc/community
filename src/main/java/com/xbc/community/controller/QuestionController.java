package com.xbc.community.controller;

import com.xbc.community.bean.Comment;
import com.xbc.community.bean.Question;
import com.xbc.community.exception.CustomizeErrorCode;
import com.xbc.community.exception.CustomizeException;
import com.xbc.community.service.CommentService;
import com.xbc.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class QuestionController {
    @Autowired
    QuestionService questionService;
    @Autowired
    CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id, Model model) {

        Question question = questionService.findById(id);
        if(question==null){
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
}
