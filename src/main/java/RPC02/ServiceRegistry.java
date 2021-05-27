package RPC02;

/**
 * 第一个版本里面服务器只能注册一个服务，这个版本将让服务器提高多个服务。
 * 我们需要一个容器，用来保存服务端提供的所有服务，
 * 即通过服务名字就能返回这个服务的具体信息（利用接口名字获取到具体接口实现类对象）。
 * 一个register注册服务信息，一个getService获取服务信息。
 */
public interface ServiceRegistry {
    <T> void register(T service);
    Object getService(String serviceName);
}
