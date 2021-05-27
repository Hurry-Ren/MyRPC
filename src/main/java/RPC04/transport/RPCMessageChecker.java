package RPC04.transport;

import RPC04.ExceptionAndCode.RPCError;
import RPC04.ExceptionAndCode.RPCException;
import RPC04.ExceptionAndCode.ResponseCode;
import RPC04.entity.RPCRequest;
import RPC04.entity.RPCResponse;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 检查响应和请求
 */
@NoArgsConstructor
public class RPCMessageChecker {

    private static final Logger logger = LoggerFactory.getLogger(RPCMessageChecker.class);

    private static final String INTERFACE_NAME = "interfaceName";

    public static void check(RPCRequest rpcRequest, RPCResponse rpcResponse){
        if (rpcResponse == null){
            logger.error("调用服务失败， serviceName：{}", rpcRequest.getInterfaceName());
            throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        // 响应与请求的请求号不同
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())){
            throw new RPCException(RPCError.RESPONSE_NOT_MATCH, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
        // 调用失败
        if (rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getStatueCode())){
            logger.error("调用服务失败，serviceName：{}，RPCResponse: {}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, INTERFACE_NAME + ":" + rpcRequest.getInterfaceName());
        }
    }
}
