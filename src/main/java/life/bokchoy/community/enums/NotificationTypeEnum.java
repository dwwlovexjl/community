package life.bokchoy.community.enums;

public enum NotificationTypeEnum {
    REPLAY_QUESTION(1,"回复了问题"),
    REPLAY_COMMENT(2,"回复了评论");
    private Integer type;
    private String description;

    NotificationTypeEnum(Integer type, String description) {
        this.type = type;
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public static String descriptionOfType(Integer type){
        for (NotificationTypeEnum notificationTypeEnum : NotificationTypeEnum.values()) {
            if (notificationTypeEnum.getType()==type) {
                return notificationTypeEnum.getDescription();
            }
        }
        return "该类型不存在";
    }
}
