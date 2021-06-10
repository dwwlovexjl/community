package life.bokchoy.community.controller;

import life.bokchoy.community.dto.QuestionDTO;
import life.bokchoy.community.model.Question;
import life.bokchoy.community.model.User;
import life.bokchoy.community.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bokchoy
 * @description: 问题发布控制器
 * @date 2021年06月05日 20:35
 */
@Controller
public class PublishController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/publish")
    public String publish() {
        return "publish";
    }

    @GetMapping("/publish/{id}")//前端class为publish method为post
    public String edit(
            @PathVariable("id") Integer id,
            Model model){

        QuestionDTO questionDTO=questionService.getQuestionDTOByQuestionId(id);
//        model.addAttribute("question" ,questionDTO);
        model.addAttribute("title",questionDTO.getTitle());//设置model后前端能够获取 ${title}
        model.addAttribute("description",questionDTO.getDescription());
        model.addAttribute("tag",questionDTO.getTag());
        model.addAttribute("id",questionDTO.getId());
        return "publish";
    }

    @PostMapping("/publish")//前端class为publish method为post
    public String doPublish(
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "description",required = false) String description,
            @RequestParam(value = "tag",required = false) String tag,
            @RequestParam(value = "id",required = false) Integer id,
            HttpServletRequest request,
            Model model){

        model.addAttribute("title",title);//设置model后前端能够获取 ${title}
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);
        if (title==null||title=="") {
            model.addAttribute("error","标题不能为空！");
            return "publish";
        }
        if (description==null||description=="") {
            model.addAttribute("error","内容不能为空！");
            return "publish";
        }
        if (tag==null||tag=="") {
            model.addAttribute("error","标签不能为空！");
            return "publish";
        }

        User user =(User) request.getSession().getAttribute("user");

        if (user==null) {
            model.addAttribute("error","用户未登录");
            return "publish";
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setId(id);
        questionService.createOrUpdate(question);
//        questionMapper.create(question);
        return "redirect:/";
    }



}
