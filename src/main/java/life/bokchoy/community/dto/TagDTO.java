package life.bokchoy.community.dto;

import lombok.Data;

import java.util.List;

/**
 * @author bokchoy
 * @description: 标签
 * @date 2021年06月25日 14:18
 */
@Data
public class TagDTO {
    private String categoryName;
    private List<String> tags;
}
