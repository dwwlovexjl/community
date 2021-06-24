package life.bokchoy.community.dto;

import life.bokchoy.community.model.User;
import lombok.Data;

/**
 * @author bokchoy
 * @description:
 * @date 2021年06月18日 13:00
 */
@Data
public class CommentDTO {
    private Long id;

    private Long parentId;

    private Integer type;

    private Long commentator;

    private Long gmtCreate;

    private Long gmtModified;

    private Long likeCount;

    private Integer commentCount;

    private String content;

    private User user;
}
