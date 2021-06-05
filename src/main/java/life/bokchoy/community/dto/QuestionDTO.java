package life.bokchoy.community.dto;

import life.bokchoy.community.model.User;
import lombok.Data;

/**
 * @author bokchoy
 * @data 2021年06月1日
 *
 */
@Data
public class QuestionDTO {
    private Integer id;
    private String title;
    private String description;
    private String tag;
    private Long gmtCreate;
    private Long gmtModified;
    private Integer creator;
    private Integer viewCount;
    private Integer commentCount;
    private Integer likeCount;
    private User user;
}
