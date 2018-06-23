package org.wifimaster.app.Orchestrator;

import org.wifimaster.app.VNFManager.NFMaster;
import org.wifimaster.app.WifiManager.WifiMaster;
import org.wifimaster.app.tenant.TenantManager;

public interface OrchestratorService {
    NFMaster getNfMaster();
    WifiMaster getWifiMaster();
    int getWifiListenPort();
    TenantManager getTenantManager();
}
