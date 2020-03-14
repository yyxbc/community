package com.xbc.community;

import com.alipay.api.domain.Article;
import com.xbc.community.dto.AccessTokenDTO;
import com.xbc.community.productSeckill.redis.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CommunityApplicationTests {

    @Value("${github.clientid}")
    String clientid;
    @Value("${github.clientsecret}")
    String clientsecret;
    @Value("${github.redirecturi}")
    String redirecturi;

    @Autowired
    RedisUtil redisUtil;
    @Test
    public void contextLoads() {
        AccessTokenDTO accessTokenDTO=new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientid);
        accessTokenDTO.setClient_secret(clientsecret);
        accessTokenDTO.setRedirect_uri(redirecturi);
        System.out.println(accessTokenDTO);
    }

    @Test
    public void testSaveObject(){
        /*******单机测试**********
         * 【序列化List对象，整存整取】
         * redis写10万条数据耗时：280
         全部获取：100000
         redis取10万条数据耗时：202

         redis写100万条数据耗时：4142
         全部获取：1000000
         redis取10万条数据耗时：1486
         */

        List<Article> artList = new ArrayList<Article>();
        for(int i=0;i<1000000;i++){
            Article a = new Article();
            a.setDesc("id-" + i);
            a.setTitle("title-" + i);
            artList.add(a);
        }

        long start = System.currentTimeMillis();
        String key = "TestSetOpt";
        redisUtil.cacheValue(key, artList.toString());

        long stored = System.currentTimeMillis();
        System.out.println("redis写10万条数据耗时：" + (stored - start));


        //验证
        Object  in= redisUtil.getValue(key);
        List<Article> list = (List<Article>) in;
//        for(Article obj : list){
//           System.out.println("测试Set操作 article title 是:" + obj.getTitle());
//        }
        long end = System.currentTimeMillis();
        System.out.println("全部获取：" + list.size());
        System.out.println("redis取10万条数据耗时：" + (end - stored));

    }

//    //@Test
//    public void testSaveList(){
//        /**** 单机测试  ************
//         redis存取10万条数据测试
//         redis写10万条数据耗时：35727
//         全部获取：100000
//         redis取10万条数据耗时：251
//
//         redis存取100万条数据测试
//         redis写10万条数据耗时：316339
//         全部获取：1000000
//         redis取10万条数据耗时：2243
//         *************************/
//
//        Jedis jedis = new Jedis(jedisShardInfo);
//        List<Article> artList = new ArrayList<Article>();
//        System.out.println("redis存取10万条数据测试");
//        long start = System.currentTimeMillis();
//        for(int i=0;i<100000;i++){
//            Article a = new Article();
//            a.setId("id-" + i);
//            a.setTitle("title-" + i);
//            String objData = JSON.toJSONString(a);
//            jedis.rpush("article", objData);
//            //System.out.println("保存对象数据：" + objData);
//        }
//        long stored = System.currentTimeMillis();
//        System.out.println("redis写10万条数据耗时：" + (stored - start));
//
//        List<String> list = jedis.lrange("article", 0, -1);
//        for(String str:list){
//            //System.out.println("[获取对象数据]：" + str);
//            Article art = JSON.parseObject(str,Article.class);
//            //System.out.println("标题：" + art.getTitle());
//        }
//        long end = System.currentTimeMillis();
//        System.out.println("全部获取：" + list.size());
//        System.out.println("redis取10万条数据耗时：" + (end - stored));
//
//    }
}
