package org.wifimaster.app.VNFManager;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import org.codehaus.jackson.map.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wifimaster.app.WifiManager.AgentManager;
/**
 * Created by hankai on 1/4/18.
 */
public class VNF {
    protected static Logger log = LoggerFactory.getLogger(AgentManager.class);
    private int vnfId;
    private int tenantId;
    private VNFAgent vnfAgent;
    private String state;
    private Image image =null;
    private String script;
    private String type;
    private int numPorts;

    public VNF(int tenantId,int vnfId,VNFAgent vnfAgent){
        this.vnfId = vnfId;
        this.tenantId = tenantId;
        this.vnfAgent = vnfAgent;
        this.state = "pending";
    }

    public void setState(String state){
        this.state = state;
    }

    public int getVnfId(){
        return vnfId;
    }

    public void setVnfId(int Id){
        this.tenantId = vnfId;
    }
    public void setNumPorts(int numPorts){
        this.numPorts=numPorts;
    }
    public int getNumPorts(){
        return this.numPorts;
    }

    public int getTenantId(){
        return tenantId;
    }

    public void setTenantId(int tenantId){
        this.tenantId = tenantId;
    }

    public void setVnfAgent(VNFAgent vnfAgent){
        this.vnfAgent = vnfAgent;
    }

    public VNFAgent getVnfAgent(){
        return vnfAgent;
    }

    public void start(){
        JsonObject object = Json.object().add("type","add_vnf").add("vnf_id",this.getVnfId()).add("tenant_id",this.getTenantId()).add("vnf_type",this.getType()).add("nb_ports",this.getNumPorts()).add("script",this.getScript());
        this.vnfAgent.sendMsg(object.toString());
    }

    public void stop(){
        JsonObject object = Json.object().add("type","del_vnf").add("vnf_id",this.getVnfId());
        this.vnfAgent.sendMsg(object.toString());
    }
    public void setType(String type){
        this.type=type;
    }
    public String getType(){
        return this.type;
    }
    public void setScript(String script){
        this.script = script;
    }

    public String getScript(){
        return this.script;
    }

}
