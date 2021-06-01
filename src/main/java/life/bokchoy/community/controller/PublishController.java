package life.bokchoy.community.controller;

import life.bokchoy.community.mapper.QuestionMapper;
import life.bokchoy.community.mapper.UserMapper;
import life.bokchoy.community.model.Question;
import life.bokchoy.community.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @description:
 * @author: ManolinCoder
 * @time: 2021/5/31
 */
@Controller
public class PublishController {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/publish")
    public String publish() {
        return "publish";
    }

    @PostMapping("/publish")//前端class为publish method为post
    public String doPublic(
            @RequestParam(value = "title",required = false) String title,
            @RequestParam(value = "description",required = false) String description,
            @RequestParam(value = "tag",required = false) String tag,
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

        User user = null;
        Cookie[] cookies = request.getCookies();
        if (cookies==null) {
            return "index";
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("token")) {
                String token = cookie.getValue();
                user= userMapper.findByToken(token);
                if (user != null) {
                    request.getSession().setAttribute("user",user);
                }
                break;
            }
        }
        if (user==null) {
            model.addAttribute("error","用户未登录");
            return "publish";
        }

        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        questionMapper.create(question);
        return "redirect:/";
    }

}
