package org.wifimaster.app.tenant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ConcurrentHashMap;

public class TenantManager {
    private final ConcurrentHashMap<Integer, Tenant> tenantMap = new ConcurrentHashMap<Integer, Tenant>();
    protected static Logger log = LoggerFactory.getLogger(TenantManager.class);
    public TenantManager(){

    }

    public void addTenant(Tenant tenant){
        if (tenant !=null){
            int tenantId = tenant.getTenantId();
            if (!tenantMap.containsKey(tenantId)){
                tenantMap.put(tenantId,tenant);
                log.info("Add a Tenant:"+tenantId);
            }
        }
    }

    public Tenant getTenant(int tenantId){
        Tenant tenant=null;
        if (!tenantMap.containsKey(tenantId)){
            tenant=tenantMap.get(tenantId);
        }
        return tenant;
    }

    public void removeTenant(int tenantId){
        if (tenantMap.containsKey(tenantId)){
            tenantMap.remove(tenantId);
        }
    }
    }

