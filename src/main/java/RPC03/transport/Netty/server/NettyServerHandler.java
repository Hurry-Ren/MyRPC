package RPC03.transport.Netty.server;

import RPC04.entity.RPCRequest;
import RPC04.entity.RPCResponse;
import RPC04.handler.RequestHandler;
import RPC04.registry.DefaultServiceRegistry;
import RPC04.registry.ServiceRegistry;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServerHandler extends SimpleChannelInboundHandler<RPCRequest> {

    private static final Logger logger = LoggerFactory.getLogger(NettyServerHandler.class);

    private static RequestHandler requestHandler;
    private static ServiceRegistry serviceRegistry;
    static {
        requestHandler = new RequestHandler();
        serviceRegistry = new DefaultServiceRegistry();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RPCRequest msg) throws Exception {
        try {
            logger.info("服务端接收到请求：{}", msg);
            String interfaceName = msg.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object response = requestHandler.handle(msg, service);
            ChannelFuture future = ctx.writeAndFlush(response);
            // 添加一个监听器到ChannelFuture来检测是否所有的数据包都发出，然后关闭通道
            future.addListener(ChannelFutureListener.CLOSE);
        }finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception{
        logger.info("处理过程中发生错误：");
        cause.printStackTrace();
        ctx.close();
    }
}
