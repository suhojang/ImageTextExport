package com.niceinfo.image.parser.pattern;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;

import com.kwic.util.StringUtil;
import com.niceinfo.image.parser.Analyzer;
import com.niceinfo.io.ImgCv;

public class MakePatterns {
	public static void writePatternsRgb(String path,int[][][] rgb) throws Exception{
		FileWriter fw	= null;
		try{
			fw	= new FileWriter(path);
			for(int i=0;i<rgb.length;i++){
				fw.write(
						(i==0?"\t":"\t,")
						+"{"
						);
				
				for(int j=0;j<rgb[i].length;j++){
					fw.write(
								(j==0?"":",")
								+"{"
								+StringUtil.intToHexString(rgb[i][j][0])
								+","+StringUtil.intToHexString(rgb[i][j][1])
								+","+StringUtil.intToHexString(rgb[i][j][2])
								+","+StringUtil.intToHexString(rgb[i][j][3])
								+"}"
							);
				}
				fw.write(
						"\t}"
						+"\n"
						);
			}
			fw.flush();
		}catch(Exception e){
			throw e;
		}finally{
			try{if(fw!=null)fw.close();}catch(Exception e){}
		}
	}
	
	public static void readPatterns(String path) throws Exception{
		File folder	= new File(path);
		File[] list	= folder.listFiles(new FileFilter(){
			@Override
			public boolean accept(File file) {
				if(file.getName().endsWith(".png"))
					return true;
				return false;
			}
		});
		int[][][] rgb	= null;
		byte[] bytes	= null;
		for(int i=0;i<list.length;i++){
			bytes	= ImgCv.readFile(list[i]);
			rgb	= Analyzer.toPNGArrays(bytes);
			writePatternsRgb(path+"/"+list[i].getName().replaceAll(".png", ".ptn"),rgb);
		}
	}
	
	public static void main(String[] args) throws Exception{
		String folderPath	= "E:/eGovFrame/eGovFrameDev-3.5.0-64bit/workspace/HealInsImg/patterns";
		readPatterns(folderPath);
	}
}
