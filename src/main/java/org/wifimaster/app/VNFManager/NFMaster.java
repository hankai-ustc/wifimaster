package org.wifimaster.app.VNFManager;


import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onosproject.net.AnnotationKeys;
import org.onosproject.net.Device;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;

/**
 * Created by hankai on 11/29/17.
 */

public class NFMaster implements Runnable{
    private  final Logger log = LoggerFactory.getLogger(NFMaster.class);
    private ExecutorService threadPool;
    private int port;
    private boolean flag;
    private VNFAgentManager vnfAgentManager;
    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected DeviceService deviceService;


    public NFMaster(ExecutorService threadPool, int port){
        this.threadPool = threadPool;
        this.port = port;
    }

    @Override
    public void run(){
        log.info("Starting NFMaster ......");
        flag =true;
        vnfAgentManager = new VNFAgentManager();
        threadPool.execute(new VNFProtocolServer(this,vnfAgentManager,threadPool,port));
    }

    public void setFlag(boolean flag){
        this.flag = flag;
    }

    public boolean getFlag(){
        return flag;
    }

    public VNFAgentManager getVnfAgentManager(){
        return this.vnfAgentManager;
    }

    public Device getSwitch(VNFAgent va){
        Device pofswitch=null;
        DeviceId deviceId = null;
        String ipAddress = va.getIpAddress().getHostAddress();
        for (Iterator iter = deviceService.getAvailableDevices().iterator(); iter.hasNext();){
            Device device =(Device)iter.next();
            String addr = device.annotations().value(AnnotationKeys.MANAGEMENT_ADDRESS);
            if (addr.equals(ipAddress)){
                pofswitch = device;
                break;
            }
        }
        return pofswitch;
    }
}
