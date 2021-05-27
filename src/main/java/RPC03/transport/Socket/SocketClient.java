package RPC03.transport.Socket;

import RPC03.ExceptionAndCode.RPCError;
import RPC03.ExceptionAndCode.RPCException;
import RPC03.ExceptionAndCode.ResponseCode;
import RPC03.entity.RPCRequest;
import RPC03.entity.RPCResponse;
import RPC03.transport.RPCClient;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 直接使用Java的序列化方式，通过Socket传输。
 * 创建一个Socket，获取ObjectOutputStream对象，然后把需要发送的对象传进去即可，
 * 接收时获取ObjectInputStream对象，readObject()方法就可以获得一个返回的对象。
 */

@AllArgsConstructor
public class SocketClient implements RPCClient{
    private static final Logger logger =LoggerFactory.getLogger(SocketClient.class);

    private final String host;
    private final int port;

    @Override
    public Object sendMessage(RPCRequest rpcRequest) {
        try (Socket socket = new Socket(host, port)) {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            RPCResponse rpcResponse = (RPCResponse) objectInputStream.readObject();
            if (rpcResponse == null){
                logger.error("服务调用失败, service: {}", rpcRequest.getInterfaceName());
                throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, "server: " + rpcRequest.getInterfaceName());
            }
            if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getStatueCode())){
                logger.error("服务调用失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RPCException(RPCError.SERVICE_INVOCATION_FAILURE, "server: " + rpcRequest.getInterfaceName());
            }
            return rpcResponse.getData();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用时有错误发生：", e);
            throw new RPCException("服务调用失败", e);
        }
    }
}
