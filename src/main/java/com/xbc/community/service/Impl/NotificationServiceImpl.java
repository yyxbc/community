package com.xbc.community.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xbc.community.bean.Notification;
import com.xbc.community.bean.User;
import com.xbc.community.dto.NotificationDTO;
import com.xbc.community.enums.NotificationStatusEnum;
import com.xbc.community.enums.NotificationTypeEnum;
import com.xbc.community.exception.CustomizeErrorCode;
import com.xbc.community.exception.CustomizeException;
import com.xbc.community.mapper.NotificationMapper;
import com.xbc.community.service.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    NotificationMapper notificationMapper;

    @Override
    public void insert(Notification notification) {
        notificationMapper.insert(notification);
    }

    @Override
    public PageInfo<NotificationDTO> list(Integer id, int pageNo, int pageSize) {
        PageHelper.startPage(pageNo, pageSize);
        List<Notification>  notifications = notificationMapper.listById(id);
        System.out.println(notifications+"+"+notifications.size());
        if(notifications.size()==0){
            PageInfo<NotificationDTO> notificationDTOPageInfo = new PageInfo(notifications);
            return notificationDTOPageInfo;
        }
        PageInfo pageInfo = new PageInfo(notifications);
        Page<NotificationDTO> notificationDTOS = new Page<>(pageInfo.getPageNum(), pageInfo.getPageSize());
        notificationDTOS.setTotal(pageInfo.getTotal());
        notificationDTOS.setReasonable(true);
        notificationDTOS.setPageSizeZero(false);
        for(Notification notification:notifications){
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification,notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
       // System.out.println(notificationDTOS);
        PageInfo<NotificationDTO> notificationDTOPageInfo = new PageInfo(notificationDTOS);
        //System.out.println(notificationDTOPageInfo);
        return notificationDTOPageInfo;
    }

    @Override
    public Long unreadCount(Integer id) {
        Long unreadCount = notificationMapper.unreadCountById(id);
        return unreadCount;
    }

    @Override
    public NotificationDTO read(Integer id, User user) {
        Notification notification = notificationMapper.findById(id);
        if(notification==null){
            log.error("回复未找到,{}",id);
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if(!Objects.equals(notification.getReceiver(),user.getId())){
            log.error("查看回复失败，查看的回复接受者跟登录者不一样");
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateById(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification,notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        System.out.println(notificationDTO);
        return notificationDTO;
    }
}
