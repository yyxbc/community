package com.xbc.community.schedule;

import com.xbc.community.Cache.HotTagCache;
import com.xbc.community.bean.Question;
import com.xbc.community.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class HotTagTasks {

    @Autowired
    QuestionService questionService;
    @Autowired
    HotTagCache hotTagCache;



    //@Scheduled(fixedRate = 6000)
    @Scheduled(cron = "0 0 1 * * *")
    public void getHotTags() {
        log.info("hotTagSchedule start {}", new Date());
        List<Question> list = new ArrayList<>();
        Map<String, Integer> priorities = new HashMap<>();
        list = questionService.findall();
        for (Question question : list) {
            // log.info("List question {}",question.getId());
            //System.out.println(question);
            String[] tags = StringUtils.split(question.getTag(), ",");
            for (String tag : tags) {
                Integer priority = priorities.get(tag);
                System.out.println(priority);
                System.out.println(tag);
                if (priority != null) {
                    priorities.put(tag, priority + 10 +question.getViewCount()+ 4*question.getCommentCount());
                    System.out.println(priorities);
                } else {
                    priorities.put(tag, 10 +question.getViewCount()+ 4*question.getCommentCount());
                    System.out.println(priorities);
                }
            }
        }
        hotTagCache.setTags(priorities);
        priorities.forEach(
                (k, v) -> {
                    System.out.print(k);
                    System.out.print(":");
                    System.out.print(v);
                    System.out.println();
                }
        );
        hotTagCache.updateTags(priorities);
        log.info("hotTagSchedule end {}", new Date());
    }
}
