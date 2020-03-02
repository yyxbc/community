package com.xbc.community.admin.controller;

import com.xbc.community.dto.ResultDTO;
import com.xbc.community.dto.TagDTO;
import com.xbc.community.exception.CustomizeErrorCode;
import com.xbc.community.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@Controller
@RequestMapping(value = "/main/content/category")
public class ContentCategoryController {
    @Autowired
    TagService tagService;


    @GetMapping("/list")
    public String categories( Model model){
        List<TagDTO> tagDTOS =new ArrayList<>();
        tagDTOS =tagService.getAll();
        model.addAttribute("tagslist",tagDTOS);
        model.addAttribute("ResultDTO",ResultDTO.okOf());
        return "admin/views/content_category_list";
    }

    @PostMapping("/update")
    public String updateTag( TagDTO tagDTO,Model model){
        if(StringUtils.isBlank(tagDTO.getCategoryName())||StringUtils.isBlank(tagDTO.getTag())){
            model.addAttribute("ResultDTO",ResultDTO.errorOf(
                    CustomizeErrorCode.TAG_IS_EMPTY
            ));
            return "admin/views/content_category_form";
        }
        if(tagDTO.getId()==null){
           int num = tagService.insert(tagDTO.getCategoryName(),tagDTO.getTag());
            model.addAttribute("tagDTO",tagDTO);
           if(num==1){
                model.addAttribute("ResultDTO",ResultDTO.okOf());
                return "redirect:/main/content/category/list";
            }else{
               model.addAttribute("ResultDTO",ResultDTO.errorOf());
                return "admin/views/content_category_form";
            }
        }
        List<String> tags =tagService.findByCategoryName(tagDTO.getCategoryName());
        if(tags.size()==0){
            return "admin/views/content_category_form";
        }
        System.out.println(tagDTO.getTag());

        int a =tagService.updateTag(tagDTO.getCategoryName(),tagDTO.getTag());
        model.addAttribute("tagDTO",tagDTO);
        if(a==1){
            model.addAttribute("ResultDTO",ResultDTO.okOf());
            return "redirect:/main/content/category/list";
        }
        return "admin/views/content_category_form";
    }



    @RequestMapping(value = "form", method = RequestMethod.GET)
    public String form(@RequestParam(value = "id",required = false) Integer id,Model model) {
        if(id!=null){
            model.addAttribute("tagDTO",tagService.findById(id));
        }
        return "admin/views/content_category_form";
    }

    @PostMapping("/delete")
    @ResponseBody
    public ResultDTO delTag(String ids ){
       int num = tagService.delete(Integer.parseInt(ids));
        if(num==1){
            return ResultDTO.okOf();
        }
        return ResultDTO.errorOf();
    }

}
