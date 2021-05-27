package RPC03.test;

import RPC03.transport.HelloObject;
import RPC03.transport.HelloService;
import RPC03.transport.RpcClientProxy;
import RPC03.transport.Socket.SocketClient;

public class SocketTestClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient("127.0.0.1", 9999);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is Socket style!");
        String res = helloService.Hello(object);
        System.out.println(res);
    }
}
