package cloud.chenh.bolt.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationResult<T> {

    private Integer code;

    private T data;

    @Getter
    public enum Type {
        OK(200),

        NO(201),

        LOST(400),

        UNSUPPORT(401),

        ERROR(500),

        EMPTY(501),

        UNAUTH(300),

        STRANGER(301);

        private final Integer code;

        Type(Integer code) {
            this.code = code;
        }
    }

    public static <T> OperationResult<T> build(Type type, T data) {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(type.getCode());
        operationResult.setData(data);
        return operationResult;
    }

    public static <T> OperationResult<T> ok() {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.OK.getCode());
        return operationResult;
    }

    public static <T> OperationResult<T> ok(T data) {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.OK.getCode());
        operationResult.setData(data);
        return operationResult;
    }

    public static <T> OperationResult<T> no() {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.NO.getCode());
        return operationResult;
    }

    public static <T> OperationResult<T> no(T data) {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.NO.getCode());
        operationResult.setData(data);
        return operationResult;
    }

    public static <T> OperationResult<T> lost() {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.LOST.getCode());
        return operationResult;
    }

    public static <T> OperationResult<T> lost(T data) {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.LOST.getCode());
        operationResult.setData(data);
        return operationResult;
    }

    public static <T> OperationResult<T> unsupport() {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.UNSUPPORT.getCode());
        return operationResult;
    }

    public static <T> OperationResult<T> unsupport(T data) {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.UNSUPPORT.getCode());
        operationResult.setData(data);
        return operationResult;
    }

    public static <T> OperationResult<T> error() {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.ERROR.getCode());
        return operationResult;
    }

    public static <T> OperationResult<T> error(T data) {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.ERROR.getCode());
        operationResult.setData(data);
        return operationResult;
    }

    public static <T> OperationResult<T> empty() {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.EMPTY.getCode());
        return operationResult;
    }

    public static <T> OperationResult<T> empty(T data) {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.EMPTY.getCode());
        operationResult.setData(data);
        return operationResult;
    }

    public static <T> OperationResult<T> unauth() {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.UNAUTH.getCode());
        return operationResult;
    }

    public static <T> OperationResult<T> unauth(T data) {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.UNAUTH.getCode());
        operationResult.setData(data);
        return operationResult;
    }

    public static <T> OperationResult<T> stranger() {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.STRANGER.getCode());
        return operationResult;
    }

    public static <T> OperationResult<T> stranger(T data) {
        OperationResult<T> operationResult = new OperationResult<>();
        operationResult.setCode(Type.STRANGER.getCode());
        operationResult.setData(data);
        return operationResult;
    }

}
