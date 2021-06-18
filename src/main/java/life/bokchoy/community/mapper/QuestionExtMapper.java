package life.bokchoy.community.mapper;

import life.bokchoy.community.model.Question;
import life.bokchoy.community.model.QuestionExample;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface QuestionExtMapper {

    int incView(Question record);
}