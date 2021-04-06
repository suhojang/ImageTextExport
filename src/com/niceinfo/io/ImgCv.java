package com.niceinfo.io;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import com.kwic.io.ImageConverter;
import com.kwic.io.JOutputStream;
/**
 * 건강보험 장기요양 관련 전용 이미지 converter 
 * */
public class ImgCv {
	
	
	/**
	 * make png bytes from tif File 
	 * */
	public static byte[] convert(File src) throws Exception{
		BufferedImage bufferedImage	= read(src);
		JOutputStream jos	= null;
		try{
			jos	= new JOutputStream();
			ImageIO.write(bufferedImage, ImageConverter.EXT_PNG, jos);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(jos!=null)jos.close();}catch(Exception e){}
		}
		return jos.getBytes();
	}
	/**
	 * make png bytes from tif bytes 
	 * */
	public static byte[] convert(byte[] src) throws Exception{
		BufferedImage bufferedImage	= read(src);
		JOutputStream jos	= null;
		try{
			jos	= new JOutputStream();
			ImageIO.write(bufferedImage, ImageConverter.EXT_PNG, jos);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(jos!=null)jos.close();}catch(Exception e){}
		}
		return jos.getBytes();
	}
	/**
	 * make png bytes from tif bytes 
	 * */
	public static byte[] convert(BufferedImage b1) throws Exception{
		JOutputStream jos	= null;
		try{
			BufferedImage b2	= new BufferedImage(b1.getWidth(),b1.getHeight(),BufferedImage.TYPE_4BYTE_ABGR);
			b2.getGraphics().drawImage(b1, 0, 0, null);
			jos	= new JOutputStream();
			ImageIO.write(b2, ImageConverter.EXT_PNG, jos);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(jos!=null)jos.close();}catch(Exception e){}
		}
		return jos.getBytes();
	}
	
	
	/**
	 * read BufferedImage from File Object
	 * */
	public static BufferedImage read(File src) throws Exception{
		return read(readFile(src));
	}
	/**
	 * read bytes array from File object
	 * */
	public static byte[] readFile(File src) throws Exception{
		JOutputStream jos	= null;
		FileInputStream fis	= null;
		byte[] bytes	= null;
		try{
			fis	= new FileInputStream(src);
			jos	= new JOutputStream();
			jos.write(fis);
			bytes	= jos.getBytes();
		}catch(Exception e){
			throw e;
		}finally{
			try{if(fis!=null)fis.close();}catch(Exception ex){}
			try{if(jos!=null)jos.close();}catch(Exception ex){}
		}
		return bytes;
	}
	/**
	 * read BufferedImage from byte array
	 * */
	public static BufferedImage read(byte[] src) throws Exception{
		return ImageIO.read(new ByteArrayInputStream(src));
	}
	/**
	 * write png image
	 * */
	public static void write(byte[] bytes,String filename){
		FileOutputStream fos	= null;
		try{
			fos	= new FileOutputStream(new File(filename));
			fos.write(bytes);
			fos.flush();
		}catch(Exception e){
			
		}finally{
			try{if(fos!=null)fos.close();}catch(Exception e){}
		}
	}
	/**
	 * 이미지 컷
	 * */
	public static BufferedImage cut(BufferedImage image,int stX,int stY,int width,int height) throws Exception{
		return image.getSubimage(stX, stY, width, height);
	}
	
	public static void main(String[] args) throws Exception{
		String folderPath	= "E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/HealInsImg/images";
		File folder	= new File(folderPath);
		File[] list	= folder.listFiles(new FileFilter(){
			@Override
			public boolean accept(File file) {
				if(file.getName().endsWith(".tif"))
					return true;
				return false;
			}
		});
		
		String dest		= null;
		byte[] bytes	= null;
		BufferedImage image	= null;
		for(int i=0;i<list.length;i++){
			//convert tif file to bytes 
			bytes	= ImgCv.readFile(list[i]);
			//read tif
			image	= ImageIO.read(new ByteArrayInputStream(bytes));
			//cut image for document number
			image	= ImageIO.read(new ByteArrayInputStream(bytes)).getSubimage(320, 330, 325, 25);
			//convert tif to png
			bytes	= ImgCv.convert(image);
			//saved file name
			dest	= folderPath+"/"+list[i].getName().replaceAll("."+ImageConverter.EXT_TIF, "."+ImageConverter.EXT_PNG);
			ImgCv.write(bytes,dest);
		}
	}
}
