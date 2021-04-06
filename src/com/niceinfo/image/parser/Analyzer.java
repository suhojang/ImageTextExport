package com.niceinfo.image.parser;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.MemoryImageSource;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.kwic.image.pattern.png.parser.PNGDecoder;
import com.kwic.image.pattern.png.parser.RGBArray;
import com.kwic.util.StringUtil;

public class Analyzer {
	public static final int DEFAULT_MATCH_RATE_LIMIT	= 90;
	public static final int DEFAULT_MATCH_CHAR_LENGTH	= 5;
	
	public static final int[][][] toPNGArrays(byte[] bytes) throws Exception{
		return toPNGArrays(new ByteArrayInputStream(bytes));
	}
	
	public static final int[][][] toPNGArrays(File file) throws Exception{
		return toPNGArrays(new FileInputStream(file));
	}
	
	public static final int[][][] toPNGArrays(InputStream is) throws Exception{
		PNGDecoder decoder	= null;
		RGBArray arrayImage	= null;
		int[][][] arr	= null;
		int width	= 0;
		int height	= 0;
		try{
			decoder	= new PNGDecoder(is);
			arrayImage	= decoder.decodeRGBArrayImage();
			width	= arrayImage.getWidth();
			height	= arrayImage.getHeight();
			arr	= parse(width,height,arrayImage.getPixels());
			
		}catch(Exception e){
			throw e;
		}finally{
			try{if(is!=null)is.close();}catch(Exception e){}
		}
		return arr;
	}
	
	public static final String getColorCode(Color color){
		String alpha	= Integer.toHexString(color.getAlpha());
		alpha			= alpha.length()==1?"0"+alpha:alpha;
		String red		= Integer.toHexString(color.getRed());
		red				= red.length()==1?"0"+red:red;
		String green	= Integer.toHexString(color.getGreen());
		green			= green.length()==1?"0"+green:green;
		String blue		= Integer.toHexString(color.getBlue());
		blue			= blue.length()==1?"0"+blue:blue;
		return alpha+red+green+blue;
	}
	public static final int getColorInt(Color color){
		return color.getRed()+color.getGreen()+color.getBlue();
	}
	public static int[][][] parse(int w,int h,int[] arr){
		int[][][] tempArr	= new int[h][w][4];
		
		int r	= 0;
		int c	= 0;
		Color color	= null;
		for(int i=0;i<arr.length;i++){
			color	= new Color(arr[i],true);

			tempArr[r][c][0]	= color.getAlpha();
			tempArr[r][c][1]	= color.getRed()==0?0:255;
			tempArr[r][c][2]	= color.getGreen()==0?0:255;
			tempArr[r][c++][3]	= color.getBlue()==0?0:255;
			if(c!=0&&c%w==0){
				c	= 0;
				r++;
			}
		}
		return tempArr;
	}
	
	public static int[] getStartEndX(int[][][] arr){
		int[] xArr		= new int[2];
		int[] bfArr		= new int[3];
		int lastX		= 0;
		for(int j=3;j<arr[0].length-3;j++){
			for(int i=3;i<arr.length-3;i++){
				if(bfArr[0]==0 || bfArr[1]==0 || bfArr[2]==0){
				}else{
					if(Math.abs(bfArr[0]-arr[i][j][1])+Math.abs(bfArr[1]-arr[i][j][2])+Math.abs(bfArr[2]-arr[i][j][3])>=60
							&& xArr[0]==0
							){
						xArr[0]	= j;
					}else if(Math.abs(bfArr[0]-arr[i][j][1])+Math.abs(bfArr[1]-arr[i][j][2])+Math.abs(bfArr[2]-arr[i][j][3])>=60){
						lastX	= j-1;
					}
				}
				bfArr[0]	= arr[i][j][1];
				bfArr[1]	= arr[i][j][2];
				bfArr[2]	= arr[i][j][3];
			}
		}		
		xArr[1]	= lastX;
		return xArr;
	}
	
	public static int[][][] getPattern(File patternFile) throws Exception{
		List<int[][]> list	= new ArrayList<int[][]>();
		int[][][] imagePixel	= null;
		BufferedReader br	= null;
		String line	= null;
		try{
			br	= new BufferedReader(new InputStreamReader(new FileInputStream(patternFile)));
			
			while( (line=br.readLine()) !=null ){
				list.add(toXYRGBLine(line.trim()));
			}
			imagePixel	= new int[list.size()][][];
			for(int i=0;i<imagePixel.length;i++){
				imagePixel[i]	= list.get(i);
			}
			
		}catch(Exception e){
			throw e;
		}finally{
			try{if(br!=null)br.close();}catch(Exception e){}
		}
		return imagePixel;
	}
	public static int[][] toXYRGBLine(String line){
		String[] pixelArr	= line.split("\t");
		int[][] linePixel		= new int[pixelArr.length][4];
		String[] rgbArr		= null;
		for(int i=0;i<pixelArr.length;i++){
			rgbArr	= pixelArr[i].trim().split(",");
			linePixel[i][0]	= 100;
			linePixel[i][1]	= Integer.parseInt(rgbArr[0]);
			linePixel[i][2]	= Integer.parseInt(rgbArr[1]);
			linePixel[i][3]	= Integer.parseInt(rgbArr[2]);
		}
		return linePixel;
	}
	public static int[] convertPNGImageFormat(int[][][] arr){
		int[] convertArr	= new int[arr.length*arr[0].length];
		int idx	= 0;
		for(int i=0;i<arr.length;i++){
			for(int j=0;j<arr[0].length;j++){
				convertArr[idx++]	= new Color(arr[i][j][1],arr[i][j][2],arr[i][j][3],0).getRGB();
			}
		}
		return convertArr;
	}
	
	public static void printPNGPixel(File pngFile) throws Exception{
		int[][][] png	= Analyzer.toPNGArrays(pngFile);//height,width,rgb
		for(int i=0;i<png.length;i++){
			for(int j=0;j<png[i].length;j++){
				System.out.print(
						(j==0?"":"\t")
						+StringUtil.addChar(String.valueOf(png[i][j][1]), 3, "0", true)
						+","+StringUtil.addChar(String.valueOf(png[i][j][2]), 3, "0", true)
						+","+StringUtil.addChar(String.valueOf(png[i][j][3]), 3, "0", true)
					);
			}
			System.out.println("");
		}
	}
	
	public static void printPNGPixel(File pngFile,File wFile) throws Exception{
		FileWriter fw	= null;
		
		try{
			fw	= new FileWriter(wFile);

			int[][][] png	= Analyzer.toPNGArrays(pngFile);//height,width,rgb
			for(int i=0;i<png.length;i++){
				for(int j=0;j<png[i].length;j++){
					fw.write(
							(j==0?"":"\t")
							+StringUtil.addChar(String.valueOf(png[i][j][1]), 3, "0", true)
							+","+StringUtil.addChar(String.valueOf(png[i][j][2]), 3, "0", true)
							+","+StringUtil.addChar(String.valueOf(png[i][j][3]), 3, "0", true)
						);
				}
				fw.write("\n");
			}
		
		}catch(Exception e){
			throw e;
		}finally{
			try{if(fw!=null)fw.close();}catch(Exception e){}
		}
	}
	
	public static void makeCutImage(File folder, int w,int h,int[] arr,String id) throws Exception{
		MemoryImageSource mis	= new MemoryImageSource(w,h,new DirectColorModel(24, 0xff0000, 0xff00, 0xff), arr, 0, w);
		mis.setAnimated(true);
		mis.setFullBufferUpdates(true);
		Image image = Toolkit.getDefaultToolkit().createImage(mis);   
		
		BufferedImage bi	= new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics g	= bi.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		
		ByteArrayOutputStream bas	= null;
		try{
			bas	= new ByteArrayOutputStream();
			ImageIO.write(bi, "png", bas);
		}catch(Exception e){
			throw e;
		}finally{
			try{if(bas!=null)bas.close();}catch(Exception ex){}
		}
		byte[] bytes	= bas.toByteArray();
		
		DataOutputStream dos	= null;
		try{
			dos	= new DataOutputStream(new BufferedOutputStream(new FileOutputStream(folder+"/"+id+".png")));
			dos.write(bytes);
			dos.flush();
		}catch(Exception e){
			throw e;
		}finally{
			try{if(dos!=null)dos.close();}catch(Exception ex){}
		}
	}

	public static final int[][][] filter(int [][][] arr){
		for(int i=0;i<arr.length;i++){
			for(int j=0;j<arr[i].length;j++){
				if(Matcher.IMAGE_FORMAT==Matcher.IMAGE_FORMAT_RGB24_1 && isCharacterRGB24_1(arr[i][j]))
					arr[i][j]	= new int[]{100,0,0,0};
				else if(Matcher.IMAGE_FORMAT==Matcher.IMAGE_FORMAT_ARGB32_2 && isCharacterRGB32_2(arr[i][j]))
					arr[i][j]	= new int[]{100,0,0,0};
				else if(Matcher.IMAGE_FORMAT==Matcher.IMAGE_FORMAT_ARGB32_1 && isCharacterARGB32_1(arr[i][j]))
					arr[i][j]	= new int[]{100,0,0,0};
				else
					arr[i][j]	= new int[]{100,255,255,255};
			}
		}
		return arr;
	}

	public static boolean isCharacterRGB24_1(int[] argb){
		int colorCnt	= 0;
		int colorCnt2	= 0;
		for(int i=1;i<=3;i++){
			if(argb[i]>170)
				colorCnt++;
			if(argb[i]<100)
				colorCnt2++;
		}
		if(colorCnt==1 && colorCnt2==2)
			return true;
		
		colorCnt	= 0;
		for(int i=1;i<=3;i++)
			if(argb[i]<60)
				colorCnt++;
		
		if(colorCnt==3)
			return true;
		
		return false;
	}
	public static boolean isCharacterRGB32_2(int[] argb){
		int colorCnt	= 0;
		int colorCnt2	= 0;
		for(int i=1;i<=3;i++){
			if(argb[i]>170)
				colorCnt++;
			if(argb[i]<100)
				colorCnt2++;
		}
		if(colorCnt==1 && colorCnt2==2)
			return true;
		
		colorCnt	= 0;
		for(int i=1;i<=3;i++)
			if(argb[i]<60)
				colorCnt++;
		
		if(colorCnt==3)
			return true;
		
		return false;
	}
	public static boolean isCharacterARGB32_1(int[] argb){
		if(argb[0]<=150)
			return false;
		return true;
	}
	public static void makePatternToPNGFolder(File convertFolder) throws Exception{
		File[] patterFiles	= convertFolder.listFiles(new FileFilter(){
			@Override
			public boolean accept(File file) {
				if(file.getName().indexOf(".png")>=0)
					return false;
				else
					return true;
			}
		});
		for(int f=0;f<patterFiles.length;f++){
			int[][][] arr	= getPattern(new File(convertFolder,patterFiles[f].getName()));
			int[] convertArr	= convertPNGImageFormat(arr);
			
			makeCutImage(convertFolder, arr[0].length,arr.length,convertArr,patterFiles[f].getName());
		}
	}
	public static void makePatternToPNGFile(File convertFile) throws Exception{
		int[][][] arr	= getPattern(convertFile);
		int[] convertArr	= convertPNGImageFormat(arr);
		
		makeCutImage(convertFile.getParentFile(), arr[0].length,arr.length,convertArr,convertFile.getName());
	}
	
	public static List<int[]> matchImage(int[][][] src, int[][][] tgt, int limitRate,String c){
		List<int[]> highRates	= new ArrayList<int[]>();
		int matchCnt		= 0;
		int blackCnt		= 0;
		int rate			= 0;
		
		for(int i=0;i<src.length;i++){
			for(int j=0;j<src[i].length;j++){
				System.out.println("src["+i+"]["+j+"][1] :: "+src[i][j][1]);
				if(src[i][j][1]==0)
					blackCnt++;
			}
		}
		for(int i=Matcher.MATCH_START_Y;i<Matcher.MATCH_END_Y;i++){
			for(int j=Matcher.MATCH_START_X;j<Matcher.MATCH_END_X;j++){
				matchCnt	= matchChar(i,j,src,tgt,c);
				rate		= (matchCnt*100)/blackCnt;
				if(rate>=limitRate){
					addExistsFlag(highRates,j,i,rate);
				}
			}
		}
		return highRates;
	}
	
	public static void addExistsFlag(List<int[]> highRates,int x,int y, int rate){
		int[] old	= null;
		boolean addFlag	= true;
		
		for(int i=highRates.size()-1;i>=0;i--){
			old	= highRates.get(i);
			
			if(old[0]>=x-10 && old[0]<=x+10 && old[1]<rate){
				highRates.remove(i);
			}else if(old[0]>=x-10 && old[0]<=x+10 && old[1]>=rate){
				addFlag	= false;
			}
		}
		
		if(addFlag)
			highRates.add(new int[]{x,rate,y});
	}
	
	
	public static int matchChar(int y,int x,int[][][] src, int[][][] tgt,String c){
		int matchCnt	= 0;
		for(int i=0;i<src.length;i++){
			if(y+i>=tgt.length)
				break;
			for(int j=0;j<src[i].length;j++){
				if(x+j>=tgt[y+i].length)
					break;
				if(src[i][j][1]==0 && tgt[y+i][x+j][1]==0)
					matchCnt++;
			}
		}
		return matchCnt;
	}
}