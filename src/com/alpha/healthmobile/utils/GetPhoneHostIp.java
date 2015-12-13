package com.alpha.healthmobile.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * ��ȡ�ֻ���ǰ�����ip��ַ
 * 
 * @author SongJian
 * 
 */
public class GetPhoneHostIp {
	public static String getPhoneHostIp() throws SocketException {
		Enumeration<NetworkInterface> enumeration;
		Enumeration<InetAddress> ipAddr;
		for (enumeration = NetworkInterface.getNetworkInterfaces(); enumeration
				.hasMoreElements();) {
			NetworkInterface networkInterface = enumeration.nextElement();
			for (ipAddr = networkInterface.getInetAddresses(); ipAddr
					.hasMoreElements();) {
				InetAddress inetAddress = ipAddr.nextElement();
				if (!inetAddress.isLoopbackAddress()
						&& inetAddress instanceof Inet4Address) {
					return inetAddress.getHostAddress();
				}
			}
		}
		return null;
	}
}
