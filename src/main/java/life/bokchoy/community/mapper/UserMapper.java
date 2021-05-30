package life.bokchoy.community.mapper;

import life.bokchoy.community.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2021/5/24
 */
@Mapper
public interface  UserMapper {
    @Insert("insert into user (name,account_id,token,gmt_create,gmt_modified) " +
            "values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified})")
    void insert(User user);//

    @Select("select * from user where token=#{token}")
    User findByToken(@Param("token") String token);//当形参不是自定义类时需要加注解@Param
}
