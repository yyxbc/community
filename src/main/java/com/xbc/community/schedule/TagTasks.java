package com.xbc.community.schedule;

import com.alibaba.fastjson.JSONObject;
import com.xbc.community.dto.HotTagDTO;
import com.xbc.community.dto.TagDTO;
import com.xbc.community.productSeckill.redis.RedisUtil;
import com.xbc.community.service.TagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class TagTasks {

    @Autowired
    TagService tagService;
    @Autowired
    RedisUtil redisUtil;

    //@Scheduled(fixedRate = 6000)
    @Scheduled(cron = "0 0 1 * * *")
    public void getTags() {
        log.info("TagSchedule start {}", new Date());
        List<TagDTO> tagDTOS = tagService.getAll();
        List<String> tags = new ArrayList<>();
        List list =new ArrayList();
        list.add("all");
        for (TagDTO t : tagDTOS) {
            list.add(t.getCategoryName());
        }
        System.out.println(list);
        for(Object categoryName:list){
        if (categoryName.equals("all") || categoryName == "all") {
            for (TagDTO t : tagDTOS) {
                List<String> tag = tagService.findByCategoryName(t.getCategoryName());
                System.out.println(tag);
                tags.addAll(tag);
            }
        } else {
            tags = tagService.findByCategoryName(categoryName.toString());
        }
        List<HotTagDTO> tagsCount = new ArrayList<>();
        for (String tag : tags) {
            String tagC = tag.replace("+", "").replace("*", "").replace("?", "");
            HotTagDTO tagCount = tagService.getCount(tagC);
            tagCount.setName(tag);
            System.out.println(tagCount);
            tagsCount.add(tagCount);
        }
        if(redisUtil.containsValueKey("tags_"+categoryName)){
            redisUtil.removeValue("tags_"+categoryName);
        }
        redisUtil.cacheValue("tags_"+categoryName, JSONObject.toJSONString(tagsCount));
        }
        log.info("hotTagSchedule end {}", new Date());
    }
}
