package com.kwic.util;

import java.net.InetAddress;
import java.net.NetworkInterface;

import org.apache.commons.codec.binary.Base64;

public class Kwic {
	private static final String kwicAccept	= "LzU0LTUzLUVELUI0LTU1LTI4Lw==";	//
	//private static final String kwicAccept	= "LzcwLTg1LUMyLTg2LTRELTJCLw==";
	private static boolean checked	= false;
	private static boolean auth		= false;
	
	public static String getMaxAddress() throws Exception{
		StringBuffer sb	= new StringBuffer();
		try{
			InetAddress			ip		= InetAddress.getLocalHost();
			NetworkInterface	netif	= NetworkInterface.getByInetAddress(ip);
			byte[] 				mac		= netif.getHardwareAddress();
			
			for (byte b : mac) 
				sb.append((sb.length()==0?"":"-")+String.format("%02X", b));
		}catch(Exception e){
			throw e;
		}
		return sb.toString();
	}
	public static boolean right() throws Exception{
		if(checked && auth)
			return auth;
		String mac	= getMaxAddress().toUpperCase();
		if(new String(Base64.decodeBase64(kwicAccept)).toUpperCase().indexOf("/"+mac+"/")>=0)
			auth	= true;
		else{
			auth	= false;
			System.err.println(
					new String(Base64.decodeBase64("SW52YWxpZCBrd2ljLWltYWdlaW8gbGljZW5zZS4gY3VycmVudD1b"))
					+mac
					+new String(Base64.decodeBase64("XSwgbGljZW5zZT1b"))
					+new String(Base64.decodeBase64(kwicAccept))
					+new String(Base64.decodeBase64("XQ=="))
					);
		}
		checked	= true;
		return auth;
	}
	
	public static void main(String[] args){
		
		try {
			String addr	= getMaxAddress();
			System.out.println("mac addr : "+addr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
