package life.bokchoy.community.exception;

/**
 * @author bokchoy
 * @description: 异常处理
 * @date 2021年06月10日 20:49
 */
public class CustomizeException extends RuntimeException{
    private String message;

    public CustomizeException(ICustomizeErrorCode errorCode) {

        this.message = errorCode.getMessage();
    }

    public CustomizeException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
