package com.kwic.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Sms {
	@SuppressWarnings("deprecation")
	public String sendSms(Map<String,String> map) throws Exception{
		String msg		= "";
		
        StringBuffer sb = new StringBuffer();
		StringBuffer sbQry = new StringBuffer();
	    sbQry.append("ComID="	 + URLEncoder.encode("kwic"));
		sbQry.append("&ComPass=" + URLEncoder.encode("kwic5539"));
		sbQry.append("&sphone1=" + URLEncoder.encode(""));
		sbQry.append("&sphone2=" + URLEncoder.encode("1588"));
		sbQry.append("&sphone3=" + URLEncoder.encode("5976"));
		sbQry.append("&rphone1=" + URLEncoder.encode((String)map.get("rphone1")));
		sbQry.append("&rphone2=" + URLEncoder.encode((String)map.get("rphone2")));
		sbQry.append("&rphone3=" + URLEncoder.encode((String)map.get("rphone3")));
		sbQry.append("&UserID="  + URLEncoder.encode("fctools"));
		sbQry.append("&SDate="   + URLEncoder.encode("00000000"));
		sbQry.append("&STime="   + URLEncoder.encode("000000"));
		sbQry.append("&Msg="	 + URLEncoder.encode((String)map.get("MSG_TXT"),"EUC-KR"));

	    String qry = sbQry.toString();
	    
	    HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection)(new URL("http://sms.kwic.co.kr/iHeart/SmsSend.asp")).openConnection();
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setUseCaches(false);

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
			dos.writeBytes(qry);
			dos.flush();
			dos.close();

			BufferedReader in = new BufferedReader( new InputStreamReader(conn.getInputStream(),"KSC5601"));
			String s ="";
			while( (s = in.readLine()) != null ){
			   sb.append(s);
			}
			in.close();
			msg = sb.toString();
		}catch (Exception e) {
        	e.printStackTrace();
		}	    
		return msg;
	}
	
	public static void main(String[] args){
		Map<String,String> info	 = new HashMap<String,String>();
		info.put("rphone1", "010");
		info.put("rphone2", "4199");
		info.put("rphone3", "2808");
		
//		String msgTxt = "[발급번호 추출 SMS 발송] 추출 시각 : " + new SimpleDateFormat("yyyy-MM-dd kk:mm:ss") + "\n" + "발급번호 추출을 성공 하였습니다.";
		String msgTxt = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(new Date()) + " 발급번호 추출을 성공 하였습니다.";
		
		info.put("MSG_TXT", msgTxt );
		
		try {
			String msg	= new Sms().sendSms(info);
			System.out.println(msg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
