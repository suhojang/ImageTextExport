import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.niceinfo.image.parser.Matcher;

/**
 * @파일명	: Test
 * @작성일	: 2021. 4. 1.
 * @작성자	: 장수호
 * @설명		: 건강보험 발급번호 추출 테스트 Class
 * @변경이력	:
 */
public class Test {
	private final static String _LINE		= System.getProperty("line.separator");

	public static void main(String[] args) {
		//발급번호를 추출 할 이미지들의 parent 경로
		String _folderPath	= "D:\\eGovFrameDev-3.8.0-64bit\\workspace\\Tess4Test\\images\\success";
		File folder	= new File(_folderPath);
		File[] list	= folder.listFiles(new FileFilter(){
			@Override
			public boolean accept(File file) {
				if(file.getName().endsWith(".tif"))
					return true;
				return false;
			}
		});
		
		//배포 된 tessdata폴더의 경로
		String tessFilePath	= "D:/eGovFrameDev-3.8.0-64bit/workspace/Tess4Test/tessdata";
		
		StringBuffer sb		= new StringBuffer();
		sb.append("[" + new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(new Date()) + "] 발급번호 추출 작업을 시작 합니다." + _LINE);
		
		for (int i = 0; i < list.length; i++) {
			long s1 = 0, s2 = 0;
			String serial	= "[Image IOException] 발급번호 추출 중 오류가 발생 하였습니다.";
			try{
				s1		= System.currentTimeMillis();
				serial	= Matcher.getSerial(list[i], new File(tessFilePath));
				s2		= System.currentTimeMillis();
				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				sb.append("순번 \t: " + (i+1) + _LINE);
				sb.append("파일명 \t: " + list[i].getName() + _LINE);
				sb.append("추출번호 \t: " + serial + " ("+(s2-s1)+" ms)" + _LINE + _LINE);
			}
		}
		sb.append("[" + new SimpleDateFormat("yyyy-MM-dd kk:mm:ss").format(new Date()) + "] 발급번호 추출 작업을 종료 합니다." + _LINE);
		
		System.out.println(sb.toString());
	}
}
