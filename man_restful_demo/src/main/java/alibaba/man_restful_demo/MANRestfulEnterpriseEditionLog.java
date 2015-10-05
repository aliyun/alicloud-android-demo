package alibaba.man_restful_demo;

/**
 * Created by ryan on 24/8/15.
 */
public class MANRestfulEnterpriseEditionLog {

    String lineSeparator = "\n";
    String columnSeparator = "\u0001";

    // Log field.
    String client_ip;
    String protocol_version;
    String imei;
    String imsi;
    String brand;
    String cpu;
    String device_id;
    String device_model;
    String resolution;
    String carrier;
    String access;
    String access_subtype;
    String channel;
    String app_key;
    String app_version;
    String long_login_nick;
    String user_nick;
    String phone_number;
    String country;
    String language;
    String os;
    String os_version;
    String sdk_type;
    String sdk_version;
    String reserve1;
    String reserve2;
    String reserve3;
    String reserve4;
    String reserve5;
    String reserves;
    String local_time;
    String local_timestamp;
    String page;
    String event_id;
    String arg1;
    String arg2;
    String arg3;
    String args;
    String day;
    String hour;
    String server_timestamp;
    String seq;

    private String log;

    // Assembly log.
    public void assemblyLog() {
        log = client_ip + columnSeparator +
                protocol_version + columnSeparator +
                imei + columnSeparator +
                imsi + columnSeparator +
                brand + columnSeparator +
                cpu + columnSeparator +
                device_id + columnSeparator +
                device_model + columnSeparator +
                resolution + columnSeparator +
                carrier + columnSeparator +
                access + columnSeparator +
                access_subtype + columnSeparator +
                channel + columnSeparator +
                app_key + columnSeparator +
                app_version + columnSeparator +
                long_login_nick + columnSeparator +
                user_nick + columnSeparator +
                phone_number + columnSeparator +
                country + columnSeparator +
                language + columnSeparator +
                os + columnSeparator +
                os_version + columnSeparator +
                sdk_type + columnSeparator +
                sdk_version + columnSeparator +
                reserve1 + columnSeparator +
                reserve2 + columnSeparator +
                reserve3 + columnSeparator +
                reserve4 + columnSeparator +
                reserve5 + columnSeparator +
                reserves + columnSeparator +
                local_time + columnSeparator +
                local_timestamp + columnSeparator +
                page + columnSeparator +
                event_id + columnSeparator +
                arg1 + columnSeparator +
                arg2 + columnSeparator +
                arg3 + columnSeparator +
                args + columnSeparator +
                day + columnSeparator +
                hour + columnSeparator +
                server_timestamp + columnSeparator +
                seq  + lineSeparator;
    }

    public String getFormattedLog() {
        return log;
    }

}
