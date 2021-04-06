package com.kwic.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.codec.binary.Base64;

public class FileIO {
	public static final File decodeBase64ToFile(String base64,File file) throws Exception{
		byte[] bytes	= Base64.decodeBase64(base64);
		FileOutputStream fos	= null;
		
		try{
			fos	= new FileOutputStream(file);
			fos.write(bytes);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(fos!=null)fos.close();}catch(Exception e){}
		}
		return file;
	}
	public static final byte[] decodeBase64ToBytes(String base64) throws Exception{
		return Base64.decodeBase64(base64);
	}
	public static final String encodeBase64(byte[] plain) throws Exception{
		return Base64.encodeBase64String(plain);
	}
	
	public static final byte[] getBytes(File file) throws Exception{
		InputStream is	= null;
		JOutputStream os	= null;
		
		try{
			is	= new FileInputStream(file);
			os	= new JOutputStream();
			os.write(is);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(os!=null)os.close();}catch(Exception e){}
			try{if(is!=null)is.close();}catch(Exception e){}
		}
		return os.getBytes();
	}
	public static final void copy(File src,File dest) throws Exception{
		InputStream is	= null;
		OutputStream os	= null;
		
		try{
			is	= new FileInputStream(src);
			os	= new FileOutputStream(dest);
			
			byte[] bytes	= new byte[1024];
			int size	= -1;
			while( (size=is.read(bytes))>=0 )
				os.write(bytes,0,size);
			os.flush();
		}catch(Exception e){
			throw e;
		}finally{
			try{if(os!=null)os.close();}catch(Exception e){}
			try{if(is!=null)is.close();}catch(Exception e){}
		}
	}
	public static void main(String[] args){
		System.out.println(new String(Base64.decodeBase64("")));
	}
}
