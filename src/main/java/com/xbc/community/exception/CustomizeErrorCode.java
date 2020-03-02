package com.xbc.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode {

    QUESTION_NOT_FOUND(2001,"你找的问题不在了，要不换个试试?"),
    TARGET_PARAM_NOT_FOUND(2002,"未选中任何问题或评论进行回复"),
    NO_LOGIN(2003,"当前操作需要登录，请登录后重试"),
    SYS_ERROR(2004,"服务器冒烟了，要不你稍后试试"),
    TYPE_PARAM_ERROR(2005,"评论类型错误"),
    COMMENT_NOT_FOUND(2006,"回复的评论不存在"),
    COMMENT_INSERT_FAILED(2007,"回复失败"),
    COMMENT_IS_EMPTY(2008,"输入的评论内容不能为空"),
    READ_NOTIFICATION_FAIL(2009,"读别人的信息"),
    NOTIFICATION_NOT_FOUND(2010,"消息不见了"),
    FILE_UPLOAD_FAIL(2011,"文件上传失败"),
    USER_USERNAME_IS_EMPTY(2012,"用户名不能为空"),
    USER_PASSWORD_IS_EMPTY(2013,"密码不能为空"),
    USER_IS_EXIST(2014,"用户已存在，请重新输入用户名"),
    CHANGE_USERINFO_FAILED(2015,"修改个人资料失败"),
    TAG_IS_EMPTY(2016,"类别或分类名称为空");



    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    private Integer code;
    private String message;


    CustomizeErrorCode(Integer code,String message ) {
        this.message = message;
        this.code = code;
    }

}
