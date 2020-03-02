package com.xbc.community.admin.controller;

import com.xbc.community.admin.dto.PageInfo;
import com.xbc.community.bean.Question;
import com.xbc.community.bean.User;
import com.xbc.community.dto.ResultDTO;
import com.xbc.community.service.QuestionService;
import com.xbc.community.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/main/content")
public class ContentController {
    @Autowired
    QuestionService questionService;
    @Autowired
    TagService tagService;
    @ModelAttribute
    public Question getQuestion(Integer id) {
        Question question = null;

        // id 不为空，则从数据库获取
        if (id != null) {
            question = questionService.findById(id);
        } else {
            question = new Question();
        }

        return question;
    }

    /**
     * 跳转内容列表页
     *
     * @return
     */

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(Model model) {
        List<Question> questions = questionService.findall();
        ResultDTO resultDTO =ResultDTO.okOf();
        resultDTO.setData(questions);
        model.addAttribute("ResultDTO", resultDTO);
        return "admin/views/content_list";
    }

    /**
     * 跳转表单页
     *
     * @return
     */
    @RequestMapping(value = "form", method = RequestMethod.GET)
    public String form(Model model) {
        model.addAttribute("tags", tagService.getAll());
        return "admin/views/content_form";
    }

    /**
     * 保存
     *
     * @param entity
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(Question question, Model model, @SessionAttribute(value = "user",required = true) User user, RedirectAttributes redirectAttributes) {
        ResultDTO resultDTO = new ResultDTO();
        question.setCreator(user.getId());
        int num = 0;
        num= questionService.createorupdate(question);
        // 保存成功
        if (num == 1) {
            redirectAttributes.addFlashAttribute("resultDTO", ResultDTO.okOf());
            resultDTO.setMessage("保存成功");
            return "redirect:/main/content/list";
        }
        // 保存失败
        else {
            model.addAttribute("resultDTO", resultDTO.errorOf());
            resultDTO.setMessage("保存失败");
            model.addAttribute("question",question);
            return "admin/views/content_form";
        }
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */

    @ResponseBody
    @RequestMapping(value = "delete", method = RequestMethod.POST)
    public ResultDTO delete(String ids) {
        ResultDTO resultDTO = null;
        if (StringUtils.isNotBlank(ids)) {
            String[] idArray = ids.split(",");
            System.out.println(ids);
            int count = questionService.deleteMulti(idArray);
            if(count==idArray.length){
                resultDTO = ResultDTO.okOf();
                resultDTO.setMessage("删除内容成功");
            }else{
                resultDTO = ResultDTO.errorOf();
                resultDTO.setMessage("删除内容失败");
            }
        } else {
             resultDTO = ResultDTO.errorOf();
            resultDTO.setMessage("删除内容失败");
        }

        return resultDTO;
    }

    /**
     * 跳转详情页
     *
     * @return
     */
    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail(@RequestParam(required = false) Integer id, Model model) {
        if (id != null) {
            model.addAttribute("tags", tagService.getAll());
            model.addAttribute("question", questionService.findById(id));
        }
        return "admin/views/content_detail";
    }

    @ResponseBody
    @RequestMapping(value = "page", method = RequestMethod.GET)
    public PageInfo<Question> page(HttpServletRequest request) {
        String strDraw = request.getParameter("draw");
        String strStart = request.getParameter("start");
        String strLength = request.getParameter("length");

        int draw = strDraw == null ? 0 : Integer.parseInt(strDraw);
        int start = strStart == null ? 0 : Integer.parseInt(strStart);
        int length = strLength == null ? 10 : Integer.parseInt(strLength);
        start =start/10+1;
        // 封装 Datatables 需要的结果
        int count = questionService.findall().size();
        PageInfo<Question> pageInfo = new PageInfo<>();
        pageInfo.setDraw(draw);
        pageInfo.setRecordsTotal(count);
        pageInfo.setRecordsFiltered(count);
        pageInfo.setData(questionService.findall(start, length).getList());
        return pageInfo;
    }

}
