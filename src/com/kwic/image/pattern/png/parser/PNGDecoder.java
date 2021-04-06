package com.kwic.image.pattern.png.parser;

import java.awt.Image;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class PNGDecoder{
  /**
  * Inpute stream for raw PNG data
  */
    private InputStream in;
    
      
    /**
     * Define PNG file SIG
     */
    public static final byte[] PNG_STREAM_SIG = new byte[] {-119, 80, 78, 71, 13, 10, 26, 10, 0, 0, 0, 13};
     
     
    /**
     * Constant for color type
     */
    public final static int COLOR_GREY=0;
    public final static int COLOR_TRUE=2;
    public final static int COLOR_INDEX=3;
    public final static int COLOR_GREY_ALPHA=4;
    public final static int COLOR_TRUE_ALPHA=6;
    
    
    /**
     * filter type
     */
    public final static int FILTER_NONE=0;
    public final static int FILTER_SUB=1;
    public final static int FILTER_UP=2;
    public final static int FILTER_AVG=3;
    public final static int FILTER_PAETH=4;    
    
    
    /**
     * Define critical chunk
     */
    public final static int CHUNK_IHDR = bytesToInt("IHDR".getBytes());
    public final static int CHUNK_PLTE = 1347179589;//bytesToInt("PLTE".getBytes());
    public final static int CHUNK_IDAT = 1229209940;//bytesToInt("IDAT".getBytes());
    public final static int CHUNK_IEND = bytesToInt("IEND".getBytes());
    
    //@TODO: we may add extended chunk
     
     //System.out.println("CHUNK_IEND="+CHUNK_IEND); //Uncomment to get a chunk int value to use in switch...case statement


    /**
     * Constructs a PNGDecoder object.
     *
     * @param in input stream to read PNG image from.
     */    
    public PNGDecoder(InputStream in) 
    {
        this.in = in;
    }
    
    
   /**
    * Reads a byte from input stream.
    *
    * @return byte read
    */
    private byte read() throws IOException 
    {
        byte b = (byte)in.read();
        return(b);
    }


   /**
    * Reads a int from input stream/
    *
    * @return int read
    */
    private int readInt() throws IOException 
    {
        byte b[] = read(4);
        return(((b[0]&0xff)<<24) +
               ((b[1]&0xff)<<16) +
               ((b[2]&0xff)<<8) +
               ((b[3]&0xff)));
    }


   /**
    * Reads n bytes from input stream.
    *
    * @param count number of bytes to read
    *
    * @return bytes array with values read
    */
    private byte[] read(int count) throws IOException 
    {
        byte[] result = new byte[count];
        for(int i = 0; i < count; i++) 
        {
            result[i] = read();
        }
        return result;
    }
    

   /**
    * Compare two bytes array
    * 
    * @param b1 input byte array 1
    * @param b2 input byte array 2
    *
    * @return true if each entries of arrays are equals or false if array differs
    */
    private boolean compare(byte[] b1, byte[] b2) 
    {
        if(b1.length != b2.length) 
        {
            return false;
        }
        
        for(int i = 0; i < b1.length; i++) 
        {
            if(b1[i] != b2[i]) 
            {
                return false;
            }
        }
        return true;
    }
  
    
    /**
    * Convert 4 bytes array to int
    */
   private static int bytesToInt(byte[] b)
   {
      int r=b[0]<<24 | b[1]<<16 | b[2]<<8 | b[3];
         
      return r;
   }
   
   
    /**
    * Convert int to a 4 character string
    */
   private static String intToString(int i)
   {
      byte b[]=new byte[4];
      b[0]=(byte)(i>>24);
      b[1]=(byte)(i>>16);
      b[2]=(byte)(i>>8);
      b[3]=(byte)(i);
      return new String(b);
   }   

   /**
    * Skip the requested numbers of bytes from the input stream
    *
    * @param bytes number of bytes to skip
    */
   public void skipFully(long bytes) throws IOException
   {
      while(bytes > 0)
      {
         long skipped = in.skip(bytes);
         if(skipped <= 0)
          throw new EOFException();
         bytes -= skipped;
      }
   }
   
   /** 
    * Decodes image from the input stream 
    *
    * @return Image object
    */
    public Image decodeImage() throws IOException 
     {
        RGBArray rgb=this.decodeRGBArrayImage();
        rgb.updateMIS();
        return rgb.getImage();
        
     }
   
    /**
     * Decodes image from the input stream.
     *
     * @return RGBArrayImage object
     */
    public RGBArray decodeRGBArrayImage() throws IOException 
    {
       /**
        * Read PNG SIG
        */
      byte sig[] = read(12);
      
      if(!compare(sig, PNG_STREAM_SIG))
         throw(new RuntimeException("Wrong SIG ID " + new String(sig)));
      
      /**
       * Read PNG HEADER
       */
      int chunkIdHeader=readInt();
       
      if(chunkIdHeader!=CHUNK_IHDR)
         throw(new RuntimeException("Wrong HEADER ID " + intToString(chunkIdHeader)));
         
      int width = readInt();
        int height = readInt();   
        int bitDepth = read();
        int colorType = read();
        read();
        read();
        read();
        
        //Read CRC => may verify here
        readInt();
        
         
        Inflater inflater = new Inflater();
        int chunkId;
        int totalImageSize=(width*height*8)/bitDepth;
        
        /*
         * Prepare buffer for uncompressed image data
         */
      switch(colorType)
      {
         case COLOR_GREY_ALPHA :
            totalImageSize*=2;
         break;
         
         case COLOR_TRUE :
            totalImageSize*=3;
         break;
         
         case COLOR_TRUE_ALPHA :
            totalImageSize*=4;
         break;
      }
        byte imageData[]=new byte[totalImageSize+height];   //Add height for the filter type value at each scanline
        
        
        
        /**
         * Now we wil read all other chunks in a loop ( and ignoring extended or unkwnow chunk )
         */
        do
        {
           int size=readInt();   //Read chunk size
           chunkId=readInt();   //Read chunk ID
           
           
           //Decode chunk data
           switch(chunkId)
           {
              /**
               * Skip PLTE chunk
               */
              case CHUNK_PLTE :
                 //@TODO: to be implemented
                 skipFully(size);
              break;
              
              /**
               * IDAT Chunk (may be repeated multiple times, but should be consecutive, pff... why for ? )
               */
               case CHUNK_IDAT :
                  byte b[]=new byte[size];
                  in.read(b);   
                  inflater.setInput(b);
                  try
                  {
                  inflater.inflate(imageData,inflater.getTotalOut(),imageData.length-inflater.getTotalOut());
               }
               catch(DataFormatException dfe)
               {
                  throw new IOException(dfe.getMessage());
               }
               break;
              
              /**
               * Skip unknow chunk
               */
              default :
                 skipFully(size);
           }
           
           
           //Read CRC => @TODO: We may verify CRC of current chunk here
           readInt();
        }while(chunkId!=CHUNK_IEND);
        
        
        /**
         * And now it is time to decode & unfilter the image
         */
         RGBArray result;
         if(colorType==COLOR_GREY_ALPHA || colorType==COLOR_TRUE_ALPHA)
            result=new RGBArray(width, height,true);
         else
            result=new RGBArray(width, height,false);
            
         int srcByte=0;
         int dstPixel=0;
         int filterType;
         int pixels[]=result.getPixels();
         
         switch(colorType)
         {
            case COLOR_INDEX :
              //@TODO: to be implemented
               System.out.println("Decoding of this color type is not yet implemented");
            break;
            
            case COLOR_GREY :
              for(int y=0;y<height;y++)
               {
                 filterType=imageData[srcByte++];
                  for(int x=0;x<width;x++)
                  {
                     int grey=imageData[srcByte]&0xFF;
                     grey|=(grey<<8) | (grey<<16);
                     pixels[dstPixel++]=grey;
                     srcByte+=1;
                  }
                  filter(filterType,pixels,y,width);
               }
            break;
            
            case COLOR_GREY_ALPHA :
              for(int y=0;y<height;y++)
               {
                 filterType=imageData[srcByte++];
                  for(int x=0;x<width;x++)
                  {
                     int grey=imageData[srcByte]&0xFF;
                     grey|=(grey<<8) | (grey<<16);
                     int alpha=(imageData[srcByte+1]&0xFF)<<24;
                     pixels[dstPixel++]=grey | alpha;
                     srcByte+=2;
                  }
                  filter(filterType,pixels,y,width);
               }
            break;
            
            case COLOR_TRUE :
               for(int y=0;y<height;y++)
               {
                 filterType=imageData[srcByte++];
                  for(int x=0;x<width;x++)
                  {
                     pixels[dstPixel++]=((imageData[srcByte]&0xFF)<<16) | ((imageData[srcByte+1]&0xFF)<<8) | (imageData[srcByte+2]&0xFF);
                     srcByte+=3;
                  }
                  filter(filterType,pixels,y,width);
               }
            break;
            
            case COLOR_TRUE_ALPHA :
              for(int y=0;y<height;y++)
              {
                 filterType=imageData[srcByte++];
                  for(int x=0;x<width;x++)
                  {
                     pixels[dstPixel++]=((imageData[srcByte]&0xFF)<<16) | ((imageData[srcByte+1]&0xFF)<<8) | (imageData[srcByte+2]&0xFF) | ((imageData[srcByte+3]&0xFF)<<24);
                     srcByte+=4;
                  }
                  filter(filterType,pixels,y,width);
              }
            break;            
            
            default :
               System.out.println("Decoding of this color type is not yet implemented and unknown...");
            break;
            
            
         }
      
        return result;
    }
    
    
    /**
     * Unfilter a PNG image row.
     *
     * @param filterType filter type for this row
     * @param pixels pixels array of the image
     * @param y line number to unfilter
     * @param width width of the row
     */
    private void filter(int filterType,int pixels[],int y,int width)
    {
       int ofsRow=y*width;
      /**
       * Perform filtering if any
       */
      switch(filterType)
      { 
         case FILTER_NONE:
         break;
         case FILTER_SUB:
         {
            int AA=0;
            int RA=0;
            int GA=0;
            int BA=0;
            for(int n=0;n<width;n++)
            {
               int pixel=pixels[ofsRow+n];
               int A=(pixel>>24)&0xFF;
               int R=(pixel>>16)&0xFF;
               int G=(pixel>>8)&0xFF;
               int B=pixel&0xFF;
               
               A+=AA;
               R+=RA;
               G+=GA;
               B+=BA;
               
               A&=0xFF;
               R&=0xFF;
               G&=0xFF;
               B&=0xFF;
               
               pixels[ofsRow+n]=(A<<24) |  (R<<16) | (G<<8) | B;
               
               AA=A;
               RA=R;
               GA=G;
               BA=B;
            }
         }
         break;
         case FILTER_UP:
         {
            int ofsRowB=ofsRow-width;
            for(int n=0;n<width;n++)
            {
               int pixelB=0;
               
               if(y>0)
                  pixelB=pixels[ofsRowB+n];
               
               int AB=(pixelB>>24)&0xFF;
               int RB=(pixelB>>16)&0xFF;
               int GB=(pixelB>>8)&0xFF;
               int BB=pixelB&0xFF;
               
               int pixel=pixels[ofsRow+n];
               int A=(pixel>>24)&0xFF;
               int R=(pixel>>16)&0xFF;
               int G=(pixel>>8)&0xFF;
               int B=pixel&0xFF;
               
               A+=AB;
               R+=RB;
               G+=GB;
               B+=BB;
               
               A&=0xFF;
               R&=0xFF;
               G&=0xFF;
               B&=0xFF;
               
               pixels[ofsRow+n]=(A<<24) | (R<<16) | (G<<8) | B;
            }
         }
         break;
         case FILTER_AVG:
         {
            int AA=0;
            int RA=0;
            int GA=0;
            int BA=0;
            
         
            int ofsRowB=ofsRow-width;
            for(int n=0;n<width;n++)
            {
               int pixelB=0;
               if(y>0)
                  pixelB=pixels[ofsRowB+n];
               int AB=(pixelB>>24)&0xFF;
               int RB=(pixelB>>16)&0xFF;
               int GB=(pixelB>>8)&0xFF;
               int BB=pixelB&0xFF;
               
               int pixel=pixels[ofsRow+n];
               int A=(pixel>>24)&0xFF;
               int R=(pixel>>16)&0xFF;
               int G=(pixel>>8)&0xFF;
               int B=pixel&0xFF;
               
               A+=(AB+AA)>>1;
               R+=(RB+RA)>>1;
               G+=(GB+GA)>>1;
               B+=(BB+BA)>>1;
               
               A&=0xFF;
               R&=0xFF;
               G&=0xFF;
               B&=0xFF;
               
               pixels[ofsRow+n]=(A<<24) |(R<<16) | (G<<8) | B;
               
               AA=A;
               RA=R;
               GA=G;
               BA=B;
            }
         }
         break;
         case FILTER_PAETH:
         {
            int AA=0;
            int RA=0;
            int GA=0;
            int BA=0;
            
            int AC=0;
            int RC=0;
            int GC=0;
            int BC=0;
            
         
            int ofsRowB=ofsRow-width;
            for(int n=0;n<width;n++)
            {
               int pixelB=0;
               if(y>0)
                  pixelB=pixels[ofsRowB+n];
               int AB=(pixelB>>24)&0xFF;
               int RB=(pixelB>>16)&0xFF;
               int GB=(pixelB>>8)&0xFF;
               int BB=pixelB&0xFF;
               
               int pixel=pixels[ofsRow+n];
               int A=(pixel>>24)&0xFF;
               int R=(pixel>>16)&0xFF;
               int G=(pixel>>8)&0xFF;
               int B=pixel&0xFF;
               
               int PA=AA+AB-AC;
               int PAA=Math.abs(PA-AA);
               int PAB=Math.abs(PA-AB);
               int PAC=Math.abs(PA-AC);
               if(PAA<=PAB && PAA<=PAC)
                  A+=AA;
               else
               {
                  if(PAB<=PAC)
                     A+=AB;
                  else
                     A+=AC;
               }
               
               
               int PR=RA+RB-RC;
               int PRA=Math.abs(PR-RA);
               int PRB=Math.abs(PR-RB);
               int PRC=Math.abs(PR-RC);
               if(PRA<=PRB && PRA<=PRC)
                  R+=RA;
               else
               {
                  if(PRB<=PRC)
                     R+=RB;
                  else
                     R+=RC;
               }
               
               
               int PG=GA+GB-GC;
               int PGA=Math.abs(PG-GA);
               int PGB=Math.abs(PG-GB);
               int PGC=Math.abs(PG-GC);
               if(PGA<=PGB && PGA<=PGC)
                  G+=GA;
               else
               {
                  if(PGB<=PGC)
                     G+=GB;
                  else
                     G+=GC;
               }
               
               int PB=BA+BB-BC;
               int PBA=Math.abs(PB-BA);
               int PBB=Math.abs(PB-BB);
               int PBC=Math.abs(PB-BC);
               if(PBA<=PBB && PBA<=PBC)
                  B+=BA;
               else
               {
                  if(PBB<=PBC)
                     B+=BB;
                  else
                     B+=BC;
               }
               
               A&=0xFF;
               R&=0xFF;
               G&=0xFF;
               B&=0xFF;
               
               pixels[ofsRow+n]=(A<<24) | (R<<16) | (G<<8) | B;
               
               AA=A;
               RA=R;
               GA=G;
               BA=B;
               
               AC=AB;
               RC=RB;
               GC=GB;
               BC=BB;
               
               
            }
         }
         break;
         default:
            System.out.println("Decoding of this filter type is not yet implemented and unknown..." + filterType);
         break;
      }
       
    }
}