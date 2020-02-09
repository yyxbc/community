package com.xbc.community.service;

import com.github.pagehelper.PageInfo;
import com.xbc.community.bean.Notification;
import com.xbc.community.bean.User;
import com.xbc.community.dto.NotificationDTO;

public interface NotificationService {
    void insert(Notification notification);

    PageInfo<NotificationDTO> list(Integer id, int pageNo, int pageSize);

    Long unreadCount(Integer id);

    NotificationDTO read(Integer id, User user);
}
