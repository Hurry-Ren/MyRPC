package RPC03.transport;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * HelloObject是需要在调用过程中从客户端传递给服务端的对象。
 */
@Data
@AllArgsConstructor
public class HelloObject implements Serializable {
    private Integer id;
    private String message;
}
