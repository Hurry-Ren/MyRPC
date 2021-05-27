package RPC01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class RPCServer {

    private static final Logger logger =  LoggerFactory.getLogger(RPCServer.class);
    private final ExecutorService threadPool;

    public RPCServer(){
        int corePoolSize = 5;
        int maximumPoolSize = 10;
        long keepAliveTime = 60;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        threadPool = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workingQueue, threadFactory);
    }

    /**
     * RpcServer暂时只能注册一个接口，即对外提供一个接口的调用服务，添加register方法，
     * 在注册完一个服务后立刻开始监听：
     */
    public void register(Object service, int port){
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            logger.info("服务器正在启动...");
            Socket socket;
            while ((socket = serverSocket.accept()) != null){
                logger.info("客户端连接成功！IP为: {}", socket.getInetAddress().getHostAddress());
                threadPool.execute(new WorkerThread(socket, service));
            }
        } catch (IOException e) {
            logger.info("链接出错！" + e);
        }
    }
}
