package RPC04.test;

import RPC04.registry.DefaultServiceRegistry;
import RPC04.registry.ServiceRegistry;
import RPC04.serializer.KryoSerializer;
import RPC04.serializer.ProtostuffSerializer;
import RPC04.transport.HelloService;
import RPC04.transport.HelloServiceImp;
import RPC04.transport.Netty.server.NettyServer;

public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImp();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.setSerializer(new ProtostuffSerializer());
        server.start(9999);
    }
}
