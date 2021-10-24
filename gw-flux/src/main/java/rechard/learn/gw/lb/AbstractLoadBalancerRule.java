package rechard.learn.gw.lb;

/**
 * @author Rechard
 **/
public abstract class AbstractLoadBalancerRule implements IRule{
    private ILoadBalancer lb;

    @Override
    public void setLoadBalancer(ILoadBalancer lb){
        this.lb = lb;
    }

    @Override
    public ILoadBalancer getLoadBalancer(){
        return lb;
    }
}
