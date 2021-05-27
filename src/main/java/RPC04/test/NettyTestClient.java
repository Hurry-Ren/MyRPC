package RPC04.test;

import RPC04.serializer.KryoSerializer;
import RPC04.serializer.ProtostuffSerializer;
import RPC04.transport.HelloObject;
import RPC04.transport.HelloService;
import RPC04.transport.Netty.Client.NettyClient;
import RPC04.transport.RPCClient;
import RPC04.transport.RPCClientProxy;

public class NettyTestClient {
    public static void main(String[] args) {
        RPCClient client = new NettyClient("127.0.0.1", 9999);
        client.setSerializer(new ProtostuffSerializer());
        RPCClientProxy rpcClientProxy = new RPCClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject obj = new HelloObject(12, "This is Netty style!");
        String res = helloService.Hello(obj);
        System.out.println(res);
    }
}
