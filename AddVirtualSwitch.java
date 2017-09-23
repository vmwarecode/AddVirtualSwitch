/*
 * ****************************************************************************
 * Copyright VMware, Inc. 2010-2016.  All Rights Reserved.
 * ****************************************************************************
 *
 * This software is made available for use under the terms of the BSD
 * 3-Clause license:
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright 
 *    notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in 
 *    the documentation and/or other materials provided with the 
 *    distribution.
 * 
 * 3. Neither the name of the copyright holder nor the names of its 
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS
 * OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.vmware.host;

import com.vmware.common.annotations.Action;
import com.vmware.common.annotations.Option;
import com.vmware.common.annotations.Sample;
import com.vmware.connection.ConnectedVimServiceBase;
import com.vmware.vim25.*;

import java.util.Map;

/**
 * <pre>
 * AddVirtualSwitch
 *
 * This sample is used to add a virtual switch
 *
 * <b>Parameters:</b>
 * url              [required] : url of the web service
 * username         [required] : username for the authentication
 * password         [required] : password for the authentication
 * vswitchid        [required]: Name of the switch to be added
 * hostname         [required]: Name of the host
 *
 * <b>Command Line:</b>
 * Add a Virtual Switch
 * run.bat com.vmware.host.AddVirtualSwitch --url [webserviceurl]
 * --username [username] --password  [password] --hostname [hostname]
 * --vswitchid [mySwitch]
 * </pre>
 */
@Sample(
        name = "add-virtual-switch",
        description = "This sample is used to add a virtual switch"
)
public class AddVirtualSwitch extends ConnectedVimServiceBase {

    private String host;
    private String virtualswitchid;

    @Option(name = "hostname", description = "Name of the host")
    public void setHost(String host) {
        this.host = host;
    }

    @Option(name = "vswitchid", description = "Name of the switch to be added")
    public void setVirtualswitchid(String virtualswitchid) {
        this.virtualswitchid = virtualswitchid;
    }

    void addVirtualSwitch() throws InvalidPropertyFaultMsg,
            RuntimeFaultFaultMsg {
        Map<String, ManagedObjectReference> hostList =
                getMOREFs.inFolderByType(serviceContent.getRootFolder(),
                        "HostSystem");
        ManagedObjectReference hostmor = hostList.get(host);
        if (hostmor != null) {
            try {
                HostConfigManager configMgr =
                        (HostConfigManager) getMOREFs.entityProps(hostmor,
                                new String[]{"configManager"}).get("configManager");
                ManagedObjectReference nwSystem = configMgr.getNetworkSystem();
                HostVirtualSwitchSpec spec = new HostVirtualSwitchSpec();
                spec.setNumPorts(8);
                vimPort.addVirtualSwitch(nwSystem, virtualswitchid, spec);
                System.out.println("Successfully created vswitch : "
                        + virtualswitchid);
            } catch (AlreadyExistsFaultMsg ex) {
                System.out.println("Failed : Switch already exists " + " Reason : "
                        + ex.getMessage());
            } catch (HostConfigFaultFaultMsg ex) {
                System.out.println("Failed : Configuration failures. "
                        + " Reason : " + ex.getMessage());
            } catch (ResourceInUseFaultMsg ex) {
                System.out.println("Failed adding switch: " + virtualswitchid
                        + " Reason : " + ex.getMessage());
            } catch (RuntimeFaultFaultMsg ex) {
                System.out.println("Failed adding switch: " + virtualswitchid
                        + " Reason : " + ex.getMessage());
            } catch (Exception ex) {
                System.out.println("Failed adding switch: " + virtualswitchid
                        + " Reason : " + ex.getMessage());
            }
        } else {
            System.out.println("Host not found");
        }
    }

    @Action
    public void run() throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg {
        addVirtualSwitch();
    }
}
