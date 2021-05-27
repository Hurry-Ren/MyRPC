package RPC04.transport;

import RPC04.serializer.CommonSerializer;
import RPC04.entity.RPCRequest;

public interface RPCClient {

    Object sendMessage(RPCRequest rpcRequest);

    void setSerializer(CommonSerializer serializer);
}
