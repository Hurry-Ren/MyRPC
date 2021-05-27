package RPC03.test;

import RPC03.registry.DefaultServiceRegistry;
import RPC03.registry.ServiceRegistry;
import RPC03.transport.HelloService;
import RPC03.transport.HelloServiceImp;
import RPC03.transport.Netty.server.NettyServer;

public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImp();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9999);
    }
}
