package RPC04.serializer;

public interface CommonSerializer  {
    // 序列化
    byte[] serialize(Object object);
    // 反序列化
    Object deserializer(byte[] bytes, Class<?> clazz);
    // 该序列化器的编号
    int getCode();
    // 根据编号获取序列化器
    static CommonSerializer getByCode(int code){
        switch (code){
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new HessianSerializer();
            case 3:
                return new ProtostuffSerializer();
            default:
                return null;
        }
    }
}
