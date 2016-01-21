package alibaba.httpdns_restful_demo;

public interface DegradationFilter {
    boolean shouldDegradeHttpDNS(String hostName);
}
