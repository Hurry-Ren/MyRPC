package RPC04.serializer;

import RPC04.ExceptionAndCode.SerializeException;
import RPC04.ExceptionAndCode.SerializerCode;
import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(HessianSerializer.class);

    @Override
    public byte[] serialize(Object object) {
        HessianOutput hessianOutput = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(object);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            logger.error("Hessian序列化时出现错误：", e);
            throw new SerializeException("Hessian序列化时出现错误！");
        }finally {
            if (hessianOutput != null){
                try{
                    hessianOutput.close();
                } catch (IOException e) {
                    logger.error("关闭Output流时出现错误", e);
                }
            }
        }
    }

    @Override
    public Object deserializer(byte[] bytes, Class<?> clazz) {
        HessianInput hessianInput = null;
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)){
            hessianInput = new HessianInput(byteArrayInputStream);
            return hessianInput.readObject();
        } catch (IOException e) {
            logger.error("反序列化时出现错误", e);
            throw new SerializeException("反序列化时出现错误");
        }finally {
            if (hessianInput != null){
                hessianInput.close();
            }
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("HESSIAN").getCode();
    }
}
