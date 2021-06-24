package life.bokchoy.community.controller;

import life.bokchoy.community.dto.CommentDTO;
import life.bokchoy.community.dto.QuestionDTO;
import life.bokchoy.community.enums.CommentTypeEnum;
import life.bokchoy.community.service.CommentService;
import life.bokchoy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author bokchoy
 * @description: 问题详情页控制器
 * @date 2021年06月08日 12:59
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Long id,
                           Model model){
        QuestionDTO questionDTO=questionService.getQuestionDTOByQuestionId(id);

        List<CommentDTO> commentList = commentService.listByParentId(id, CommentTypeEnum.QUESTION);
        //增加阅读数
        questionService.incView(id);
        model.addAttribute("question" ,questionDTO);
        model.addAttribute("comments" ,commentList);
        return "question";

    }
}
