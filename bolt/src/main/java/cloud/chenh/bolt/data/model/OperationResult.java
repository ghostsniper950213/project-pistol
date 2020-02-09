package cloud.chenh.bolt.data.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class OperationResult implements Serializable {

    private Integer code;

    private Object data;

    private static final class CODE {

        public static final Integer SUCCESS = 200;
        public static final Integer FAILURE = 500;

    }

    public static OperationResult success() {
        OperationResult operationResult = new OperationResult();
        operationResult.setCode(CODE.SUCCESS);
        return operationResult;
    }

    public static OperationResult success(Object data) {
        OperationResult operationResult = new OperationResult();
        operationResult.setCode(CODE.SUCCESS);
        operationResult.setData(data);
        return operationResult;
    }

    public static OperationResult fail() {
        OperationResult operationResult = new OperationResult();
        operationResult.setCode(CODE.FAILURE);
        return operationResult;
    }

    public static OperationResult fail(Object data) {
        OperationResult operationResult = new OperationResult();
        operationResult.setCode(CODE.FAILURE);
        operationResult.setData(data);
        return operationResult;
    }

}
