package org.wifimaster.app.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wifimaster.app.WifiManager.WifiClient;

import java.util.concurrent.ConcurrentHashMap;

public class Tenant {
    protected final Logger log = LoggerFactory.getLogger(WifiClient.class);
    private final ConcurrentHashMap<String, Tenant> tenantMap = new ConcurrentHashMap<String,Tenant>();
    private int tenantId;
    private String tenantName;

    public Tenant(int tenantId,String tenantName){
        this.tenantId = tenantId;
        this.tenantName = tenantName;
    }

    public int getTenantId(){
        return tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(){
        this.tenantName=tenantName;
    }
}
