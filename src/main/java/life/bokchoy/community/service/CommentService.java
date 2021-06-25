package life.bokchoy.community.service;

import life.bokchoy.community.dto.CommentDTO;
import life.bokchoy.community.enums.CommentTypeEnum;
import life.bokchoy.community.enums.NotificationStatusEnum;
import life.bokchoy.community.enums.NotificationTypeEnum;
import life.bokchoy.community.exception.CustomizeErrorCode;
import life.bokchoy.community.exception.CustomizeException;
import life.bokchoy.community.mapper.*;
import life.bokchoy.community.model.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author bokchoy
 * @description:
 * @date 2021年06月18日 17:05
 */
@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;

    @Autowired
    private CommentExtMapper commentExtMapper;

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private NotificationMapper notificationMapper;

    @Transactional//方法体加上事务
    public void insert(Comment comment) {
        if(comment.getParentId()==null||comment.getParentId()==0){
            throw new CustomizeException(CustomizeErrorCode.TARGET_PARAM_NOT_FOUND);
        }
        if(comment.getType()==null||!CommentTypeEnum.isExist(comment.getType())){
            throw new CustomizeException(CustomizeErrorCode.TYPE_PARAM_WRONG);
        }
        if(comment.getType()==CommentTypeEnum.COMMENT.getType()){
            //回复评论
            Comment dbComment = commentMapper.selectByPrimaryKey(comment.getParentId());
            if(dbComment==null){
                throw new CustomizeException(CustomizeErrorCode.COMMENT_NOT_FOUND);
            }
            commentMapper.insert(comment);
            //增加评论数
            dbComment.setCommentCount(1);
            commentExtMapper.incCommentCount(dbComment);
            //创建通知
            createNotify(comment.getCommentator(), dbComment.getCommentator(), NotificationTypeEnum.REPLAY_COMMENT, comment.getParentId());
        }else{
            //回复问题
            Question question = questionMapper.selectByPrimaryKey(comment.getParentId());
            if (question==null) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
            commentMapper.insert(comment);
            question.setCommentCount(1);
            questionExtMapper.incCommentCount(question);
            //创建通知
            createNotify(comment.getCommentator(), question.getCreator(), NotificationTypeEnum.REPLAY_QUESTION, comment.getParentId());
        }
    }

    private void createNotify(Long notifier, Long receiver, NotificationTypeEnum type, Long outerId) {
        /*
         * 插入通知数据
         * @author bokchoy
         * @date 2021/6/25 17:04
         * @param notifier 回复的发起人
         * @param receiver 要通知的人
         * @param outerId 回复的目标id question id or comment id
         * @param type 回复的类型，问题或者评论
         */
        Notification notification = new Notification();
        notification.setGmtCreate(System.currentTimeMillis());
        notification.setNotifier(notifier);//发起者id
        notification.setReceiver(receiver);//接收者id
        notification.setOuterId(outerId);//所评论的问题id
        notification.setType(type.getType());//回复的是评论，还是问题
        notification.setStatus(NotificationStatusEnum.UNREAD.getStatus());//是否已读
        notificationMapper.insert(notification);
    }

    public List<CommentDTO> listByParentId(Long id, CommentTypeEnum type) {
        CommentExample commentExample = new CommentExample();
        commentExample.createCriteria().andParentIdEqualTo(id).andTypeEqualTo(type.getType());
        commentExample.setOrderByClause("gmt_create desc");
        List<Comment> commentList = commentMapper.selectByExample(commentExample);

        if (commentList.size()==0) {
            return new ArrayList<>();
        }
        //获取去重的评论人
        Set<Long> commentators = commentList.stream().map(comment -> comment.getCommentator()).collect(Collectors.toSet());
        List<Long> userIds=new ArrayList<>();
        userIds.addAll(commentators);

        //获取评论人并转为Map
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(userIds);
        List<User> users = userMapper.selectByExample(userExample);
        Map<Long, User> userMap = users.stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //转换comment为commentDTO
        List<CommentDTO> commentDTOS = commentList.stream().map(comment -> {
            CommentDTO commentDTO = new CommentDTO();
            BeanUtils.copyProperties(comment,commentDTO);
            commentDTO.setUser(userMap.get(comment.getCommentator()));
            return commentDTO;
        }).collect(Collectors.toList());
        return commentDTOS;
    }
}
