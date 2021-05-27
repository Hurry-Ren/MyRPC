package RPC02.Test;

import RPC03.transport.HelloObject;
import RPC03.transport.HelloService;
import RPC02.RpcClientProxy;

/**
 * 客户端方面，我们需要通过动态代理，生成代理对象，并且调用，动态代理会自动帮我们向服务端发送请求的
 * 这里生成了一个HelloObject对象作为方法的参数。
 */
public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy clientProxy = new RpcClientProxy("127.0.0.1", 9000);
        HelloService helloService = clientProxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(12, "This is message!");
        String res = helloService.Hello(helloObject);
        System.out.println(res);
    }
}
