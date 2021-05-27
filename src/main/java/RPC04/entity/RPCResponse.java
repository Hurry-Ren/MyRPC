package RPC04.entity;

import RPC04.ExceptionAndCode.ResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * 传输协议
 *  返回信息：
 *      服务器调用完后，需要返回给客户端的信息
 *          --> 如果调用成功的话，显然需要返回值；如果调用失败了，就需要失败的信息。
 *              这里封装成一个RPCResponse对象。
 */
@Data
@AllArgsConstructor
public class RPCResponse<T> implements Serializable {

    public RPCResponse(){}


    /**
     * 利用请求号对服务端返回的响应数据进行校验，保证请求与响应一一对应
     */
    // 响应对应的请求号
    private String requestId;

    // 响应状态码
    private Integer statusCode;

    // 响应状态补充信息
    private String message;

    // 响应的数据
    private T Data;

    // 快速生成成功的响应对象
    public static <T> RPCResponse<T> success(T data, String requestId){
        RPCResponse<T> response = new RPCResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(ResponseCode.SUCCESS.getStatueCode());
        response.setData(data);
        return response;
    }

    // 失败的响应对象
    public static <T> RPCResponse<T> fail(ResponseCode responseCode, String requestId){
        RPCResponse<T> response = new RPCResponse<>();
        response.setRequestId(requestId);
        response.setStatusCode(responseCode.getStatueCode());
        response.setMessage(responseCode.getMessage());
        return response;
    }
}
