package RPC03.test;

import RPC03.transport.RpcClientProxy;
import RPC03.transport.HelloObject;
import RPC03.transport.HelloService;
import RPC03.transport.Netty.Client.NettyClient;
import RPC03.transport.RPCClient;

public class NettyTestClient {
    public static void main(String[] args) {
        RPCClient client = new NettyClient("127.0.0.1", 9999);
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject obj = new HelloObject(12, "This is Netty style!");
        String res = helloService.Hello(obj);
        System.out.println(res);
    }
}
