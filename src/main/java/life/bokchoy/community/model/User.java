package life.bokchoy.community.model;

import lombok.Data;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2021/5/24
 */
@Data
public class User {
    private Integer id;
    private String name;
    private String accountId;
    private String token;
    private Long gmtCreate;
    private Long gmtModified;
    private String avatarUrl;


}
