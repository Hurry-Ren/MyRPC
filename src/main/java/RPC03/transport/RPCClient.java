package RPC03.transport;

import RPC03.entity.RPCRequest;

public interface RPCClient {
    Object sendMessage(RPCRequest rpcRequest);
}
