package RPC03.transport;

import RPC03.entity.RPCRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * 客户端方面，由于在客户端这一侧我们并没有接口的具体实现类，就没有办法直接生成实例对象。
 * 这时，我们可以通过动态代理的方式生成实例，并且调用方法时生成需要的RpcRequest对象并且发送给服务端
 */

public class RpcClientProxy implements InvocationHandler {

    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);

    private final RPCClient client;
    public RpcClientProxy(RPCClient client){
        this.client = client;
    }

    // 使用getProxy()方法来生成代理对象。
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz}, this);
    }

    /**
     * InvocationHandler接口需要实现invoke()方法，来指明代理对象的方法被调用时的动作。
     * 在这里，我们显然就需要生成一个RpcRequest对象，发送出去，然后返回从服务端接收到的结果
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        RPCRequest rpcRequest = new RPCRequest(method.getDeclaringClass().getName(),
                                                method.getName(), args, method.getParameterTypes());
        return client.sendMessage(rpcRequest);
    }
}
