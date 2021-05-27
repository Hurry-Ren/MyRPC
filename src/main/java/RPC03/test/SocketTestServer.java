package RPC03.test;

import RPC03.registry.DefaultServiceRegistry;
import RPC03.registry.ServiceRegistry;
import RPC03.transport.HelloService;
import RPC03.transport.HelloServiceImp;
import RPC03.transport.Socket.SocketServer;


public class SocketTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImp();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        SocketServer socketServer = new SocketServer(serviceRegistry);
        socketServer.start(9999);
    }
}
