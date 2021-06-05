package life.bokchoy.community.model;

import lombok.Data;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2021/5/31
 */
@Data
public class Question {
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
}
