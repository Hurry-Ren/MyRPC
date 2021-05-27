package RPC04.transport.Socket;

import RPC04.ExceptionAndCode.RPCError;
import RPC04.ExceptionAndCode.RPCException;
import RPC04.factory.ThreadPoolFactory;
import RPC04.handler.RequestHandler;
import RPC04.handler.RequestHandlerThread;
import RPC04.registry.ServiceRegistry;
import RPC04.serializer.CommonSerializer;
import RPC04.transport.RPCServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class SocketServer implements RPCServer {

    private static final Logger logger =  LoggerFactory.getLogger(SocketServer.class);


    private final ExecutorService threadPool;
    private final ServiceRegistry serviceRegistry;
    private CommonSerializer serializer;
    private RequestHandler requestHandler = new RequestHandler();


    public SocketServer(ServiceRegistry serviceRegistry){
        this.serviceRegistry = serviceRegistry;
        // 创建线程池
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
    }

    // 服务端启动
    @Override
    public void start(int port){
        if (serializer == null){
            logger.error("未设置序列化器！");
            throw new RPCException(RPCError.SERVICE_NOT_FOUND);
        }
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器启动...");
            Socket socket;
            // 当未接到连接请求，将一直阻塞
            while ((socket = serverSocket.accept()) != null){
                logger.info("客户端连接成功！{}：{}", socket.getInetAddress().getHostAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serviceRegistry, serializer));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {

    }

}
