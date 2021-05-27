package RPC02;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 传输协议
 *  请求信息：
 *      服务端需要哪些信息才能唯一确定服务端需要调用的接口的方法呢？
 *          --> 接口的名字，和方法的名字，但是由于方法重载的缘故，我们还需要这个方法的所有参数的类型，
 *              客户端调用时，还需要传递参数的实际值
 */
@Data
@Builder
public class RPCRequest implements Serializable {
    // 待调用接口的名字
    private String interfaceName;

    // 待调用方法的名字
    private String methodName;

    // 待调用方法的参数
    private Object[] parameters;

    // 待调用方法的参数类型(这里用的是Class，也可以使用字符串)
    private Class<?>[] paramTypes;

}
