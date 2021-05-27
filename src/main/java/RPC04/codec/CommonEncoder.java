package RPC04.codec;

import RPC04.ExceptionAndCode.PackageType;
import RPC04.serializer.CommonSerializer;
import RPC04.entity.RPCRequest;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CommonEncoder extends MessageToByteEncoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonEncoder.class);

    private static final int MAGIC_NUMBER = 0xCAFEBABE; // 魔数
    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }


    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        // 数据写到缓冲区
        out.writeInt(MAGIC_NUMBER);
        if (msg instanceof RPCRequest){
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        }else{
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);

    }
}
