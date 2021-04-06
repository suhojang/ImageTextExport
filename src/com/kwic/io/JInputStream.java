package com.kwic.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * <pre>
 * Title		: JInputStream
 * Description	: InputStream 저장
 * Date			: 2012.9.01
 * Copyright	: Copyright	(c)	2012
 * Company		: KWIC
 * 
 *    수정일                  수정자                      수정내용
 * -------------------------------------------
 * 
 * </pre>
 *
 * @author 장정훈 
 * @version	1.0
 * @since 1.6
 */
public class JInputStream extends ByteArrayInputStream {
	private static final byte EMPTY_ARRAY[] = new byte[0];
	
	public JInputStream(){
		this(EMPTY_ARRAY, 0);
	}
	
	public JInputStream(byte abyte0[], int i){
		super(abyte0, 0, i);
	}
	
	public JInputStream(byte abyte0[], int i, int j){
		super(abyte0, i, j);
	}
	
	public byte[] getBytes(){
		return buf;
	}
	
	public int getCount(){
		return count;
	}
	
	public void close()throws IOException{
		reset();
	}
	
	public void setBuf(byte abyte0[]){
		buf = abyte0;
		pos = 0;
		count = abyte0.length;
	}
}
 
