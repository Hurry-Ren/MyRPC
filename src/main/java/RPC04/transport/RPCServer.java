package RPC04.transport;

import RPC04.serializer.CommonSerializer;

public interface RPCServer {

    void start(int port);

    void setSerializer(CommonSerializer serializer);
}
