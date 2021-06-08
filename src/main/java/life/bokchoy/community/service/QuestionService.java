package life.bokchoy.community.service;

import life.bokchoy.community.dto.PaginationDTO;
import life.bokchoy.community.dto.QuestionDTO;
import life.bokchoy.community.mapper.QuestionMapper;
import life.bokchoy.community.mapper.UserMapper;
import life.bokchoy.community.model.Question;
import life.bokchoy.community.model.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    private UserMapper userMapper;


    public PaginationDTO list(Integer page, Integer size) {
        Integer totalCount = questionMapper.count();

        Integer totalPage;
        if (totalCount%size==0) {
            totalPage=totalCount/size;
        }else{
            totalPage=totalCount/size+1;
        }

        if(page<1){
            page=1;
        }
        if(page>totalPage){
            page=totalPage;
        }
        //size*(page-1)
        Integer offset=size*(page-1);
        List<Question> questions = questionMapper.list(offset,size);
        ArrayList<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();
        for (Question question : questions) {
            User user=userMapper.findById(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);//将question所有属性拷贝至questionDTO
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);

        }
        paginationDTO.setQuestionDTOS(questionDTOList);
        paginationDTO.setPagination(totalPage,page);

        return paginationDTO;
    }

    public PaginationDTO listByUserId(Integer userId, Integer page, Integer size) {

        Integer totalCount = questionMapper.countByUserId(userId);
        Integer totalPage;
        if (totalCount%size==0) {
            totalPage=totalCount/size;
        }else{
            totalPage=totalCount/size+1;
        }

        if(page<1){
            page=1;
        }
        if(page>totalPage){
            page=totalPage;
        }
        //size*(page-1)
        Integer offset=size*(page-1);
        List<Question> questions = questionMapper.listByUserId(userId,offset,size);
        ArrayList<QuestionDTO> questionDTOList = new ArrayList<>();
        PaginationDTO paginationDTO = new PaginationDTO();
        User user=userMapper.findById(userId);
        for (Question question : questions) {
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);//将question所有属性拷贝至questionDTO
            questionDTO.setUser(user);
            questionDTOList.add(questionDTO);
        }
        paginationDTO.setQuestionDTOS(questionDTOList);
        paginationDTO.setPagination(totalPage,page);

        return paginationDTO;
    }


    public QuestionDTO getQuestionDTOByQuestionId(Integer id) {
        QuestionDTO questionDTO=new QuestionDTO();
        Question question=questionMapper.getById(id);
        Integer userId=question.getCreator();
        User user=userMapper.findById(userId);
        BeanUtils.copyProperties(question,questionDTO);
        questionDTO.setUser(user);
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if (question.getId()==null) {
            //创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.create(question);
        }else{
            //更新
            question.setGmtModified(System.currentTimeMillis());
            questionMapper.modified(question);
        }
    }
}
