package rechard.learn.gw.lb;

/**
 * @author Rechard
 **/
public class LoadBalancerBuilder {

    private AbstractLoadBalancerRule rule=null;
    public static LoadBalancerBuilder newBuilder(){
        return new LoadBalancerBuilder();
    }
    public  LoadBalancerBuilder of(String loadbalanceStretage) {
        this.rule=new RoundRobinRule();
        if(loadbalanceStretage.equalsIgnoreCase("weight")){
            this.rule = new WeightRule();
        }
        return this;
    }
    public  LoadBalancerBuilder setIpList(String ipList) {
       this.rule.setLoadBalancer(new DefaultLoadBalancer(ipList));
        return this;
    }

    public  AbstractLoadBalancerRule build(){
        return this.rule;
    }
}
