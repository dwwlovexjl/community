package life.bokchoy.community.controller;

import life.bokchoy.community.dto.NotificationDTO;
import life.bokchoy.community.dto.PaginationDTO;
import life.bokchoy.community.model.Notification;
import life.bokchoy.community.model.User;
import life.bokchoy.community.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author bokchoy
 * @description: 通知控制
 * @date 2021年06月25日 23:16
 */
@Controller
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @GetMapping("/notification/{id}")
    public String profile(HttpServletRequest request,
                          @PathVariable(name = "id") Long id
                          ){

        User user =(User) request.getSession().getAttribute("user");
        if (user==null) {
            return "redirect:/";
        }
        NotificationDTO notificationDTO=notificationService.read(id,user);//读取通知

        return "redirect:/question/"+notificationDTO.getQuestionId();
    }
}
