package RPC04.transport.Socket;

import RPC04.ExceptionAndCode.PackageType;
import RPC04.ExceptionAndCode.RPCError;
import RPC04.ExceptionAndCode.RPCException;
import RPC04.entity.RPCRequest;
import RPC04.entity.RPCResponse;
import RPC04.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;

/**
 * Socket方式将数据序列化并写入输出流中【编码】
 */
public class ObjectReader {
    private static final Logger logger = LoggerFactory.getLogger(ObjectReader.class);

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static Object readObject(InputStream in)throws IOException{
        // 从缓冲区中读数据
        byte[] numberBytes = new byte[4];
        in.read(numberBytes);
        int magic = bytesToInt(numberBytes);
        if (magic != MAGIC_NUMBER){
            logger.error("不认识的协议包：{}", magic);
            throw new RPCException(RPCError.UNKNOWN_PROTOCOL);
        }

        in.read(numberBytes);
        int packageCode = bytesToInt(numberBytes);
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()){
            packageClass = RPCRequest.class;
        }else if (packageCode == PackageType.RESPONSE_PACK.getCode()){
            packageClass = RPCResponse.class;
        }else {
            logger.error("不认识的数据包：{}", packageCode);
            throw new RPCException(RPCError.UNKNOWN_PACKAGE_TYPE);
        }

        in.read(numberBytes);
        int serializerCode = bytesToInt(numberBytes);
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null){
            logger.info("不认识的反序列化器：{}",serializerCode);
            throw new RPCException(RPCError.UNKNOWN_SERIALIZER);
        }

        in.read(numberBytes);
        int length = bytesToInt(numberBytes);
        byte[] bytes = new byte[length];
        in.read(bytes);
        return serializer.deserializer(bytes, packageClass);
    }

    // 字节转数组
    private static int bytesToInt(byte[] src) {
        int value;
        value = ((src[0] & 0xFF) << 24)
                | ((src[1] & 0xFF)<< 16)
                | ((src[2] & 0xFF)<< 8)
                | (src[3] & 0xFF);
        return value;
    }
}
