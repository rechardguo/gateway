package rechard.learn.gw.lb;

import java.util.List;

/**
 * @author Rechard
 **/
public interface ILoadBalancer {
    /**
     * @return All known servers, both reachable and unreachable.
     */
    public List<Server> getAllServers();
}
