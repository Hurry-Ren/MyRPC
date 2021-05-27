package RPC02.Test;

import RPC02.*;
import RPC03.transport.HelloService;
import RPC03.transport.HelloServiceImp;


public class TestServer {
    public static void main(String[] args) {
        // 创建服务对象
        HelloService helloService = new HelloServiceImp();
        // 创建服务容器
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        // 注册服务对象到服务容器中
        serviceRegistry.register(helloService);
        // 将服务容器纳入到服务端
        RPCServer rpcServer = new RPCServer(serviceRegistry);
        // 启动服务端
        rpcServer.start(9000);
    }
}
