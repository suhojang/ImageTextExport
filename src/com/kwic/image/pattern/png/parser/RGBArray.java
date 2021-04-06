package com.kwic.image.pattern.png.parser;

import java.awt.image.*;
import java.awt.*;

/**
 * Image RGB dont les pixels peuvents etre lues et modifiés via un tableau d'entier
 */
public class RGBArray {
   private static boolean warningMIS=false;
   private int width;
   private int height;
   private int pixels[];
   private Image image;
   private MemoryImageSource mis;

    public RGBArray(int width,int height,boolean alpha) {
       this.width=width;
       this.height=height;
       this.image=null;
       this.mis=null;
       this.pixels=null;
       
       try
       {
          Class.forName("java.awt.image.BufferedImage");
          PixelsBufferBI pbi=new PixelsBufferBI(this.width,this.height,alpha);
          this.pixels=pbi.getPixels();
          this.image=pbi.getImage();
       }
       catch(ClassNotFoundException e) //Old JVM ?
       {
          if(!warningMIS)
             System.out.println("Old JVM => using MemoryImageSource");
          warningMIS=true;
          this.pixels=new int[this.width*this.height];
          
          if(alpha)
             this.mis=new MemoryImageSource(this.width,this.height,new DirectColorModel(32, 0xff0000, 0xff00, 0xff,0xFF000000), this.pixels, 0, this.width);
          else
             this.mis=new MemoryImageSource(this.width,this.height,new DirectColorModel(24, 0xff0000, 0xff00, 0xff), this.pixels, 0, this.width);
             
          this.mis.setAnimated(true);
         this.mis.setFullBufferUpdates(true);
         this.image = Toolkit.getDefaultToolkit().createImage(this.mis);   
       }
    }
    
    public void updateMIS()
    {
       if(this.isMIS())
          this.mis.newPixels();
    }
    
    public boolean isMIS()
    {
       return (this.mis!=null);
    }
    
    public RGBArray(int width,int height) 
    {
       this(width,height,false);
    }
    
    public Image getImage()
    {
       return this.image;
    }
    
    public int[] getPixels()
    {
       return this.pixels;
    }    
    public int getWidth(){
    	return this.width;
    }
    public int getHeight(){
    	return this.height;
    }
    
    public RGBArray(Image image)
    {
       this(image.getWidth(null),image.getHeight(null));
       PixelGrabber pg=new PixelGrabber(image,0,0,this.width,this.height,this.pixels,0,this.width);
       try
       {
          pg.grabPixels();
       }
       catch(InterruptedException ie)
       {
          System.out.println(ie);
       }
    }
    
    
    class PixelsBufferBI
   {
      private Image image;
      private int pixels[];
      public PixelsBufferBI(int width,int height,boolean alpha)
      {   
            BufferedImage BIImage;
            if(alpha)
               BIImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_ARGB);
            else
               BIImage = new BufferedImage(width, height,BufferedImage.TYPE_INT_RGB);
            pixels=((DataBufferInt)(BIImage.getRaster().getDataBuffer())).getData(); 
            image=(Image)BIImage;         
      }
      public Image getImage()
      {
         return this.image;
      }
      public int[] getPixels()
      {
         return this.pixels;
      }
   }
    
}