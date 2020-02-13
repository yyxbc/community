package com.xbc.community.controller;

import com.xbc.community.dto.TagDTO;
import com.xbc.community.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
public class TagController {
    @Autowired
    TagService tagService;

    public List<TagDTO> getTag(){
        List<TagDTO> tagDTOS =new ArrayList<>();
        tagService.getAll();
        return tagDTOS;
    }

}
