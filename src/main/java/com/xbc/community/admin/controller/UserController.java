package com.xbc.community.admin.controller;

import com.xbc.community.admin.dto.PageInfo;
import com.xbc.community.bean.User;
import com.xbc.community.dto.ResultDTO;
import com.xbc.community.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户管理
 * <p>Title: UserController</p>
 * <p>Description: </p>
 *
 * @author Lusifer
 * @version 1.0.0
 * @date 2018/6/14 14:07
 */
@Controller
@RequestMapping("/main/user")
public class UserController {
    @Autowired
    UserService userService;

    @ModelAttribute
    public User getUser(Integer id) {
        User user = null;

        // id 不为空，则从数据库获取
        if (id != null) {
            user = userService.findById(id);
        } else {
            user = new User();
        }

        return user;
    }

    /**
     * 跳转到用户列表页
     *
     * @return
     */

    @RequestMapping(value = "list", method = RequestMethod.GET)
    public String list(Model model) {
        List<User> users = userService.findAll();
        ResultDTO resultDTO =ResultDTO.okOf();
        resultDTO.setData(users);
        model.addAttribute("ResultDTO", resultDTO);
        return "admin/views/user_list";
    }

    /**
     * 跳转用户表单页
     *
     * @return
     */

    @RequestMapping(value = "form", method = RequestMethod.GET)
    public String form() {
        return "admin/views/user_form";
    }

    /**
     * 保存用户信息
     *
     * @param tbUser
     * @return
     */

    @RequestMapping(value = "save", method = RequestMethod.POST)
    public String save(User user, Model model, RedirectAttributes redirectAttributes) {
        ResultDTO resultDTO = new ResultDTO();
        int num = 0;
        if (user.getId() != null) {
            num = userService.update(user);
        } else {
            User user1 = userService.insert(user);
            if (user1 != null) {
                num = 1;
            }
        }
        // 保存成功
        if (num == 1) {
            redirectAttributes.addFlashAttribute("resultDTO", ResultDTO.okOf());
            return "redirect:/main/user/list";
        }

        // 保存失败
        else {
            model.addAttribute("resultDTO", resultDTO.errorOf());
            return "admin/views/user_form";
        }
    }

    /**
     * 删除用户信息
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
           int count = userService.delteByid(idArray);
           if(count==idArray.length){
               resultDTO = ResultDTO.okOf();
               resultDTO.setMessage("删除成功");
           }else{
               resultDTO = ResultDTO.errorOf();
               resultDTO.setMessage("删除失败");
           }
        } else {
            resultDTO = ResultDTO.errorOf();
            resultDTO.setMessage("删除失败");
        }

        return resultDTO;
    }

    /**
     * 显示用户详情
     *
     * @return
     */

    @RequestMapping(value = "detail", method = RequestMethod.GET)
    public String detail(@RequestParam(required = false) Integer id, Model model) {
        if (id != null) {
            model.addAttribute("user", userService.findById(id));
        }
        return "admin/views/user_detail";
    }

    @ResponseBody
    @RequestMapping(value = "page", method = RequestMethod.GET)
    public PageInfo<User> page(HttpServletRequest request) {
        String strDraw = request.getParameter("draw");
        String strStart = request.getParameter("start");
        String strLength = request.getParameter("length");

        int draw = strDraw == null ? 0 : Integer.parseInt(strDraw);
        int start = strStart == null ? 0 : Integer.parseInt(strStart);
        int length = strLength == null ? 10 : Integer.parseInt(strLength);
        start =start/10+1;
        // 封装 Datatables 需要的结果
        int count = userService.findAll().size();
        PageInfo<User> pageInfo = new PageInfo<>();
        System.out.println(userService.findAll().toString());
        pageInfo.setDraw(draw);
        pageInfo.setRecordsTotal(count);
        pageInfo.setRecordsFiltered(count);
        pageInfo.setData(userService.findall(start, length).getList());
        return pageInfo;
    }

}
