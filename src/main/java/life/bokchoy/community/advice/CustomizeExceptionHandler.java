package life.bokchoy.community.advice;

import com.alibaba.fastjson.JSON;
import life.bokchoy.community.dto.ResultDTO;
import life.bokchoy.community.exception.CustomizeErrorCode;
import life.bokchoy.community.exception.CustomizeException;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author bokchoy
 * @description: 异常处理
 * @date 2021年06月10日 20:34
 */
@ControllerAdvice
public class CustomizeExceptionHandler {
    @ExceptionHandler(Exception.class)
    Object handle(HttpServletRequest request, HttpServletResponse response, Throwable ex, Model model) {

        String contentType = request.getContentType();
        if("application/json".equals(contentType)){
            ResultDTO resultDTO;
            //返回json
            if (ex instanceof CustomizeException){
                resultDTO= ResultDTO.errorOf((CustomizeException) ex);
            }else {
                resultDTO= ResultDTO.errorOf(CustomizeErrorCode.SYSTEM_ERROR);
            }
            try {
                response.setContentType("application/json");
                response.setStatus(200);
                response.setCharacterEncoding("UTF-8");
                PrintWriter writer = response.getWriter();
                writer.write(JSON.toJSONString(resultDTO));
                writer.close();
            } catch (IOException IO) {
            }
            return null;
        }else {
            //错误页面跳转
            if(ex instanceof CustomizeException){
                model.addAttribute("message",ex.getMessage());
            }else {
                model.addAttribute("message",CustomizeErrorCode.SYSTEM_ERROR.getMessage());
            }

            return new ModelAndView("error");
        }


    }

}
