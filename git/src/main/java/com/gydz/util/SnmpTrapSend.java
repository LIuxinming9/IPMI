package com.gydz.util;

import java.io.IOException;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.SNMP4JSettings;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

/** 
 * 本类用于发送Trap信息 
 *  
 * @author gfw2306
 * 
 */  
public class SnmpTrapSend {

    private Snmp snmp = null;

    private Address targetAddress = null;

    private TransportMapping<UdpAddress> transport = null;

    //UsmUser 的userName
    private String username1 = "user1";
    //认证协议的密码  如MD5
    private String authPassword = "password1";
    //加密协议密码  如 DES AES
    private String privPassword = "password2";

    public static void main(String[] args) {

        SnmpTrapSend poc = new SnmpTrapSend();

        try {
            poc.init();

            poc.sendV1Trap();

            poc.sendV2cTrap();

            poc.sendV3TrapNoAuthNoPriv();

            //poc.sendV3Auth();
            poc.sendV3();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void init() throws IOException {
        //目标主机的ip地址 和 端口号
        targetAddress = GenericAddress.parse("udp:192.168.5.10/162");
        transport = new DefaultUdpTransportMapping();
        snmp = new Snmp(transport);
        transport.listen();
    }

    /**
     * Snmp V1 测试发送Trap
     * @return
     * @throws IOException
     */
    public ResponseEvent sendV1Trap() throws IOException {
        PDUv1 pdu = new PDUv1();
        VariableBinding v = new VariableBinding();
        v.setOid(SnmpConstants.sysName);
        v.setVariable(new OctetString("Snmp Trap V1 Test"));
        pdu.add(v);
        pdu.setType(PDU.V1TRAP);

        // set target
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);
        // retry times when commuication error
        target.setRetries(2);
        // timeout
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version1);
        // send pdu, return response
        return snmp.send(pdu, target);
    }

    /**
     * Snmp V2c 测试发送Trap
     * @return
     * @throws IOException
     */
    public ResponseEvent sendV2cTrap() throws IOException {

        PDU pdu = new PDU();
        VariableBinding v = new VariableBinding();
        v.setOid(SnmpConstants.sysName);
        v.setVariable(new OctetString("Snmp Trap V2 Test"));
        pdu.add(v);
        pdu.setType(PDU.TRAP);

        // set target
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString("public"));
        target.setAddress(targetAddress);

        // retry times when commuication error
        target.setRetries(2);
        target.setTimeout(1500);
        target.setVersion(SnmpConstants.version2c);
        // send pdu, return response
        return snmp.send(pdu, target);

    }

    /**
     * SnmpV3 不带认证加密协议.
     * @return
     * @throws IOException
     */
    public ResponseEvent sendV3TrapNoAuthNoPriv() throws IOException {
        SNMP4JSettings.setExtensibilityEnabled(true);
        SecurityProtocols.getInstance().addDefaultProtocols();

        UserTarget target = new UserTarget();
        target.setVersion(SnmpConstants.version3);

        try {
            transport = new DefaultUdpTransportMapping();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        byte[] enginId = "TEO_ID".getBytes();
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
                enginId), 500);
        SecurityModels secModels = SecurityModels.getInstance();
        if (snmp.getUSM() == null) {
            secModels.addSecurityModel(usm);
        }

        target.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);

        target.setAddress(targetAddress);

        ScopedPDU pdu = new ScopedPDU();
        pdu.setType(PDU.NOTIFICATION);
        VariableBinding v = new VariableBinding();
        v.setOid(SnmpConstants.sysName);
        v.setVariable(new OctetString("Snmp Trap V3 Test sendV3TrapNoAuthNoPriv"));
        pdu.add(v);

        snmp.setLocalEngine(enginId, 500, 1);
        return snmp.send(pdu, target);
    }

    /**
     * 目前不可以被接收
     * @return
     * @throws IOException
     */
    public ResponseEvent sendV3Auth() throws IOException {
        SNMP4JSettings.setExtensibilityEnabled(true);
        SecurityProtocols.getInstance().addDefaultProtocols();

        UserTarget target = new UserTarget();
        target.setSecurityName(new OctetString(username1));
        target.setVersion(SnmpConstants.version3);

        try {
            transport = new DefaultUdpTransportMapping();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        byte[] enginId = "TEO_ID".getBytes();
        USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
                enginId), 500);
        SecurityModels secModels = SecurityModels.getInstance();
        synchronized (secModels) {
            if (snmp.getUSM() == null) {
                secModels.addSecurityModel(usm);
            }
            snmp.getUSM().addUser(
                    new OctetString(username1),
                    new OctetString(enginId),
                    new UsmUser(new OctetString(username1), AuthMD5.ID,
                            new OctetString(authPassword), Priv3DES.ID,
                            new OctetString(privPassword)));
            target.setSecurityLevel(SecurityLevel.AUTH_PRIV);

            target.setAddress(targetAddress);

            ScopedPDU pdu = new ScopedPDU();
            pdu.setType(PDU.NOTIFICATION);
            VariableBinding v = new VariableBinding();
            v.setOid(SnmpConstants.sysName);
            v.setVariable(new OctetString("Snmp Trap V3 Test sendV3Auth"));
            pdu.add(v);

            snmp.setLocalEngine(enginId, 500, 1);
            ResponseEvent send = snmp.send(pdu, target);
            //System.out.println(send.getError());

            return send;
        }
    }


    /**
     * 测试SnmpV3  带认证协议，加密协议
     * @return
     * @throws IOException
     */
    public ResponseEvent sendV3() throws IOException{
        OctetString userName = new OctetString(username1);
        OctetString authPass = new OctetString(authPassword);
        OctetString privPass = new OctetString("privPassword");

        TransportMapping<?> transport;   
        transport = new DefaultUdpTransportMapping();

        Snmp snmp = new Snmp(transport);
        //MPv3.setEnterpriseID(35904);
        USM usm = new USM(SecurityProtocols.getInstance(),   
        new OctetString(MPv3.createLocalEngineID()), 500);   
        SecurityModels.getInstance().addSecurityModel(usm);   

        UserTarget target = new UserTarget();

        byte[] enginId = "TEO_ID".getBytes();

        SecurityModels secModels = SecurityModels.getInstance();
        synchronized (secModels) {
            if (snmp.getUSM() == null) {
                secModels.addSecurityModel(usm);
            }
            /*snmp.getUSM().addUser(
                    new OctetString(username),
                    new OctetString(enginId),
                    new UsmUser(new OctetString(username), AuthMD5.ID,
                            new OctetString(authPassword), Priv3DES.ID,
                            new OctetString(privPassword)));*/
            // add user to the USM   
            snmp.getUSM().addUser(userName,new UsmUser(userName,AuthMD5.ID,authPass,PrivDES.ID,privPass));

            target.setAddress(targetAddress);   
            target.setRetries(2);   
            target.setTimeout(3000);   
            target.setVersion(SnmpConstants.version3);   
            target.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);   
            target.setSecurityName(userName); 

            ScopedPDU pdu = new ScopedPDU();
            pdu.setType(PDU.NOTIFICATION);
            VariableBinding v = new VariableBinding();
            v.setOid(SnmpConstants.sysName);
            v.setVariable(new OctetString("Snmp Trap V3 Test sendV3Auth----------"));
            pdu.add(v);

            snmp.setLocalEngine(enginId, 500, 1);
            ResponseEvent send = snmp.send(pdu, target);
            //System.out.println(send.getError());

            return send;
        }

    }






    /*public void sendV3() throws IOException {
        snmp.getUSM().addUser(
                new OctetString("MD5DES"),
                new UsmUser(new OctetString("MD5DES"), AuthMD5.ID,
                        new OctetString("MD5DESUserAuthPassword"), PrivDES.ID,
                        new OctetString("MD5DESUserPrivPassword")));
        // create the target
        UserTarget target = new UserTarget();
        target.setAddress(targetAddress);
        target.setRetries(1);
        target.setTimeout(5000);
        target.setVersion(SnmpConstants.version3);
        target.setSecurityLevel(SecurityLevel.AUTH_PRIV);
        target.setSecurityName(new OctetString("MD5DES"));

        // create the PDU
        PDU pdu = new ScopedPDU();
        pdu.add(new VariableBinding(new OID("1.3.6")));
        pdu.setType(PDU.GETNEXT);

        // send the PDU
        ResponseEvent response = snmp.send(pdu, target);
        // extract the response PDU (could be null if timed out)
        PDU responsePDU = response.getResponse();
        // extract the address used by the agent to send the response:
        Address peerAddress = response.getPeerAddress();
    }*/





}