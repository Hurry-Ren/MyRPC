package RPC01.Test;

import RPC01.HelloService;
import RPC01.HelloServiceImp;
import RPC01.RPCServer;

/**
 * 在上面实现了一个HelloService的实现类HelloServiceImpl的实现类了，
 * 我们只需要创建一个RpcServer并且把这个实现类注册进去就行了
 */
public class TestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImp();
        RPCServer rpcServer = new RPCServer();
        rpcServer.register(helloService, 9000);
    }
}
