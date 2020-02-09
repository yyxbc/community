package com.xbc.community.controller;

import com.xbc.community.dto.FileDTO;
import com.xbc.community.provider.OssProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Controller
public class FileController {

    @Autowired
    OssProvider ossProvider;

    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(HttpServletRequest request){
        MultipartHttpServletRequest multipartRequest =(MultipartHttpServletRequest)request;
        MultipartFile file = multipartRequest.getFile("editormd-image-file");
        FileDTO fileDTO = new FileDTO();
        try {
            String url = ossProvider.upload(file.getInputStream(),file.getOriginalFilename());

            fileDTO.setSuccess(1);
            fileDTO.setUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileDTO;
    }
    @GetMapping("/file/download")
    @ResponseBody
    public String download(){
        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setUrl("/images/img.jpg");

        return ossProvider.download("Capture001.png");
    }


}
