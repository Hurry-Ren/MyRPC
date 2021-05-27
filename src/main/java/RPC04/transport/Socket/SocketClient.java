package RPC04.transport.Socket;

import RPC04.ExceptionAndCode.RPCError;
import RPC04.ExceptionAndCode.RPCException;
import RPC04.ExceptionAndCode.ResponseCode;
import RPC04.entity.RPCRequest;
import RPC04.entity.RPCResponse;
import RPC04.serializer.CommonSerializer;
import RPC04.transport.RPCClient;
import RPC04.transport.RPCMessageChecker;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * 直接使用Java的序列化方式，通过Socket传输。
 * 创建一个Socket，获取ObjectOutputStream对象，然后把需要发送的对象传进去即可，
 * 接收时获取ObjectInputStream对象，readObject()方法就可以获得一个返回的对象。
 */

public class SocketClient implements RPCClient {
    private static final Logger logger =LoggerFactory.getLogger(SocketClient.class);

    private final String host;
    private final int port;

    public SocketClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    private CommonSerializer serializer;

    @Override
    public Object sendMessage(RPCRequest rpcRequest) {
        if (serializer == null){
            logger.error("未设置序列化器！");
            throw new RPCException(RPCError.SERVICE_NOT_FOUND);
        }
        try (Socket socket = new Socket(host, port)) {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            Object obj = ObjectReader.readObject(inputStream);
            RPCResponse rpcResponse = (RPCResponse) obj;
            RPCMessageChecker.check(rpcRequest, rpcResponse);
            return rpcResponse.getData();
        } catch (IOException e) {
            logger.error("调用时有错误发生：", e);
            throw new RPCException("服务调用失败", e);
        }
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
