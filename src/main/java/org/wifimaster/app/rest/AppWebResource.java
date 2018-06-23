/*
 * Copyright 2018-present Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wifimaster.app.rest;

import com.eclipsesource.json.Json;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.onosproject.rest.AbstractWebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wifimaster.app.Orchestrator.OrchestratorService;
import org.wifimaster.app.VNFManager.VNF;
import org.wifimaster.app.VNFManager.VNFAgent;
import org.wifimaster.app.WifiManager.VirtualAP;
import org.wifimaster.app.WifiManager.WifiAgent;
import org.wifimaster.app.tenant.Tenant;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import static org.onlab.util.Tools.nullIsNotFound;

/**
 * Sample web resource.
 */
@Path("sdvrs")
public class AppWebResource extends AbstractWebResource {
    private final OrchestratorService orchestrator=get(OrchestratorService.class);
    private final Logger log = LoggerFactory.getLogger(getClass());
    /**
     * Get hello world greeting.
     *
     * @return 200 OK
     */
    @GET
    @Path("hello")
    public Response getGreeting() {
        ObjectNode node = mapper().createObjectNode().put("hello", "world");
        return ok(node).build();
    }

    /**
    * Post a new virtual access point add request.
     *
     * @onos.rsModel CreateVap
     * @param stream JSON stream
     * @return 200 OK
     */
    @POST
    @Path("wifi/createVap")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createVap(InputStream stream) throws IOException {
        ObjectNode node = mapper().createObjectNode();
        try{
            ObjectNode jsonTree = (ObjectNode) mapper().readTree(stream);
            JsonNode tenantId=jsonTree.get("tenantId");
            JsonNode wa=jsonTree.get("wifiAgent");
            JsonNode ifname = jsonTree.get("interface");
            JsonNode ssid = jsonTree.get("ssid");
            if (tenantId ==null ||  wa==null || ifname==null || ssid==null){
                node.put("result","Error input stream");
                return ok(node).build();
            }
            WifiAgent agent=orchestrator.getWifiMaster().getAgentManager().getWifiAgent(InetAddress.getByName(wa.asText()));
            if (agent !=null){
                agent.createVap(ifname.asText(),ssid.asText());
                node.put("result","Create Vap successful");
                }
            else{
                    node.put("result","wifiAgent dose not exist");
                }
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return ok(node).build();
        }
    /**
     * Post a new virtual access point add request.
     *
     * @onos.rsModel CreateNvap
     * @param stream JSON stream
     * @return 200 OK
     */
    @POST
    @Path("wifi/createNvap")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createNvap(InputStream stream) throws IOException {
        ObjectNode node = mapper().createObjectNode();
        try{
            ObjectNode jsonTree = (ObjectNode) mapper().readTree(stream);
            JsonNode wa=jsonTree.get("wifiAgent");
            JsonNode nvaps=jsonTree.get("vapNumber");
            WifiAgent agent=orchestrator.getWifiMaster().getAgentManager().getWifiAgent(InetAddress.getByName(wa.asText()));
            if (agent !=null){
                agent.createNvaps(nvaps.asText());
                node.put("result","Create Vap successful");
            }
            else{
                node.put("result","wifiAgent dose not exist");
            }
        }catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return ok(node).build();

    }

    /**
     * Post a new virtual access point add request.
     *
     * @onos.rsModel RemoveNvap
     * @param stream JSON stream
     * @return 200 OK
     */
    @POST
    @Path("wifi/removeNvap")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response RemoveNvap(InputStream stream) throws IOException {
        ObjectNode node = mapper().createObjectNode();
        try{
            ObjectNode jsonTree = (ObjectNode) mapper().readTree(stream);
            JsonNode wa=jsonTree.get("wifiAgent");
            JsonNode nvaps=jsonTree.get("vapNumber");
            WifiAgent agent=orchestrator.getWifiMaster().getAgentManager().getWifiAgent(InetAddress.getByName(wa.asText()));
            if (agent !=null){
                agent.removeNvaps(nvaps.asText());
                node.put("result","Remove Vap successful");
            }
            else{
                node.put("result","wifiAgent dose not exist");
            }
        }catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return ok(node).build();

    }


    /**
     * Post an exist virtual access point remove request.
     *
     * @onos.rsModel RemoveVap
     * @param stream JSON stream
     * @return 200 OK
     */
    @POST
    @Path("wifi/removeVap")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response removeVap(InputStream stream) throws IOException {
        ObjectNode node = mapper().createObjectNode();
        try{
            ObjectNode jsonTree = (ObjectNode) mapper().readTree(stream);
            JsonNode tenantId=jsonTree.get("tenantId");
            JsonNode wa=jsonTree.get("wifiAgent");
            JsonNode ifname = jsonTree.get("interface");
            if (tenantId ==null || ifname==null){
                node.put("result","Error input stream");
                return ok(node).build();}
            WifiAgent agent=orchestrator.getWifiMaster().getAgentManager().getWifiAgent(InetAddress.getByName(wa.asText()));
            if (agent ==null ||agent.getVirtualAP(ifname.asText())==null){
                node.put("result","WifiAgent or Vap not exists");
                return ok(node).build();
            }
            agent.destroyVap(ifname.asText());
            node.put("result","Remove vap successfully");

        }catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return ok(node).build();
    }
    /**
     * Post an add tenant request.
     *
     * @onos.rsModel addTenant
     * @param stream JSON stream
     * @return 200 OK
     */
    @POST
    @Path("tenant/addTenant")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTenant(InputStream stream) throws IOException {
        ObjectNode node = mapper().createObjectNode();
        try{
            ObjectNode jsonTree = (ObjectNode) mapper().readTree(stream);
            JsonNode tenantId=jsonTree.get("tenantId");
            JsonNode tenantName = jsonTree.get("tenantName");
            if (tenantId ==null){
                node.put("result","Error input stream");
                return ok(node).build();
            }
            if (orchestrator.getTenantManager().getTenant(tenantId.asInt()) ==null){
                node.put("result","Tenant exists");
                return ok(node).build();
            }
            Tenant tenant=new Tenant(tenantId.asInt(),tenantName.asText());
            orchestrator.getTenantManager().addTenant(tenant);
        }catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return ok(node).build();
    }
    @POST
    @Path("tenant/delTenant")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response delTenant(InputStream stream) throws IOException {
        ObjectNode node = mapper().createObjectNode();
        try{
            ObjectNode jsonTree = (ObjectNode) mapper().readTree(stream);
            JsonNode tenantId=jsonTree.get("tenantId");
            if (tenantId ==null){
                node.put("result","Error input stream");
                return ok(node).build();
            }
            if (orchestrator.getTenantManager().getTenant(tenantId.asInt()) ==null){
                node.put("result","Tenant not exists");
                return ok(node).build();
            }
            orchestrator.getTenantManager().removeTenant(tenantId.asInt());

        }catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        return ok(node).build();
    }

    /**
     * Post a new virtual network function request.
     *
     * @onos.rsModel CreateVnf
     * @param stream JSON stream
     * @return 200 OK
     */
    @POST
    @Path("vnf/createVnf")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createVnf(InputStream stream) throws IOException {
        ObjectNode node = mapper().createObjectNode();
        try {
            ObjectNode jsonTree = (ObjectNode) mapper().readTree(stream);
            JsonNode tenantId = jsonTree.get("tenantId");
            JsonNode va = jsonTree.get("vnfAgent");
            JsonNode vnfId = jsonTree.get("vnfId");
            JsonNode vnfType = jsonTree.get("vnfType");
            JsonNode nbPort = jsonTree.get("nbPort");
            JsonNode script = jsonTree.get("script");

            if (tenantId ==null ||  va==null || vnfId ==null || vnfType ==null || nbPort==null ||script==null){
                node.put("result","Error input stream");
                return ok(node).build();
            }
            VNFAgent vnfAgent=orchestrator.getNfMaster().getVnfAgentManager().getVNFAgent(InetAddress.getByName(va.asText()));
            if (vnfAgent !=null){
                VNF vnf= new VNF(tenantId.asInt(),vnfId.asInt(),vnfAgent);
                vnf.setNumPorts(nbPort.asInt());
                vnf.setType(vnfType.asText());
                vnf.setScript(script.asText());
                vnf.start();
                node.put("result","Create vnf successful");
            }
            else {
                node.put("result","vnfAgent dose not exist");
            }

        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        return ok(node).build();
        }


}
