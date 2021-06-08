package life.bokchoy.community.mapper;

import life.bokchoy.community.model.User;
import org.apache.ibatis.annotations.*;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2021/5/24
 */
@Mapper
public interface  UserMapper {

    @Insert("insert into user (name,account_id,token,gmt_create,gmt_modified,avatar_url) " +
            "values (#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    void insert(User user);

    @Select("select * from user where token=#{token}")
    User findByToken(@Param("token") String token);//当形参不是自定义类时需要加注解@Param

    @Select("select * from user where id=#{id}")
    User findById(@Param("id") Integer id);

    @Select("select * from user where account_id=#{accountId}")
    User findByAccountId(@Param("accountId") String accountId);

    @Update("update user set name=#{name},token=#{token},gmt_modified=#{gmtModified},avatar_url=#{avatarUrl} " +
            "where account_id=#{accountId}")
    void update(User user);
}
