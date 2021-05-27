package RPC04.handler;

import RPC04.ExceptionAndCode.ResponseCode;
import RPC04.entity.RPCRequest;
import RPC04.entity.RPCResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public Object handle(RPCRequest request, Object service){
        Object result = null;
        try {
            result = invokeTargetMethod(request, service);
            logger.info("服务:{}成功调用方法:{}",request.getInterfaceName(), request.getMethodName());
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.info("调用或发送时出现错误：" + e);
        }
        return RPCResponse.success(result, request.getRequestId());
    }

    private Object invokeTargetMethod(RPCRequest request, Object service) throws InvocationTargetException, IllegalAccessException {
        Method method;
        try {
            method = service.getClass().getMethod(request.getMethodName(), request.getParamTypes());
        } catch (NoSuchMethodException e) {
           return RPCResponse.fail(ResponseCode.METHOD_NOT_FOUND, request.getRequestId());
        }
        return method.invoke(service, request.getParameters());
    }
}
