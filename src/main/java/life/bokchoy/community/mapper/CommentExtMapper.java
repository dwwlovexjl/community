package life.bokchoy.community.mapper;

import life.bokchoy.community.model.Comment;

public interface CommentExtMapper {
    int incCommentCount(Comment record);

}