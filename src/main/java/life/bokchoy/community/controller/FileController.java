package life.bokchoy.community.controller;

import life.bokchoy.community.dto.FileDTO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author bokchoy
 * @description: 上传文件
 * @date 2021年06月28日 17:37
 */
@Controller
public class FileController {
    @RequestMapping("/file/upload")
    @ResponseBody
    public FileDTO upload(){
        FileDTO fileDTO = new FileDTO();
        fileDTO.setSuccess(1);
        fileDTO.setUrl("images/img/test.jpg");
        return fileDTO;
    }
}
