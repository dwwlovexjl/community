package life.bokchoy.community.dto;

import lombok.Data;

/**
 * @author bokchoy
 * @description: 文件格式
 * @date 2021年06月28日 17:38
 */
@Data
public class FileDTO {
    private int success;         // 0 表示上传失败，1 表示上传成功
    private String  message; //"提示的信息，上传成功或上传失败及错误信息等。",
    private String url;      // "图片地址"   上传成功时才返回
}
