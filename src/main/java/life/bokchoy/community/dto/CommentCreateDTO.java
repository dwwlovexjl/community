package life.bokchoy.community.dto;

import lombok.Data;

/**
 * @author bokchoy
 * @description:
 * @date 2021年06月18日 13:00
 */
@Data
public class CommentCreateDTO {
    private Long parentId;
    private String content;
    private Integer type;
}
