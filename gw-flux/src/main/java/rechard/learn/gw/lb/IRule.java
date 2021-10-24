package rechard.learn.gw.lb;

/**
 * @author Rechard
 **/
public interface IRule {

    public Server choose();

    public void setLoadBalancer(ILoadBalancer lb);

    public ILoadBalancer getLoadBalancer();
}
