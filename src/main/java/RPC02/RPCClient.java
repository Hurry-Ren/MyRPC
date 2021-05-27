package RPC02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 直接使用Java的序列化方式，通过Socket传输。
 * 创建一个Socket，获取ObjectOutputStream对象，然后把需要发送的对象传进去即可，
 * 接收时获取ObjectInputStream对象，readObject()方法就可以获得一个返回的对象。
 */

public class RPCClient {
    private static final Logger logger =LoggerFactory.getLogger(RPCClient.class);

    public Object sendRequest(RPCRequest rpcRequest, String host, int port){
        try {
            Socket socket = new Socket(host, port);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            oos.writeObject(rpcRequest);
            oos.flush();
            return ois.readObject();
        } catch (Exception e){
            logger.error("调用错误：" + e);
            return null;
        }

    }
}
