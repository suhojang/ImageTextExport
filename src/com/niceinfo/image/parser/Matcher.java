package com.niceinfo.image.parser;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import com.kwic.util.StringUtil;
import com.niceinfo.image.parser.pattern.Patterns;
import com.niceinfo.io.ImgCv;

public class Matcher {
	public static final int WEIGHT_8_CHAR	= 1;
	public static final int WEIGHT_SIMILAR_1_CHAR	= 2;
	public static final int WEIGHT_SIMILAR_0_CHAR	= 1;
	public static final int IMAGE_FORMAT_RGB24_1	= 1;
	public static final int IMAGE_FORMAT_ARGB32_1	= 2;
	public static final int IMAGE_FORMAT_ARGB32_2	= 3;
	
	public static final int IMAGE_CUT_START_X	= 320; 
	public static final int IMAGE_CUT_START_Y	= 330; 
	public static final int IMAGE_WIDTH			= 325; 
	public static final int IMAGE_HEIGHT		= 25; 
	
	public static int IMAGE_FORMAT	= IMAGE_FORMAT_ARGB32_2;
	public static final int CharacterCount	= 19;
	public static final int RATE_REDUCE_OF_1	= -3;
	public static final int MATCH_START_X	= 0;
	public static final int MATCH_END_X	= 320;
	public static final int MATCH_START_Y	= 0;
	public static final int MATCH_END_Y	= 10;
	public static final int MIN_MATCH_RATE	= 100;
	
	public static final String[] MatchOrder	= new String[]{
		"8","9","6","0","5","4","3","2","7","1","-"
	};

	public static String getSerial(String src) throws Exception{
		return getSerial(src,MIN_MATCH_RATE);
	}
	public static String getSerial(String src, int limit) throws Exception{
		return getSerial(ImgCv.readFile(new File(src)),limit);
	}
	public static String getSerial(String src, String tessSrc) throws Exception{
		return getTessSerial(new File(src), new File(tessSrc));
	}
	public static String getSerial(File src, File tessFile) throws Exception{
		return getTessSerial(src, tessFile);
	}
	public static String getSerial(File src) throws Exception{
		return getSerial(src,MIN_MATCH_RATE);
	}
	public static String getSerial(File src, int limit) throws Exception{
		return getSerial(ImgCv.readFile(src),limit);
	}
	public static String getSerial(byte[] bytes) throws Exception{
		return getSerial(bytes,MIN_MATCH_RATE);
	}
	public static String getTessSerial(File src, File tessFile) throws Exception{
		return getTessSerial(src, tessFile, MIN_MATCH_RATE);
	}
	
	public static String getTessSerial(File oriFile, File tessFile, int limit) throws Exception{
		byte[] oriBytes		= ImgCv.readFile(oriFile);
		
		if(oriBytes==null || oriBytes.length==0)
			throw new Exception("Image is null.");
		BufferedImage oriImage	= ImageIO.read(new ByteArrayInputStream(oriBytes));
		oriImage	= ImageIO.read(new ByteArrayInputStream(oriBytes));
		if(oriImage==null)
			throw new Exception("Invalid image format.");
		if(oriImage.getWidth()<650 || oriImage.getHeight()<400)
			throw new Exception("Invalid image size. ["+oriImage.getWidth()+" * "+oriImage.getHeight()+"]");

		StringBuffer sb	= new StringBuffer();
		//직장
		BufferedImage image	= oriImage.getSubimage(330, 215, 325, 25);
		byte[] bytes		= ImgCv.convert(image);
		
		int[][][] rgb		= Analyzer.toPNGArrays(bytes);
		String[][] matches	= match(rgb,limit,CharacterCount);
		if(matches[0]==null){
			image	= oriImage.getSubimage(330, 370, 325, 25);
			bytes	= ImgCv.convert(image);
		}
		
		String scanFilePath	= null;
		File scanFile		= null;
		try {
			scanFilePath	= oriFile.getParentFile().getAbsolutePath() + File.separator + "export_" + UUID.randomUUID() + ".tif";
			scanFile		= new File(scanFilePath);
			ImageIO.write(image, "tif", scanFile);
			
			ITesseract instance	=  new Tesseract();
			instance.setDatapath(tessFile.getParentFile().getAbsolutePath());
			
			Pattern pattern 				= Pattern.compile("(^[0-9]*$)");
			java.util.regex.Matcher matcher	= null;
			
			String number	= "";
			String result	= instance.doOCR(scanFile).trim();
			char[] chr		= result.toCharArray();
			for(int i=0;i<chr.length;i++) {
				String chrStr	= Character.toString(chr[i]);
				if(!"".equals(chrStr)) {
					matcher	= pattern.matcher(chrStr);
					if(matcher.find()) {
						number	+= chrStr;
					}else {
						number	+= "-";
					}
				}
			}
			number	= number.replace("-", "");
			number	= number.substring(0,2) + "-" + number.substring(2,10) + "-" + number.substring(10);
			
			sb.append(number);
		} catch (Exception e) {
			return getSerial(oriBytes, MIN_MATCH_RATE);
		} finally {
			if(scanFile != null)
				scanFile.delete();
		}
		return sb.toString();
	}
	
	public static String getSerial(byte[] oriBytes, int limit) throws Exception{
		if(oriBytes==null || oriBytes.length==0)
			throw new Exception("Image is null.");
		BufferedImage oriImage	= ImageIO.read(new ByteArrayInputStream(oriBytes));
		oriImage	= ImageIO.read(new ByteArrayInputStream(oriBytes));
		if(oriImage==null)
			throw new Exception("Invalid image format.");
		if(oriImage.getWidth()<650 || oriImage.getHeight()<400)
			throw new Exception("Invalid image size. ["+oriImage.getWidth()+" * "+oriImage.getHeight()+"]");

		StringBuffer sb	= new StringBuffer();
		
		//직장
		BufferedImage image	= oriImage.getSubimage(330, 215, 325, 25);
		byte[] bytes		= ImgCv.convert(image);
		
		int[][][] rgb		= Analyzer.toPNGArrays(bytes);
		String[][] matches	= match(rgb,limit,CharacterCount);
		//지역
		if(matches[0]==null){
			image	= oriImage.getSubimage(330, 370, 325, 25);
			bytes	= ImgCv.convert(image);
			
			rgb	= Analyzer.toPNGArrays(bytes);
			matches	= match(rgb,limit,CharacterCount);
		}
		
		for(int i=0;i<matches.length;i++){
			sb.append(matches[i][0]);
		}
		return sb.toString();
	}
	
	public static void writePatternsRgb(String path,int[][][] rgb) throws Exception{
		FileWriter fw	= null;
		try{
			fw	= new FileWriter(path);
			for(int i=0;i<rgb.length;i++){
				for(int j=0;j<rgb[i].length;j++){
					fw.write(
								(j==0?"":"\t")
								+StringUtil.intToHexString(rgb[i][j][0])
								+","+StringUtil.intToHexString(rgb[i][j][1])
								+","+StringUtil.intToHexString(rgb[i][j][2])
								+","+StringUtil.intToHexString(rgb[i][j][3])
							);
				}
				fw.write("\n");
			}
			fw.flush();
		}catch(Exception e){
			throw e;
		}finally{
			try{if(fw!=null)fw.close();}catch(Exception e){}
		}
	}
	
	/**
	 * 
	 * 
	 * @param rgbBytes		: png형식의 rbg bytes
	 * @param limit			: 일치율
	 * @param charLength	: 문자의 길이
	 * @return
	 * @throws Exception
	 */
	private static String[][] match(int[][][] rgbBytes,int limit,int charLength) throws Exception{
		
		List<int[][][]>			patterns	= null;
		Map<String,List<int[]>>	matchMap	= new HashMap<String,List<int[]>>();
		List<int[]>				matchArr	= null;
		
		for(int i=0;i<MatchOrder.length;i++){
			patterns	= Patterns.getPatterns().get(MatchOrder[i]);
			if(patterns==null)
				continue;

			for(int j=0;j<patterns.size();j++){
				matchArr	= matchMap.get(MatchOrder[i]);
				if(matchArr==null)
					matchArr	= new ArrayList<int[]>();
				matchArr.addAll(Analyzer.matchImage(patterns.get(j), rgbBytes,limit,MatchOrder[i]));	
				matchMap.put(MatchOrder[i], matchArr);
			}
		}
		
		String[][] flagArr	= sortRate(matchMap);
		int idx	= 0;
		String[][] str	= new String[CharacterCount][];
		for(int i=0;i<flagArr[0].length;i++){
			if(idx>=str.length)
				break;
			if(flagArr[0][i]!=null){
				str[idx]	= new String[3];
				str[idx][0]	= flagArr[0][i];
				str[idx][1]	= String.valueOf(i);
				str[idx][2]	= flagArr[1][i];
				idx++;
			}
		}
		return str;
	}
	
	private static String[][] sortRate(Map<String,List<int[]>> matchMap){
		String[] flagArr	= new String[IMAGE_WIDTH];
		String[] rateArr	= new String[IMAGE_WIDTH];
		
		List<int[]> list	= null;
		for(int i=0;i<MatchOrder.length;i++){
			list	= matchMap.get(MatchOrder[i]);
			if(list==null)
				continue;
			for(int j=0;j<list.size();j++){
				if(!exists(flagArr,rateArr,list.get(j)[0],MatchOrder[i],list.get(j)[1])){
					flagArr[list.get(j)[0]]	= MatchOrder[i];
					rateArr[list.get(j)[0]]	= String.valueOf(list.get(j)[1]);
				}
			}
		}
		return new String[][]{flagArr,rateArr};
	}
	
	private static boolean exists(String[] flagArr,String[] rateArr, int idx, String flag,int rate){
		int minGapCommon	= 10;
		
		int incWeight	= -1;
		for(int i=idx-minGapCommon;i<idx+minGapCommon;i++){
			
			if(i<0 || i>=flagArr.length)
				continue;
			
			if(flagArr[i]==null)
				continue;
			
			incWeight	= getWeightGap(flagArr[i],flag);
			
			if(flagArr[i]==null)
				continue;
			
			if((Integer.parseInt(rateArr[i])+incWeight)<rate){
				flagArr[i]	= null;
				rateArr[i]	= null;
			}else{
				return true;
			}

		}
		return false;
	}
	private static int getWeightGap(String bf,String af){
		if(bf==null || af==null)
			return 0;
		else if(bf.equals(af))
			return 0;
		
		int weightGap	= "8".equals(bf)?WEIGHT_8_CHAR:0;
		if("3".equals(bf) && "1".equals(af))
			return weightGap+RATE_REDUCE_OF_1*-1-WEIGHT_SIMILAR_1_CHAR;
		else if(("4".equals(bf) || "7".equals(bf)) && "1".equals(af))
			return weightGap+RATE_REDUCE_OF_1*-1-WEIGHT_SIMILAR_1_CHAR;
		else if("0".equals(bf) && "9".equals(af))
			return weightGap+RATE_REDUCE_OF_1*-1-WEIGHT_SIMILAR_0_CHAR;
		else if("1".equals(bf) && !"1".equals(af))
			return weightGap+RATE_REDUCE_OF_1;
		else if(!"1".equals(bf) && "1".equals(af))
			return weightGap+RATE_REDUCE_OF_1*-1;
		
		int bfSeq	= 0;
		int afSeq	= 0;
		for(int i=0;i<MatchOrder.length;i++){
			if(MatchOrder[i].equals(bf))
				bfSeq	= i;
			else if(MatchOrder[i].equals(af))
				afSeq	= i;
		}
		return weightGap+(-1)*(bfSeq-afSeq)/3;
	}

	public static void main(String[] args) throws Exception{
		String folderPath	= "D:/workspace/HealInsImg/images3/";
		File folder	= new File(folderPath);
		File[] list	= folder.listFiles(new FileFilter(){
			@Override
			public boolean accept(File file) {
				if(file.getName().endsWith(".tif"))
					return true;
				return false;
			}
		});
		long s1,s2;
		for(int i=0;i<list.length;i++){
			s1	= System.currentTimeMillis();
			System.out.println("파일명 : "+list[i].getName());
			String serial	= Matcher.getSerial(list[i]);
			s2	= System.currentTimeMillis();
			System.out.println("문서번호 : "+serial+" ("+(s2-s1)+" ms)");
		}
	}
}
