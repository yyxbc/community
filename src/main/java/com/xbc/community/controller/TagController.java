package com.xbc.community.controller;

import com.alibaba.fastjson.JSONObject;
import com.xbc.community.dto.HotTagDTO;
import com.xbc.community.dto.ResultDTO;
import com.xbc.community.dto.TagDTO;
import com.xbc.community.productSeckill.redis.RedisUtil;
import com.xbc.community.service.QuestionService;
import com.xbc.community.service.TagService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
@Controller
public class TagController {
    @Autowired
    TagService tagService;

    @Autowired
    RedisUtil redisUtil;
    @Autowired
    QuestionService questionService;

    public List<TagDTO> getTag() {
        List<TagDTO> tagDTOS = new ArrayList<>();
        tagService.getAll();
        return tagDTOS;
    }

    @GetMapping("/categories")
    public String categories(String categoryName, Model model) {
        List<HotTagDTO> tagsCount=new ArrayList<>();
        List<TagDTO> tagDTOS = tagService.getAll();
        model.addAttribute("tagslist", tagDTOS);
        model.addAttribute("section", "categories");
        if(redisUtil.containsValueKey("tags_"+categoryName)){
            String object = (String)redisUtil.getValue("tags_"+categoryName);
            tagsCount = (List<HotTagDTO>) JSONObject.parseArray(object, HotTagDTO.class);
           // System.out.println(tagsCount);
        }else{
            List<String> tags = new ArrayList<>();
            //System.out.println(categoryName);
            if (categoryName.equals("all") || categoryName == "all") {
                for (TagDTO t : tagDTOS) {
                    List<String> tag = tagService.findByCategoryName(t.getCategoryName());
                    System.out.println(tag);
                    tags.addAll(tag);
                }
            } else {
                tags = tagService.findByCategoryName(categoryName);
            }
            for (String tag : tags) {
                String tagC = tag.replace("+", "").replace("*", "").replace("?", "");
                HotTagDTO tagCount = tagService.getCount(tagC);
                tagCount.setName(tag);
                //System.out.println(tagCount);
                tagsCount.add(tagCount);
            }
            redisUtil.cacheValue("tags_"+categoryName, JSONObject.toJSONString(tagsCount));
        }
        model.addAttribute("tags", tagsCount);
        model.addAttribute("categoryName", categoryName);
        return "categories";
    }

    @PostMapping("/categories")
    @ResponseBody
    public ResultDTO updateTag(@RequestBody TagDTO tagDTO) {
        List<String> tags = tagService.findByCategoryName(tagDTO.getCategoryName());
        System.out.println(tagDTO);
        if (tags.size() == 0) {
            return ResultDTO.errorOf();
        }
        ArrayList<String> taglist = new ArrayList<>(tags);
        taglist.add(tagDTO.getTag());
        String tag = StringUtils.join(taglist, ",");
        int a = tagService.updateTag(tagDTO.getCategoryName(), tag);
        if (a == 1) {
            return ResultDTO.okOf();
        }
        System.out.println(2);
        return ResultDTO.errorOf();
    }

    @DeleteMapping("/categories")
    @ResponseBody
    public ResultDTO delTag(@RequestBody TagDTO tagDTO) {
        List<String> tags = tagService.findByCategoryName(tagDTO.getCategoryName());
        System.out.println(tagDTO);
        if (tags.size() == 0) {
            return ResultDTO.errorOf();
        }
        ArrayList<String> taglist = new ArrayList<>(tags);
        taglist.remove(tagDTO.getTag());
        String tag = StringUtils.join(taglist, ",");
        int a = tagService.updateTag(tagDTO.getCategoryName(), tag);
        if (a == 1) {
            return ResultDTO.okOf();
        }
        return ResultDTO.errorOf();
    }



}
