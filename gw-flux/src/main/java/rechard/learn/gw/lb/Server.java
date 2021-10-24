package rechard.learn.gw.lb;

import lombok.Data;

/**
 * @author Rechard
 **/
@Data
public class Server {
    private String host;
    private int port = 80;
    private volatile String id;
    private int weight;
    private String scheme;

    public Server(String host, int port) {
      this(host,port,1);
    }

    public Server(String host, int port,int weight) {
        this.host = host;
        this.port = port;
        this.weight=weight;
        this.id = host + ":" + port;
    }
}
