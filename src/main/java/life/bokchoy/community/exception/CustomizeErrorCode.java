package life.bokchoy.community.exception;

public enum CustomizeErrorCode implements ICustomizeErrorCode{
    QUESTION_NOT_FOUND(2001,"该问题不存在"),
    TARGET_PARAM_NOT_FOUND(2002,"未选中任何问题或评论回复"),
    NO_LOGIN(2003,"当前用户未登录，请登录"),
    SYSTEM_ERROR(2004,"服务器错误"),
    TYPE_PARAM_WRONG(2005,"评论类型错误或不存在"),
    COMMENT_NOT_FOUND(2006,"你操作的评论已删除"),
    CONTENT_IS_EMPTY(2007,"评论内容不能为空"),
    READ_NOTIFICATION_FAIL(2008,"你这是在读取别人信息？"),
    NOTIFICATION_NOT_FOUND(2009,"消息不翼而飞了？"),
    ;

    private String message;
    private Integer code;
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    CustomizeErrorCode(Integer code, String message) {
        this.message = message;
        this.code = code;
    }

}
