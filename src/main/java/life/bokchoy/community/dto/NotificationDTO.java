package life.bokchoy.community.dto;

import lombok.Data;

/**
 * @author bokchoy
 * @description: 存储notification 与用户
 * @date 2021年06月25日 17:32
 */
@Data
public class NotificationDTO {
    private Long id;
    private Long gmtCreate;
    private String notifierName;//发送消息的人名
    private String questionTitle;//相关问题标题
    private Long questionId;//相关问题id
    private String replyDescription;//回复类型，回复的是问题还是评论
    private Integer status;
}
