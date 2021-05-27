package RPC04.transport;

import RPC04.ExceptionAndCode.RPCError;
import RPC04.ExceptionAndCode.RPCException;
import RPC04.codec.CommonDecoder;
import RPC04.codec.CommonEncoder;
import RPC04.serializer.CommonSerializer;
import RPC04.transport.Netty.Client.NettyClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 用于获取Channel对象
 *      将客户端Channel的连接创建分离出来，给其设定5次失败后重连的机会；
 *      同时改变Socket方式数据传输的端序，使其与Netty保持一致。
 *      具体来说比如137这个int值，是按从1到3再到7的顺序序列化，还是从7到3再到1的顺序转换
 */
public class ChannelProvider {

    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);

    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();

    private static final int MAX_RETRY_COUNT = 5;
    private static Channel channel = null;

    private static Bootstrap initializeBootstrap(){
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                // 连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500)
                //启用该功能时，TCP会主动探测空闲连接的有效性。可以将此功能视为TCP的心跳机制，默认的心跳间隔是7200s即2小时。
                .option(ChannelOption.SO_KEEPALIVE, true)
                // 配置Channel参数，nodelay没有延迟，true就代表禁用Nagle算法，减小传输延迟。理解可参考：https://blog.csdn.net/lclwjl/article/details/80154565
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }

    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer){
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new CommonEncoder(serializer))
                             .addLast(new CommonDecoder())
                             .addLast(new NettyClientHandler());
            }
        });
        // 设置计数器的值
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try{
            connect(bootstrap, inetSocketAddress, countDownLatch);
            // 阻塞当前线程直到计时器为0
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("获取channel时出现错误", e);
        }
        return channel;
    }

    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, CountDownLatch countDownLatch){
        connect(bootstrap, inetSocketAddress, MAX_RETRY_COUNT, countDownLatch);
    }

    /**
     * Netty客户端创建通道连接,实现连接失败重试机制
     */
    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress,int retry, CountDownLatch countDownLatch){
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
           if (future.isSuccess()){
               logger.info("客户端连接成功");
               channel = future.channel();
               // 计数器减一
               countDownLatch.countDown();
               return;
           }
           if (retry == 0){
               logger.error("客户端连接失败：重试次数已用完，放弃连接！");
               countDownLatch.countDown();
               throw new RPCException(RPCError.CLIENT_CONNECT_SERVER_FAILURE);
           }
            // 第几次重连
            int order = (MAX_RETRY_COUNT - retry) + 1;
            // 重连时间间隔,相当于1乘以2的order次方
            int delay = 1 << order;
            logger.error("{}:连接失败，第{}次重连...", new Date(), order);
            // 利用schedule()在给定的延迟时间后执行connect()重连
            bootstrap.config().group().schedule(()-> connect(bootstrap, inetSocketAddress,
                    retry - 1, countDownLatch), delay, TimeUnit.SECONDS);
        });
    }


}
