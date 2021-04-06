package com.kwic.io;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Locale;

import javax.imageio.ImageIO;

/**
 * Convert or resize Images
 * 
 * caution) tiff conversion is not recommended.
 * 
 * supported image format : jpg/jpeg, gif, png, bmp, tif/tiff
 * 
 *dependency
 * 	- jai-imageio-core-1.3.0.jar
 * */
public class ImageConverter {
	public static final String TYPES_JPEG	= "jpg/jpeg";
	public static final String TYPES_GIF	= "gif";
	public static final String TYPES_BMP	= "bmp";
	public static final String TYPES_TIF	= "tif/tiff";
	public static final String TYPES_PNG	= "png";
	
	public static final String EXT_JPEG	= "jpeg";
	public static final String EXT_GIF	= "gif";
	public static final String EXT_BMP	= "bmp";
	public static final String EXT_TIFF	= "tiff";
	public static final String EXT_TIF	= "tif";
	public static final String EXT_PNG	= "png";
	
	/**
	 * Returns the image format as a given filename
	 * 
	 * name : file name
	 * */
	public static String getImageType(String name) throws Exception{
		String ext	= name.toLowerCase(Locale.KOREA);
		ext	= ext.substring(ext.lastIndexOf(".")+1);
		
		if(TYPES_JPEG.indexOf(ext)>=0)
			return TYPES_JPEG;
		else if(TYPES_GIF.indexOf(ext)>=0)
			return TYPES_GIF;
		else if(TYPES_BMP.indexOf(ext)>=0)
			return TYPES_BMP;
		else if(TYPES_TIF.indexOf(ext)>=0)
			return TYPES_TIF;
		else if(TYPES_PNG.indexOf(ext)>=0)
			return TYPES_PNG;
		else
			throw new Exception("Unxpected image extension ["+name+"].");
	}
	/**
	 * Return height of the image in proportion to given width 
	 * 
	 * origin : source image file
	 * width : resized width
	 * */
	public static int getProportionalHeight(File origin,int width) throws Exception{
		BufferedImage img	= read(origin);
		double rate	= (double)img.getHeight(null) / (double)img.getWidth(null);
		return (int)(rate*width);
	}
	/**
	 * Return width of the image in proportion to given height
	 * 
	 * origin : source image file
	 * height : resized height
	 * */
	public static int getProportionalWidth(File origin,int height) throws Exception{
		BufferedImage img	= read(origin);
		double rate	= (double)img.getWidth(null) / (double)img.getHeight(null);
		return (int)(rate*height);
	}
	
	/**
	 * Convert the image to another format
	 * 
	 * src : source image file
	 * dest : new image file
	 * ext : new file extension
	 * */
	public static File convert(File src,File dest,String ext) throws Exception{
		BufferedImage bufferedImage	= read(src);
		ImageIO.write(bufferedImage,ext, dest);
		return dest;
	}
	public static BufferedImage read(File src) throws Exception{
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
		return ImageIO.read(new ByteArrayInputStream(bytes));
	}
	/**
	 * Convert the image to another format with a given width
	 * 
	 * origin : source image file
	 * dest : new image file
	 * width : resized width
	 * ext : new file extension
	 * */
	public static File resizeW(File origin,File dest,int width,String ext) throws Exception{
		return resize(origin,dest,new int[]{width,getProportionalHeight(origin,width)},ext);
	}	
	/**
	 * Convert the image to another format with a given height
	 * 
	 * origin : source image file
	 * dest : new image file
	 * height : resized height
	 * ext : new file extension
	 * */
	public static File resizeH(File origin,File dest,int height,String ext) throws Exception{
		return resize(origin,dest,new int[]{getProportionalWidth(origin,height),height},ext);
	}
	
	/**
	 * Return java.awt.Image object loaded from a file
	 * 
	 * src : source image file
	 * */
	private static Image getImage(File src) throws Exception{
		Image			img	= null;
		FileInputStream	fis	= null;
		byte[]			bytes	= null;
		try{
			fis		= new FileInputStream(src);
			bytes	= new byte[fis.available()];
			fis.read(bytes);
			fis.close();
			
			img = Toolkit.getDefaultToolkit().createImage(bytes);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(fis!=null)fis.close();}catch(Exception e){}
		}
		return img;
		
	}
	/**
	 * Recreates the image with a given file size and stores 
	 * origin : source image file
	 * dest : new image file
	 * size[0] : width
	 * size[1] : height
	 * ext : new file extension
	 * 
	 * */
	public static File resize(File origin,File dest,int[] size,String ext) throws Exception{
		File src	= origin;
		if(getImageType(src.getName()).indexOf(ext)<0)
			src	= convert(origin,dest,ext);
		
		Image			img	= null;
		Image			scaledImage	= null;
		BufferedImage	bi	= null;
		ByteArrayOutputStream	bas	= null;
		DataOutputStream		dos	= null;
		byte[]			bytes	= null;
		try{
			
			img = getImage(src);
			scaledImage = img.getScaledInstance(size[0], size[1], Image.SCALE_SMOOTH);
			
			MediaTracker tracker	= new MediaTracker(new java.awt.Frame());
			tracker.addImage(scaledImage, 0);
			tracker.waitForAll();
			
			bi	= new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_RGB);
			
			Graphics g	= bi.getGraphics();
			g.drawImage(scaledImage, 0, 0, null);
			g.dispose();
		
			bas	= new ByteArrayOutputStream();
			ImageIO.write(bi, ext, bas);
			bytes	= bas.toByteArray();
			
			dos	= new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dest)));
			dos.write(bytes);
			
		}catch(Exception e){
			throw e;
		}finally{
			try{if(bas!=null)bas.close();}catch(Exception e){}
			try{if(dos!=null)dos.close();}catch(Exception e){}
		}
		return dest;
	}
	/**
	 * Recreates the image with a given file size and stores 
	 * origin : source image file
	 * dest : new image file
	 * size[0] : width
	 * size[1] : height
	 * ext : new file extension
	 * 
	 * */
	public static File rewrite(File dest,String ext) throws Exception{
		Image			img	= null;
		Image			scaledImage	= null;
		BufferedImage	bi	= null;
		ByteArrayOutputStream	bas	= null;
		DataOutputStream		dos	= null;
		byte[]			bytes	= null;
		try{
			
			img = getImage(dest);
			scaledImage = img.getScaledInstance(img.getWidth(null), img.getHeight(null), Image.SCALE_SMOOTH);
			
			MediaTracker tracker	= new MediaTracker(new java.awt.Frame());
			tracker.addImage(scaledImage, 0);
			tracker.waitForAll();
			
			bi	= new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
			
			Graphics g	= bi.getGraphics();
			g.drawImage(scaledImage, 0, 0, null);
			g.dispose();
		
			bas	= new ByteArrayOutputStream();
			ImageIO.write(bi, ext, bas);
			bytes	= bas.toByteArray();
			
			dos	= new DataOutputStream(new BufferedOutputStream(new FileOutputStream(dest)));
			dos.write(bytes);
			
		}catch(Exception e){
			throw e;
		}finally{
			try{if(bas!=null)bas.close();}catch(Exception e){}
			try{if(dos!=null)dos.close();}catch(Exception e){}
		}
		return dest;
	}
	
	/**
	 * Return image size for a given file size
	 * 
	 * origin : source image file
	 * limit : limit file length (bytes)
	 * 
	 * */
	public static int[] sizeLimitLength(File origin,long limit) throws Exception{
		BufferedImage img	= read(origin);
		long size	= origin.length();
		if(size<limit)
			return new int[]{img.getWidth(null),img.getHeight(null)};

		double x	= (double)size/(double)limit;
		
		double rate	= new BigDecimal(Math.pow(x, 0.5)).divide(new BigDecimal(1), 2, BigDecimal.ROUND_UP).doubleValue();
		return new int[]{(int)((double)img.getWidth(null)/rate),(int)((double)img.getHeight(null)/rate)};
	}
	public static int[] sizeLimitLength(File origin,long limit,boolean re) throws Exception{
		limit	= (long) (limit*1.2);
		
		BufferedImage img	= read(origin);
		long size	= origin.length();
		if(size<limit)
			return new int[]{img.getWidth(null),img.getHeight(null)};

		double x	= (double)size/(double)limit;
		
		double rate	= new BigDecimal(Math.pow(x, 0.5)).divide(new BigDecimal(1), 2, BigDecimal.ROUND_UP).doubleValue();
		return new int[]{(int)((double)img.getWidth(null)/rate),(int)((double)img.getHeight(null)/rate)};
	}
	
	public static File convert(File src,File dest,long maxSize) throws Exception{
		String srcType	= getImageType(src.getName());
		String destType	= getImageType(dest.getName());

		String destExt	= null;
		
		if(destType.equals(ImageConverter.TYPES_JPEG)){
			destExt	= ImageConverter.EXT_JPEG;
		}else if(destType.equals(ImageConverter.TYPES_GIF)){
			destExt	= ImageConverter.EXT_GIF;
		}else if(destType.equals(ImageConverter.TYPES_BMP)){
			destExt	= ImageConverter.EXT_BMP;
		}else if(destType.equals(ImageConverter.TYPES_TIF)){
			destExt	= ImageConverter.EXT_TIFF;
		}else if(destType.equals(ImageConverter.TYPES_PNG)){
			destExt	= ImageConverter.EXT_PNG;
		}else{
			throw new Exception("Undefined image type ["+dest.getName()+"]");
		}
		
		if(!srcType.equals(destType) || destExt.equals(ImageConverter.EXT_TIFF)){
			ImageConverter.convert(
					  src
					, dest
					, destExt
				);
			src	= dest;
		}
		else
			FileIO.copy(src, dest);
		
		rewrite(dest,destExt);
		
		if(dest.length()<=maxSize || maxSize<=0)
			return dest;
		
		int[] sizes	= sizeLimitLength(src,maxSize);
		ImageConverter.resize(src, dest, sizes, destExt);
		if(dest.length()>maxSize){
			sizes	= sizeLimitLength(dest,maxSize);
			ImageConverter.resize(dest, dest, sizes, destExt);
		}
		return dest;
	}
	
	public static void main(String[] args) throws Exception{
		File folder	= new File("E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/HealInsImg/images/");
		File[] list	= folder.listFiles(new FileFilter(){
			@Override
			public boolean accept(File file) {
				if(file.getName().endsWith(".tif"))
					return true;
				return false;
			}
		});
		File src	= null;
		File dest	= null;
		for(int i=0;i<list.length;i++){
			src	= list[i];
			dest	= new File(folder,src.getName().replaceAll(".tif", ".png"));
			ImageConverter.convert(src,dest,-1);
		}
//		320,330~650,340
		
		
		
//		File src	= new File("E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/HealInsImg/images/R22C-6e180228085847.tif");
//		File dest	= new File("E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/HealInsImg/images/1.png");
		
//		PDFImage pdf	= new PDFImage(new File("E:/image/L01.pdf"));
//		int cnt	= pdf.getPageCount();
//		File file	= pdf.writeImage(new File("E:/image/PDF_IMAGE.jpeg"), cnt, cnt, "1234");
//		ImageConverter.convert(file,new File("E:/image/L01.jpg"),400*1024);
	}
}
