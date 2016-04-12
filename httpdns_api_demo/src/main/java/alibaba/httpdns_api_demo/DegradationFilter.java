package alibaba.httpdns_api_demo;

public interface DegradationFilter {
    boolean shouldDegradeHttpDNS(String hostName);
}
