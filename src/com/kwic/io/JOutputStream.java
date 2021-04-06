package com.kwic.io;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <pre>
 * Title		: JOutputStream
 * Description	: OutputStream 저장
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
public class JOutputStream extends OutputStream {
	protected byte buf[];
	protected int count;

	public JOutputStream(){
		this(1024);
	}

	public JOutputStream(int i){
		count = 0;
		buf = new byte[i];
	}
	public void write(InputStream inputstream) throws IOException {
		if(inputstream instanceof ByteArrayInputStream) {
	    	int i = inputstream.available();
	        ensureCapacity(i);
	        count += inputstream.read(buf, count, i);
	        return;
		}
		do{
			int j;
			int k;
			do{
				j = buf.length - count;
				k = inputstream.read(buf, count, j);
				if(k < 0)
	            	return;
	                count += k;
			}while(j != k);
			ensureCapacity(count);
		}while(true);
	}
	
	public void write(int i){
		ensureCapacity(1);
		buf[count] = (byte)i;
		count++;
	}
	private void ensureCapacity(int i){
		int j = i + count;
		if(j > buf.length){
			byte abyte0[] = new byte[Math.max(buf.length << 1, j)];
			System.arraycopy(buf, 0, abyte0, 0, count);
			buf = abyte0;
		}
	}
	public void write(byte abyte0[], int i, int j){
		ensureCapacity(j);
		System.arraycopy(abyte0, i, buf, count, j);
		count += j;
	}

	public void write(byte abyte0[]){
		write(abyte0, 0, abyte0.length);
	}

	public void writeAsAscii(String s){
		int i = s.length();
		ensureCapacity(i);
		int j = count;
		for(int k = 0; k < i; k++)
			buf[j++] = (byte)s.charAt(k);
		
		count = j;
	}
	
	public void writeTo(OutputStream outputstream)throws IOException{
		outputstream.write(buf, 0, count);
	}
	
	public void reset(){
		count = 0;
	}
	
	/**
	* @deprecated Method toByteArray is deprecated
	*/
	public byte[] toByteArray(){
		byte abyte0[] = new byte[count];
		System.arraycopy(buf, 0, abyte0, 0, count);
		return abyte0;
	}
	
	public int size(){
		return count;
	}

	public JInputStream newInputStream(){
		return new JInputStream(buf, count);
	}

	public String toString(){
		if(this.buf.length!=count){
			byte[] buf	= new byte[count];
			System.arraycopy(this.buf, 0, buf, 0, count);
			this.buf	= buf;
		}
		return new String(buf, 0, count);
	}
	
	public void close(){
		
	}
	
	public byte[] getBytes(){
		if(this.buf.length!=count){
			byte[] buf	= new byte[count];
			System.arraycopy(this.buf, 0, buf, 0, count);
			this.buf	= buf;
		}
		return buf;
	}
	
	public int getCount(){
		return count;
	}
}
