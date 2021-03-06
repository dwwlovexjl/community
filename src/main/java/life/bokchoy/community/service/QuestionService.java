package life.bokchoy.community.service;

import life.bokchoy.community.dto.PaginationDTO;
import life.bokchoy.community.dto.QuestionDTO;
import life.bokchoy.community.exception.CustomizeErrorCode;
import life.bokchoy.community.exception.CustomizeException;
import life.bokchoy.community.mapper.QuestionExtMapper;
import life.bokchoy.community.mapper.QuestionMapper;
import life.bokchoy.community.mapper.UserMapper;
import life.bokchoy.community.model.Question;
import life.bokchoy.community.model.QuestionExample;
import life.bokchoy.community.model.User;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bokchoy
 * @description:
 * @date 2021年06月01日 22:41
 */
@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private QuestionExtMapper questionExtMapper;
    @Autowired
    private UserMapper userMapper;


    public PaginationDTO list(Integer page, Integer size) {
        Integer totalCount = (int) questionMapper.countByExample(new QuestionExample());
//        Integer totalCount = questionMapper.count();

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
        //size*(page-1)
        Integer offset = size * (page - 1);
        QuestionExample questionExample = new QuestionExample();
        questionExample.setOrderByClause("gmt_create desc");
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(
                questionExample, new RowBounds(offset, size));
//        List<Question> questions = questionMapper.list(offset,size);
//        Collections.sort(questions, new Comparator<Question>() {
//            @Override
//            public int compare(Question o1, Question o2) {
//                return (int)(o1.getGmtModified()-o2.getGmtModified());
//            }
//        });
        ArrayList<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<>();
        for (Question question : questions) {
            User user = userMapper.selectByPrimaryKey(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);//将question所有属性拷贝至questionDTO
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);

        }
        paginationDTO.setData(questionDTOList);
        paginationDTO.setPagination(totalPage, page);

        return paginationDTO;
    }

    public PaginationDTO listByUserId(Long userId, Integer page, Integer size) {

        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andCreatorEqualTo(userId);
        Integer totalCount = (int) questionMapper.countByExample(questionExample);
//        Integer totalCount = questionMapper.countByUserId(userId);
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
        //size*(page-1)
        Integer offset = size * (page - 1);
        QuestionExample example = new QuestionExample();
        example.createCriteria().andCreatorEqualTo(userId);
        List<Question> questions = questionMapper.selectByExampleWithRowbounds(
                example, new RowBounds(offset, size));
//        List<Question> questions = questionMapper.listByUserId(userId,offset,size);
        ArrayList<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO<QuestionDTO> paginationDTO = new PaginationDTO<QuestionDTO>();
        User user = userMapper.selectByPrimaryKey(userId);
        for (Question question : questions) {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question, questionDTO);//将question所有属性拷贝至questionDTO
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setData(questionDTOList);
        paginationDTO.setPagination(totalPage, page);

        return paginationDTO;
    }


    public QuestionDTO getQuestionDTOByQuestionId(Long id) {
        QuestionDTO questionDTO = new QuestionDTO();
        if (questionDTO == null) {
            throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
        }
        Question question = questionMapper.selectByPrimaryKey(id);
        Long userId = question.getCreator();
        User user = userMapper.selectByPrimaryKey(userId);
        BeanUtils.copyProperties(question, questionDTO);
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId() == null) {
            //创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            question.setCommentCount(0);
            question.setLikeCount(0);
            question.setViewCount(0);
//            questionMapper.insert(question);
            //insertSelective可以处理默认值
            questionMapper.insertSelective(question);
        } else {
            //更新
            Question updateQuestion = new Question();
            updateQuestion.setGmtModified(System.currentTimeMillis());
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            QuestionExample example = new QuestionExample();
            example.createCriteria().andIdEqualTo(question.getId());
            int updated = questionMapper.updateByExampleSelective(updateQuestion, example);
            if (updated != 1) {
                throw new CustomizeException(CustomizeErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }

    public void incView(Long id) {

        Question question = new Question();
        question.setId(id);
        question.setViewCount(1);
        questionExtMapper.incView(question);
    }

    public List<QuestionDTO> selectRelated(QuestionDTO queryDTO) {
        if (StringUtils.isBlank(queryDTO.getTag())) {
            return new ArrayList<>();
        }
        //String[] tags = StringUtils.split(queryDTO.getTag(), ',');
        //String replacedTag = Arrays.stream(tags).collect(Collectors.joining("|"));
        String replacedTag = StringUtils.replace(queryDTO.getTag(), ",", "|");
        Question question = new Question();
        question.setId(queryDTO.getId());
        question.setTag(replacedTag);
        List<Question> questions = questionExtMapper.selectRelated(question);
        List<QuestionDTO> questionDTOS = questions.stream().map(q -> {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(q, questionDTO);
            return questionDTO;
        }).collect(Collectors.toList());
        return questionDTOS;

    }
}
