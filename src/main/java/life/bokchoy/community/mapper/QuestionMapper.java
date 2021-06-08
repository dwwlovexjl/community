package life.bokchoy.community.mapper;

import life.bokchoy.community.dto.QuestionDTO;
import life.bokchoy.community.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2021/5/31
 */

@Mapper
public interface QuestionMapper {

    @Insert("insert into question (title,description,gmt_create,gmt_modified,creator,tag) " +
            "values(#{title},#{description},#{gmtCreate},#{gmtModified},#{creator},#{tag})")
    void create(Question question);

    @Select("select * from question Limit #{offset},#{size}")
    List<Question> list(Integer offset, Integer size);

    @Select("select count(1) from question")
    Integer count();

    @Select("select * from question where creator= #{userId} Limit #{offset},#{size}")
    List<Question> listByUserId(Integer userId, Integer offset, Integer size);

    @Select("select count(1) from question where creator= #{userId}")
    Integer countByUserId(Integer userId);

    @Select("select * from question where id=#{id}")
    Question getById(Integer id);
}
