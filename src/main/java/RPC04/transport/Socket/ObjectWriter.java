package RPC04.transport.Socket;

import RPC04.ExceptionAndCode.PackageType;
import RPC04.entity.RPCRequest;
import RPC04.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Socket方式从输入流中读取字节并反序列化【解码】
 */
public class ObjectWriter {

    private static final Logger logger = LoggerFactory.getLogger(ObjectWriter.class);

    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static void writeObject(OutputStream out, Object object, CommonSerializer serializer) throws IOException{
        out.write(intToBytes(MAGIC_NUMBER));
        if (object instanceof RPCRequest){
            out.write(intToBytes(PackageType.REQUEST_PACK.getCode()));
        }else{
            out.write(intToBytes(PackageType.RESPONSE_PACK.getCode()));
        }
        out.write(intToBytes(serializer.getCode()));
        byte[] bytes = serializer.serialize(object);
        out.write(intToBytes(bytes.length));
        out.write(bytes);
        out.flush();
    }


    // int转字符
    private static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }
}
