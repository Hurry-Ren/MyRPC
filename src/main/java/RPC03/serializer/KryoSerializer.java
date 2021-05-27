package RPC03.serializer;


import RPC03.ExceptionAndCode.SerializeException;
import RPC03.ExceptionAndCode.SerializerCode;
import RPC03.entity.RPCRequest;
import RPC03.entity.RPCResponse;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class KryoSerializer implements CommonSerializer {

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    // //使用ThreadLocal初始化Kryo，因为Kryo中的output和input是线程不安全的
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(()->{
        Kryo kryo = new Kryo();
        // 注册类
        kryo.register(RPCResponse.class);
        kryo.register(RPCRequest.class);
        // 循环引用检测，默认为true
        kryo.setReferences(true);
        // 不强制要求注册类，默认为false，
        // 若设置为true则要求涉及到的所有类都要注册，包括jdk中的比如Object
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    @Override
    public byte[] serialize(Object object) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream))
        {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, object);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (IOException e) {
            logger.error("Kryo序列化是有错误发生" + e);
            throw new SerializeException("Kryo序列化是有错误发生");
        }
    }

    @Override
    public Object deserializer(byte[] bytes, Class<?> clazz) {
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream))
        {
            Kryo kryo = kryoThreadLocal.get();
            Object obj = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return obj;
        } catch (IOException e) {
            logger.error("Kryo反序列化时出现错误：" + e);
            throw new SerializeException("ryo反序列化时出现错误");
        }
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("KRYO").getCode();
    }
}
