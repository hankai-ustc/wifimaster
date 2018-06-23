package org.wifimaster.app.Orchestrator;

import org.apache.felix.scr.annotations.*;
import org.onosproject.core.ApplicationId;
import org.onosproject.core.CoreService;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.net.table.FlowTableService;
import org.onosproject.net.table.FlowTableStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wifimaster.app.VNFManager.NFMaster;
import org.wifimaster.app.WifiManager.WifiMaster;
import org.wifimaster.app.tenant.TenantManager;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by hankai on 1/4/18.
 */
@Component(immediate = true)
@Service
public class Orchestrator implements OrchestratorService{
    private static final String RULE_TEST = "org.onosproject.app";
    private final Logger log = LoggerFactory.getLogger(getClass());
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowTableStore tableStore;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowTableService flowTableService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;
    static private final int WIFI_DEFAULT_PORT = 6677;
    static private final int VNF_DEFAULT_PORT = 6688;

    private ApplicationId appId;
    private ExecutorService threadPool;
    private WifiMaster wifiMaster;
    private NFMaster nfMaster;
    private TenantManager tenantManager;
    @Activate
    protected void activate(){
        appId = coreService.registerApplication(RULE_TEST);
        log.info(appId.toString());
        ExecutorService executorService = Executors.newCachedThreadPool();
        tenantManager = new TenantManager();
        threadPool = executorService;
        wifiMaster = new WifiMaster(threadPool,WIFI_DEFAULT_PORT);
        nfMaster = new NFMaster(threadPool,VNF_DEFAULT_PORT);
        threadPool.execute(wifiMaster);
        threadPool.execute(nfMaster);

    }

    @Deactivate
    protected void deactivate(){
        log.info("Stopped **************************************");
        wifiMaster.setFlag(false);
        try {
            Socket socket = new Socket("localhost", WIFI_DEFAULT_PORT);
            socket.close();
            } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        wifiMaster.getAgentManager().removeAllWifiAgent();

        nfMaster.setFlag(false);
        try {
            Socket socket = new Socket("localhost", VNF_DEFAULT_PORT);
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        nfMaster.getVnfAgentManager().removeAllVNFAgent();
    }
    @Override
    public NFMaster getNfMaster(){
        return nfMaster;
    }

    @Override
    public WifiMaster getWifiMaster() {
        return wifiMaster;
    }

    @Override
    public int getWifiListenPort(){
        return WIFI_DEFAULT_PORT;
    }

    @Override
    public TenantManager getTenantManager(){
        return tenantManager;
    }
}
