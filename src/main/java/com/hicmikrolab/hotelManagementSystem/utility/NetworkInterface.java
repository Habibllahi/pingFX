/**
 * @Author Hamzat Habibllahi Adewale
 */
package com.hicmikrolab.hotelManagementSystem.utility;

import java.io.IOException;
import java.net.InetAddress;

public interface NetworkInterface {

    boolean sendPingRequest(String ip);

    InetAddress getLocalHostIp() throws IOException;

    InetAddress getNodeIp(String ip) throws IOException;

    boolean lowLevelWriteAndRead(String ip, int port, String message);

    String httpClientOnOffRequester(String ip, int port, String message);

    String httpClientOnOffChecker(String ip, int port, String message);

}
