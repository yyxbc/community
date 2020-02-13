package com.xbc.community;

import com.xbc.community.dto.AccessTokenDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CommunityApplicationTests {

    @Value("${github.clientid}")
    String clientid;
    @Value("${github.clientsecret}")
    String clientsecret;
    @Value("${github.redirecturi}")
    String redirecturi;

    @Test
    void contextLoads() {
        AccessTokenDTO accessTokenDTO=new AccessTokenDTO();
        accessTokenDTO.setClient_id(clientid);
        accessTokenDTO.setClient_secret(clientsecret);
        accessTokenDTO.setRedirect_uri(redirecturi);
        System.out.println(accessTokenDTO);
    }

}
