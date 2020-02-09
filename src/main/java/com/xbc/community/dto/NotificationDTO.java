package com.xbc.community.dto;

import lombok.Data;

@Data
public class NotificationDTO {
    private Integer id;
    private Long gmtCreate;
    private Integer status;
    private Integer notifier;
    private String typeName;
    private Integer outerid;
    private String notifierName;
    private String outerTitle;
    private Integer type;
}
