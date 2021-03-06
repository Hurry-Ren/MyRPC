package RPC02;

import RPC02.ExceptionAndCode.RPCError;
import RPC02.ExceptionAndCode.RPCException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultServiceRegistry implements ServiceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DefaultServiceRegistry.class);

    // key = 服务名称(即接口名), value = 服务实体(即实现类的实例对象)
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    // 用来存放实现类的名称，Set存取更高效，存放实现类名称相比存放接口名称占的空间更小，因为一个实现类可能实现了多个接口
    private final Set<String> registerService = ConcurrentHashMap.newKeySet();

    @Override
    public synchronized  <T> void register(T service) {
        String serviceImplName = service.getClass().getCanonicalName();
        if (registerService.contains(serviceImplName))
            return;
        registerService.add(serviceImplName);
        // 可能实现了多个接口，故使用Class数组接收
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0)
            throw new RPCException(RPCError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE); // 注册服务未实现接口
        for (Class<?> i : interfaces){
            serviceMap.put(i.getCanonicalName(), service);
        }
        logger.info("向接口：{} 注册服务：{}", interfaces, serviceImplName);
    }

    @Override
    public Object getService(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null)
            throw new RPCException(RPCError.SERVICE_NOT_FOUND);
        return service;
    }
}
