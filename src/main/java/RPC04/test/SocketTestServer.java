package RPC04.test;

import RPC04.registry.DefaultServiceRegistry;
import RPC04.registry.ServiceRegistry;
import RPC04.serializer.KryoSerializer;
import RPC04.transport.HelloService;
import RPC04.transport.HelloServiceImp;
import RPC04.transport.Socket.SocketServer;


public class SocketTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImp();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        SocketServer socketServer = new SocketServer(serviceRegistry);
        socketServer.setSerializer(new KryoSerializer());
        socketServer.start(9999);
    }
}
