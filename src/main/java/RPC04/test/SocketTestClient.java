package RPC04.test;

import RPC04.serializer.KryoSerializer;
import RPC04.transport.HelloObject;
import RPC04.transport.HelloService;
import RPC04.transport.RPCClientProxy;
import RPC04.transport.Socket.SocketClient;

public class SocketTestClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient("127.0.0.1", 9999);
        // 接口与代理对象之间的中介对象
        client.setSerializer(new KryoSerializer());
        RPCClientProxy proxy = new RPCClientProxy(client);
        // 创建代理对象
        HelloService helloService = proxy.getProxy(HelloService.class);
        // 接口方法的参数对象
        HelloObject object = new HelloObject(12, "This is Socket style!");
        // 由动态代理可知，代理对象调用hello()实际会执行invoke()
        String res = helloService.Hello(object);
        System.out.println(res);
    }
}
