package RPC04.transport.Netty.Client;

import RPC03.ExceptionAndCode.RPCError;
import RPC03.ExceptionAndCode.RPCException;
import RPC04.entity.RPCRequest;
import RPC04.serializer.CommonSerializer;
import RPC04.entity.RPCResponse;
import RPC04.transport.ChannelProvider;
import RPC04.transport.RPCClient;
import RPC04.transport.RPCMessageChecker;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

public class NettyClient implements RPCClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);


    private static final Bootstrap bootStrap;

    private CommonSerializer serializer;

    private String host;
    private int port;
    public NettyClient(String host, int port){
        this.host = host;
        this.port = port;
    }

    static {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        bootStrap = new Bootstrap();
        bootStrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    @Override
    public Object sendMessage(RPCRequest rpcRequest) {
        if (serializer == null){
            logger.error("未设置序列化器！");
            throw new RPCException(RPCError.SERVICE_NOT_FOUND);
        }
        // 保证自定义实体类变量的原子性和共享性的线程安全，此处应用于rpcResponse
        AtomicReference<Object> result = new AtomicReference<>(null);
        try {
            Channel channel = ChannelProvider.get(new InetSocketAddress(host, port), serializer);
            if (channel.isActive()){
                // 向服务端发请求，并设置监听，关于writeAndFlush()的具体实现可以参考：https://blog.csdn.net/qq_34436819/article/details/103937188
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()){
                        logger.info(String.format("客户端发送消息：%s", rpcRequest.toString()));
                    }else
                        logger.error("发送消息时出现错误：",  future1.cause());
                });
                channel.closeFuture().sync();
                // AttributeMap<AttributeKey, AttributeValue>是绑定在Channel上的，可以设置用来获取通道对象
                AttributeKey<RPCResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
                // get()阻塞获取value
                RPCResponse rpcResponse = channel.attr(key).get();
                RPCMessageChecker.check(rpcRequest, rpcResponse);
                result.set(rpcResponse.getData());
            }
        } catch (InterruptedException e) {
            logger.error("发送信息时出现错误", e);
        }
        // 注意并没有调用shutdown关闭客户端Netty
        return result.get();
    }

    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
}
