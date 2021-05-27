package RPC03.codec;

import RPC03.ExceptionAndCode.PackageType;
import RPC03.ExceptionAndCode.RPCError;
import RPC03.ExceptionAndCode.RPCException;
import RPC03.serializer.CommonSerializer;
import RPC03.entity.RPCRequest;
import RPC03.entity.RPCResponse;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        // 从缓冲区中读数据
        int magic = in.readInt();
        if (magic != MAGIC_NUMBER){
            logger.error("不认识的协议包：{}", magic);
            throw new RPCException(RPCError.UNKNOWN_PROTOCOL);
        }

        int packageCode = in.readInt();
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()){
            packageClass = RPCRequest.class;
        }else if (packageCode == PackageType.RESPONSE_PACK.getCode()){
            packageClass = RPCResponse.class;
        }else {
            logger.error("不认识的数据包：{}", packageCode);
            throw new RPCException(RPCError.UNKNOWN_PACKAGE_TYPE);
        }

        int serializerCode = in.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null){
            logger.info("不认识的反序列化器：{}",serializerCode);
            throw new RPCException(RPCError.UNKNOWN_SERIALIZER);
        }

        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object object = serializer.deserializer(bytes, packageClass);
        // 添加到对象列表
        out.add(object);
    }
}
