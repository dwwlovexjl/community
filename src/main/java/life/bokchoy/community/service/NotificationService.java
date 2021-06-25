package life.bokchoy.community.service;

import life.bokchoy.community.dto.NotificationDTO;
import life.bokchoy.community.dto.PaginationDTO;
import life.bokchoy.community.enums.NotificationStatusEnum;
import life.bokchoy.community.enums.NotificationTypeEnum;
import life.bokchoy.community.exception.CustomizeErrorCode;
import life.bokchoy.community.exception.CustomizeException;
import life.bokchoy.community.mapper.CommentMapper;
import life.bokchoy.community.mapper.NotificationMapper;
import life.bokchoy.community.mapper.QuestionMapper;
import life.bokchoy.community.mapper.UserMapper;
import life.bokchoy.community.model.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author bokchoy
 * @description: 通知
 * @date 2021年06月25日 17:28
 */
@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private QuestionMapper questionMapper;

    public PaginationDTO listByUserId(Long userId, Integer page, Integer size) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId);
        Integer totalCount = (int) notificationMapper.countByExample(notificationExample);
        Integer totalPage;
        if (totalCount % size == 0) {
            totalPage = totalCount / size;
        } else {
            totalPage = totalCount / size + 1;
        }

        if (page < 1) {
            page = 1;
        }
        if (page > totalPage) {
            page = totalPage;
        }
        PaginationDTO<NotificationDTO> paginationDTO = new PaginationDTO<NotificationDTO>();
        paginationDTO.setPagination(totalPage, page);
        //size*(page-1)
        Integer offset = size * (page - 1);

        NotificationExample example = new NotificationExample();
        example.createCriteria().andReceiverEqualTo(userId);
        example.setOrderByClause("gmt_create desc");
        List<Notification> notifications = notificationMapper.selectByExampleWithRowbounds(
                example, new RowBounds(offset, size));
        if (notifications.size() == 0) {
            return paginationDTO;
        }
        //去重
        Set<Long> disNotifierUserId = notifications.stream().map(notification -> notification.getNotifier()).collect(Collectors.toSet());
        ArrayList<Long> notifierUserIds = new ArrayList<>(disNotifierUserId);
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdIn(notifierUserIds);
        AtomicReference<List<User>> notifierUsers = new AtomicReference<>(userMapper.selectByExample(userExample));//获取不重复的发送消息的user对象
        Map<Long, User> notifierUserMap = notifierUsers.get().stream().collect(Collectors.toMap(user -> user.getId(), user -> user));

        //获取相关问题
        List<Long> questionIds = notifications.stream().filter(new Predicate<Notification>() {
            @Override
            public boolean test(Notification notification) {
                return notification.getType() == NotificationTypeEnum.REPLAY_QUESTION.getType();
            }
        }).map(notification -> notification.getOuterId()).collect(Collectors.toList());
        //获取评价comment
        List<Long> commentIdList = notifications.stream().filter(new Predicate<Notification>() {
            @Override
            public boolean test(Notification notification) {
                return notification.getType() == NotificationTypeEnum.REPLAY_COMMENT.getType();
            }
        }).map(notification -> notification.getOuterId()).collect(Collectors.toList());

        //添加评论相关问题ID
        Map<Long, Long> comment2questionMap;
        if (commentIdList.size() != 0) {
            CommentExample commentExample = new CommentExample();
            commentExample.createCriteria().andIdIn(commentIdList);
            List<Comment> comments = commentMapper.selectByExample(commentExample);
            comment2questionMap = comments.stream().collect(Collectors.toMap(comment -> comment.getId(), comment -> comment.getParentId()));
            questionIds.addAll(comment2questionMap.values());
        } else {
            comment2questionMap = new HashMap<Long, Long>();
        }

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andIdIn(questionIds);
        List<Question> questions = questionMapper.selectByExample(questionExample);
        Map<Long, String> questionId2TitleMap = questions.stream().collect(Collectors.toMap(question -> question.getId(), question -> question.getTitle()));


        List<NotificationDTO> notificationDTOS = notifications.stream().map(notification -> {
            NotificationDTO notificationDTO = new NotificationDTO();
            User notifierUser = notifierUserMap.get(notification.getNotifier());
            notificationDTO.setNotifierName(notifierUser.getName());
            //获取所关联的问题Id
            Long questionId = notification.getType() == NotificationTypeEnum.REPLAY_COMMENT.getType() ? comment2questionMap.get(notification.getOuterId()) : notification.getOuterId();
            String questionTitle = questionId2TitleMap.get(questionId);
            notificationDTO.setQuestionTitle(questionTitle);
            notificationDTO.setQuestionId(questionId);
            notificationDTO.setId(notification.getId());
            notificationDTO.setGmtCreate(notification.getGmtCreate());
            notificationDTO.setStatus(notification.getStatus());
            notificationDTO.setReplyDescription(NotificationTypeEnum.descriptionOfType(notification.getType()));
            return notificationDTO;
        }).collect(Collectors.toList());

        /*ArrayList<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            User notifierUser = notifierUserMap.get(notification.getNotifier());
            notificationDTO.setNotifierName(notifierUser.getName());
            //获取所关联的问题Id
            Long questionId = notification.getType() == NotificationTypeEnum.REPLAY_COMMENT.getType() ? comment2questionMap.get(notification.getOuterId()) : notification.getOuterId();
            String questionTitle = questionId2TitleMap.get(questionId);
            notificationDTO.setQuestionTitle(questionTitle);
            notificationDTO.setQuestionId(questionId);
            notificationDTOS.add(notificationDTO);
        }*/
        paginationDTO.setData(notificationDTOS);
        return paginationDTO;
    }

    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andReceiverEqualTo(userId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }

    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!notification.getReceiver().equals(user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }
        //标记已读
        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);
        Long questionId = notification.getOuterId();
        if (notification.getType() == NotificationTypeEnum.REPLAY_COMMENT.getType()) {
            Comment comment = commentMapper.selectByPrimaryKey(questionId);
            questionId = comment.getParentId();
        }
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setQuestionId(questionId);
        return notificationDTO;
    }
}
