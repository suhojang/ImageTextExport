package com.kwic.util;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
* Class  StringUtil.java
* @program String 관련 utility class
* @description String 관련 utility class
* 
* @author 기웅정보통신
* @update :Feb 1, 2007
* @package com.kwic.util; 
* @see
* 
* @DBTable
*/
public class StringUtil {
	 
    
    /**
     * 문자열 내의 특정한 문자열을 모두 지정한 다른 문자열로 바꾼다.
     * 원본 String 이 null 일 경우에는 null 을 반환한다.
     * StringBuffer 를 이용하였으므로 이전의 String 을 이용한 것 보다
     * 월등히 속도가 빠르다. (약 50 ~ 60 배)
     *
     * 사용 예: <BR>
     *
     *   1. 게시판에서 HTML 태그가 안 먹히게 할려면
     *
     *      String str = "<TD>HTML Tag Free Test</TD>";
     *      str = replace(str, "&", "&amp;");
     *      str = replace(str, "<", "&lt;");
     *
     *   2. ' 가 포한된 글을 DB 에 넣을려면
     *
     *      String str2 = "I don't know.";
     *      str2 = replace(str2, "'", "''");
     *
     * @param   String src       원본 String
     * @param   String oldstr    원본 String 내의 바꾸기 전 문자열
     * @param   String newstr    바꾼 후 문자열
     * @return  String           치환이 끝난 문자열
     *
     */
    public static String replace(String src, String oldstr, String newstr) {
        if (src == null) {
            return src;
        }

        StringBuffer dest = new StringBuffer("");
        int len = oldstr.length();
        int srclen = src.length();
        int pos = 0;
        int oldpos = 0;

        while ( (pos = src.indexOf(oldstr, oldpos)) >= 0) {
            dest.append(src.substring(oldpos, pos));
            dest.append(newstr);
            oldpos = pos + len;
        }

        if (oldpos < srclen) {
            dest.append(src.substring(oldpos, srclen));

        }
        return dest.toString();
    }
	
	/**
	 * 1000단위 콤마 처리용.
	 * @param moneyString
	 * @return
	 */
	public static String makeMoneyType(String moneyString)
	{
	    String ls_Part1 = "";
	    String ls_Part2 = "";
		if ( moneyString == null || moneyString.length() == 0 ) return "0";
		
		// 이상한 문자가 들어온경우 원래 값 반환. 
		// ex) 95억 -_-);
		try {
			Double.parseDouble(moneyString);
		} catch(NumberFormatException nfe) {
			return moneyString;
		}
		
		if ( moneyString.indexOf(".") != -1) {
			ls_Part1 = moneyString.substring(0,moneyString.indexOf("."));
			ls_Part2 = moneyString.substring(moneyString.indexOf("."));
		} else {
			ls_Part1 = moneyString;
		}

		DecimalFormat df = new DecimalFormat();
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();

		dfs.setGroupingSeparator(',');
		df.setGroupingSize(3);
		df.setDecimalFormatSymbols(dfs);

		return (df.format(Long.parseLong(ls_Part1))).toString() + ls_Part2;
	}    
    
	public static final String _LINE		= System.getProperty("line.separator");
	public static final int PAGE_PER_BLOCK	= 10;	//블럭당 페이지 갯수
	public static final int ROW_PER_PAGE	= 50;	//페이지당 row 수
	public static final String[] imgNames	= new String[]{
												"/kbss/img/i_firstpage.gif"
												,"/kbss/img/i_beforepage.gif"
												,"/kbss/img/i_nextpage.gif"
												,"/kbss/img/i_lastpage.gif"
											};
	/**
	 * 페이징 문자열 생성
	 * arg	PAGE_PER_BLOCK	:	블럭당 보여줄 페이지 갯수
	 * arg	ROW_PER_PAGE	:	페이지당 보여줄 row갯수
	 * arg 	tRowCnt			:	총 row수
	 * arg	CURRENT_PAGE	:	현재페이지번호
	 * */
	public static String getPageTag( int PAGE_PER_BLOCK, int ROW_PER_PAGE, int tRowCnt, int CURRENT_PAGE){
		StringBuffer sb	= new StringBuffer();
		try{
			//페이지당 20개, 블럭당 5페이지
			int t_page			= tRowCnt/ROW_PER_PAGE; //전체 페이지 숫자 (1,2,...)
			if(tRowCnt%ROW_PER_PAGE!=0)		t_page++;

			int t_block			= t_page/PAGE_PER_BLOCK; //전체 블럭 수 (1,2,...)
			if(t_page%PAGE_PER_BLOCK!=0)	t_block++;

			int n_block			= CURRENT_PAGE/PAGE_PER_BLOCK;	//현재 블럭
			if(CURRENT_PAGE%PAGE_PER_BLOCK!=0)	n_block++;

			int s_page			= (n_block-1)*PAGE_PER_BLOCK+1;	//현재블럭의 시작 페이지
			int e_page			= n_block*PAGE_PER_BLOCK;		//현재블럭의 끝 페이지

			sb.append("<table border='0' align='center' cellpadding='0' cellspacing='0'>"+_LINE);
			sb.append("<tr height='5'><td></td></tr><tr>"+_LINE);
			//이전블럭
			if(n_block>1){
				sb.append("<td width='20'><img alt='이전블럭' src='"+imgNames[0]+"' onfocus='this.blur();' style='cursor:pointer;' onclick='javascript:gotoPage("+((n_block-1)*PAGE_PER_BLOCK)+");'></td>"+_LINE);
			}else{
				sb.append("<td width='20'><img alt='이전블럭' src='"+imgNames[0]+"'></td>"+_LINE);
			}
			//이전페이지
			if(CURRENT_PAGE>1){
				sb.append("<td width='20'><img alt='이전페이지' src='"+imgNames[1]+"'  onfocus='this.blur();' style='cursor:pointer;' onclick='javascript:gotoPage("+(CURRENT_PAGE-1)+");'></td>"+_LINE);
			}else{
				sb.append("<td width='20'><img alt='이전페이지' src='"+imgNames[1]+"'></td>"+_LINE);
			}

			sb.append("<td class='number'>&nbsp;"+_LINE);
			for(int j=s_page;j<=e_page;j++){
				if(j>t_page){
					break;
				}
				if(j==CURRENT_PAGE){
					sb.append("<font color='#FB8D18'><b>"+j+"</b></font>&nbsp;"+_LINE);
				}else{
					sb.append("<span onclick='javascript:gotoPage("+j+");' style='cursor:pointer'><font color='#6695C9'><b>"+j+"</b></font></span>&nbsp;"+_LINE);
				}
			}
			sb.append("&nbsp;</td>"+_LINE);

			//다음페이지
			if(CURRENT_PAGE<t_page){
				sb.append("<td width='20'><img alt='다음페이지' src='"+imgNames[2]+"' onfocus='this.blur();' style='cursor:pointer;' onclick='javascript:gotoPage("+(CURRENT_PAGE+1)+");'></td>"+_LINE);
			}else{
				sb.append("<td width='20'><img alt='다음페이지' src='"+imgNames[2]+"'></td>"+_LINE);
			}

			//다음블럭
			if(n_block<t_block){
				sb.append("<td width='20'><img alt='다음블럭' src='"+imgNames[3]+"' onfocus='this.blur();' style='cursor:pointer;' onclick='javascript:gotoPage("+((n_block*PAGE_PER_BLOCK)+1)+");'></td>"+_LINE);
			}else{
				sb.append("<td width='20'><img alt='다음블럭' src='"+imgNames[3]+"'></td>"+_LINE);
			}
			sb.append("</tr>"+_LINE);
			sb.append("</table>"+_LINE);
		}catch(Exception ex){
			ex.printStackTrace();
			sb.setLength(0);
			sb.append(ex.getMessage());
		}
		return sb.toString();
	}

	/**
	 * 일반 문자열을 html형식으로 변환
	 * */
	public static String htmlFormat(String s){
		s	= StringUtil.replace(s,"&","&amp;");
		s	= StringUtil.replace(s,"\"","&quot;");
		s	= StringUtil.replace(s,"<","&lt;");
		s	= StringUtil.replace(s,">","&gt;");
		s	= StringUtil.replace(s,_LINE,"<br>");
		return s;
	}
	/**
	 * <p>	년,월,일 처리	(yyyy년mm월dd일) <p>
	 *  20080101 형식을 2008년01월01일 로 바꿔준다. 
	 * 
	 *@return java.lang.String
	 *@throws java.lang.Exception  
	 * */
	public static String strToDate(String str){
		
		int strCnt = str.length();
		if(strCnt != 8){
			return str;
		}else{
		
	        String yyyy   = str.substring(0,4);
	        String mm     = str.substring(4,6);
	        String dd     = str.substring(6,8);
	        String date = yyyy + " 년 " + mm + " 월 " + dd + " 일 ";
			return date;
		}
	}

	/**
	 * <p>	년,월 처리	(yyyy년mm월) <p>
	 *  20080101 형식을 2008년01월 로 바꿔준다. 
	 * 
	 *@return java.lang.String
	 *@throws java.lang.Exception  
	 * */
	public static String strToDate2(String str){
		
		int strCnt = str.length();
		if(strCnt != 8){
			return str;
		}else{
		
	        String yyyy   = str.substring(0,4);
	        String mm     = str.substring(4,6);
	        
	        String date = yyyy + " 년 " + mm + " 월 " ;
			return date;
		}
	}	
	/**
	* 특정문자를 주어진 횟수만큼 반복한 문자열 반환<p>
	*@return java.lang.String
	*@throws java.lang.Exception
	*/
	public static String append(String str, int cnt) throws Exception{
		StringBuffer sb	= new StringBuffer();
		for(int i=0;i<cnt-1;i++){
			sb.append(str);
		}
		return sb.toString();
	}

    public static String makeCalendar(int startValue, int endValue, String selectedValue){
    	selectedValue	= selectedValue==null?"":selectedValue;
        String value = "";
        StringBuffer sb	= new StringBuffer();
    	for (int i=startValue; i<=endValue; i++){
    		if(i<=9){value="0"+i;}else{value=""+i;}
    		sb.append("<option value=\"")
    		.append(value)
    		.append("\" "+(selectedValue.equals(value)?"selected":"")+">")
    		.append(value)
    		.append("</option>")
    		.append(System.getProperty("line.separator"));
    	}
    	return sb.toString();
    }
	/**
	 * 구분자에 의해 분리된 string을 분리하여 string array 로 만든다.
	 * 내부적으로  Trim 하지 않는다.
	 * @param str 구분자에 의해 분리된 string
	 * @param delim 구분자
	 * @return 분리된 string array
	 * @exception Execption error occurs
	 */
	public static String[] split(String str,String spr){
        String[] returnVal = null;
        int cnt = 1;

        int index = str.indexOf(spr);
        int index0 = 0;
        while (index >= 0) {
            cnt++;
            index = str.indexOf(spr, index + 1);
        }
        returnVal = new String[cnt];
        cnt = 0;
        index = str.indexOf(spr);
        while (index >= 0) {
            returnVal[cnt] = str.substring(index0, index);
            index0 = index + 1;
            index = str.indexOf(spr, index + 1);
            cnt++;
        }
        returnVal[cnt] = str.substring(index0);
        
        return returnVal;
    }
	/**<pre>
	 * 주어진 문자열이 최대크기가 될때까지 특정문자를 첨부하여 반환한다.
	 * </pre>
	 * @param str String 
	 * @param maxSize int 
	 * @param addChar String 
	 * @param boolean appendLeft 
	 * */
	public static final String addChar(String str,int maxSize,String addChar,boolean appendLeft){
		if(maxSize<1 || addChar==null || addChar.length()<1)
			return str;
		
		if(str==null)
			str	= "";
		
		if(str.getBytes().length>maxSize){
			byte[] bytes	= new byte[maxSize];
			System.arraycopy(str.getBytes(), 0, bytes, 0, bytes.length);
			return new String(bytes);
		}else if(str.getBytes().length==maxSize){
			return str;
		}

		StringBuffer sb	= new StringBuffer();
		
		if(!appendLeft){
			sb.append(str);
		
			while(sb.toString().getBytes().length<maxSize){
				sb.append(addChar);
			}
		}else{
			while(sb.toString().getBytes().length<maxSize-str.getBytes().length){
				sb.append(addChar);
			}
			sb.append(str);
		}

		if(sb.toString().getBytes().length>maxSize){
			byte[] bytes	= new byte[maxSize];
			System.arraycopy(sb.toString().getBytes(), 0, bytes, 0, bytes.length);
			return new String(bytes);
		}
				
		return sb.toString();
	}
	
	public static final String convertTelNo(String telNo){
		telNo	= replace(telNo,"-","");
		
		if(telNo==null)
			return "";
		
		if(telNo.length()==7){
			return telNo.substring(0,3)+"-"+telNo.substring(3);
		}else if(telNo.length()==8){
			return telNo.substring(0,4)+"-"+telNo.substring(4);
		}else if(telNo.length()<7){
			return telNo;
		}		
		
		String tel1	= null;
		String tel2	= null;
		String tel3	= null;
		
		tel3	= telNo.substring(telNo.length()-4);
		
		if(telNo.length()==9){
			tel1	= telNo.substring(0,2);
			tel2	= telNo.substring(2,5);
		}else if(telNo.length()==10 && telNo.startsWith("02")){
			tel1	= telNo.substring(0,2);
			tel2	= telNo.substring(2,6);
		}else if(telNo.length()==10){
			tel1	= telNo.substring(0,3);
			tel2	= telNo.substring(3,6);
		}else if(telNo.length()==11){
			tel1	= telNo.substring(0,3);
			tel2	= telNo.substring(3,7);
		}else if(telNo.length()==12){
			tel1	= telNo.substring(0,4);
			tel2	= telNo.substring(4,8);
		}
		return tel1+"-"+tel2+"-"+tel3;
	}
	
	/**<pre>
	 * 문자열을 구성하는 각각의 문자들이 숫자형인지 판별한다.
	 * </pre>
	 * @param num String
	 * @return boolean
	 * */
	public static boolean isNumber(String num){
		if(num==null || num.trim().length()==0)
			return false;

		num	= num.trim();
		char c;
		for(int i=0;i<num.length();i++){
			c	= num.charAt(i);
			if(c<'0' || c>'9')
				return false;
		}
		return true;
	}
	
	public static List<String> splitSmsString(String msg) throws UnsupportedEncodingException{
		return splitString(msg,80);
	}
	public static List<String> splitString(String str,int size) throws UnsupportedEncodingException{
		StringBuffer sb	= new StringBuffer();
		List<String> list	= new ArrayList<String>();
		if(str.getBytes("EUC-KR").length<=size){
			list.add(str);
			return list;
		}
		
		for(int i=0;i<str.length();i++){
			if(sb.toString().getBytes("EUC-KR").length+new String(new char[]{str.charAt(i)}).getBytes("EUC-KR").length>80){
				list.add(sb.toString());
				sb.setLength(0);
			}
			sb.append(new String(new char[]{str.charAt(i)}));
		}
		if(sb.length()!=0)
			list.add(sb.toString());

		return list;
	} 

	//bytes To Hex String
	public static String bytesToHexString(byte[] bytes) {
		final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for ( int j = 0; j < bytes.length; j++ ) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}
	
	public static String intToHexString(int n){
		int i	= Integer.parseInt(String.valueOf(n), 16);
		return "0x"+StringUtil.addChar(String.valueOf(i), 3, "0", true);
	}
	
	public static String currentTimeToKey(){
		char[] arr1	= new char[]{'A','B','C','D','E','F','G','H','I','J'};
		char[] arr2	= String.valueOf(Calendar.getInstance().getTimeInMillis()).toCharArray();
		
		StringBuffer sb	= new StringBuffer();
		for(int i=3;i<arr2.length;i++)
			sb.append(arr1[Integer.parseInt(String.valueOf(arr2[i]))]);
		return sb.toString();
	}
	
	public static int CONTAIN_TYPE_NUMBER	= 1;
	public static int CONTAIN_TYPE_ENGLISH	= 2;
	public static int CONTAIN_TYPE_SIGNAL	= 3;
	
	public static boolean isContain(String val,int type){
		//숫자
		if(type==CONTAIN_TYPE_NUMBER){
			for(int i=48;i<=57;i++)
				if(val.indexOf(String.valueOf((char)i))>=0)
					return true;
			return false;
		}else if(type==CONTAIN_TYPE_ENGLISH){
			//영문대문자
			for(int i=65;i<=90;i++)
				if(val.indexOf(String.valueOf((char)i))>=0)
					return true;
			//영문소문자
			for(int i=97;i<=122;i++)
				if(val.indexOf(String.valueOf((char)i))>=0)
					return true;
			return false;
		}else if(type==CONTAIN_TYPE_SIGNAL){
			//특수문자
			for(int i=1;i<=127;i++){
				if(i==48){i=58;}
				if(i==65){i=91;}
				if(i==97){i=123;}
				if(val.indexOf(String.valueOf((char)i))>=0)
					return true;
			}
			return false;
		}
		return false;
	}
	
	public static void main(String[] args){
//		String val	= "081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]100000.00[F]0.00[F]3000000.00[F]홍길동[F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]50000.00[F]0.00[F]3050000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00003[F]120133[F]30000.00[F]0.00[F]3080000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00004[F]120133[F]20000.00[F]0.00[F]3100000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00005[F]120133[F]300000.00[F]0.00[F]3400000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]100000.00[F]0.00[F]3000000.00[F]홍길동[F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]50000.00[F]0.00[F]3050000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00003[F]120133[F]30000.00[F]0.00[F]3080000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00004[F]120133[F]20000.00[F]0.00[F]3100000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00005[F]120133[F]300000.00[F]0.00[F]3400000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]100000.00[F]0.00[F]3000000.00[F]홍길동[F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]50000.00[F]0.00[F]3050000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00003[F]120133[F]30000.00[F]0.00[F]3080000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00004[F]120133[F]20000.00[F]0.00[F]3100000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00005[F]120133[F]300000.00[F]0.00[F]3400000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]100000.00[F]0.00[F]3000000.00[F]홍길동[F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]50000.00[F]0.00[F]3050000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00003[F]120133[F]30000.00[F]0.00[F]3080000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00004[F]120133[F]20000.00[F]0.00[F]3100000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00005[F]120133[F]300000.00[F]0.00[F]3400000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]100000.00[F]0.00[F]3000000.00[F]홍길동[F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]50000.00[F]0.00[F]3050000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00003[F]120133[F]30000.00[F]0.00[F]3080000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00004[F]120133[F]20000.00[F]0.00[F]3100000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00005[F]120133[F]300000.00[F]0.00[F]3400000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]100000.00[F]0.00[F]3000000.00[F]홍길동[F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]50000.00[F]0.00[F]3050000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00003[F]120133[F]30000.00[F]0.00[F]3080000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00004[F]120133[F]20000.00[F]0.00[F]3100000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00005[F]120133[F]300000.00[F]0.00[F]3400000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]100000.00[F]0.00[F]3000000.00[F]홍길동[F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]50000.00[F]0.00[F]3050000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00003[F]120133[F]30000.00[F]0.00[F]3080000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00004[F]120133[F]20000.00[F]0.00[F]3100000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00005[F]120133[F]300000.00[F]0.00[F]3400000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]100000.00[F]0.00[F]3000000.00[F]홍길동[F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00001[F]120133[F]50000.00[F]0.00[F]3050000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00003[F]120133[F]30000.00[F]0.00[F]3080000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00004[F]120133[F]20000.00[F]0.00[F]3100000.00[F][F][F][F]20120911[F]063321[L]081[F]KRW[F]00000000000000001[F]20120911[F]00005[F]120133[F]300000.00[F]0.00[F]3400000.00[F][F][F][F]20120911[F]063321";
//		String del	= "[L]";
//		String[] arr	= StringUtil.tocken(val,del);
//		
//		for(int i=0;i<arr.length;i++){
//			System.out.println(arr[i]);
//		}
//		
//		
//		
//        String GR65_SRDT	= "20130131";
//    	Calendar cal	= Calendar.getInstance();
//    	cal.set(Integer.parseInt(GR65_SRDT.substring(0,4))
//    			, Integer.parseInt(GR65_SRDT.substring(4,6))-1
//    			, Integer.parseInt(GR65_SRDT.substring(6))
//    		);
//    	
//    	cal.add(Calendar.MONTH, 1);cal.add(Calendar.DAY_OF_MONTH, -1);
//    	System.out.println(new java.text.SimpleDateFormat("yyyyMMdd").format(cal.getTime()));
		
		
//		System.out.println(convertTelNo("023334444"));
//		System.out.println(convertTelNo("0233334444"));
//		System.out.println(convertTelNo("0333334444"));
//		System.out.println(convertTelNo("03333334444"));
//		System.out.println(convertTelNo("053333334444"));
		
		System.out.println(currentTimeToKey());
		System.out.println(60*60*24*365);
	}
}



