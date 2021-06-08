package life.bokchoy.community.controller;

import life.bokchoy.community.dto.QuestionDTO;
import life.bokchoy.community.mapper.QuestionMapper;
import life.bokchoy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author bokchoy
 * @description: 问题详情页控制器
 * @date 2021年06月08日 12:59
 */
@Controller
public class QuestionController {

    @Autowired
    private QuestionService questionService;
    @GetMapping("/question/{id}")
    public String question(@PathVariable(name = "id") Integer id,
                           Model model){
        QuestionDTO questionDTO=questionService.getQuestionDTOByQuestionId(id);
        model.addAttribute("question" ,questionDTO);
        return "question";

    }
}
