package com.greetty.printerdemo.utils;

import java.nio.ByteBuffer;
import java.util.Hashtable;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;

public class BitImageEncoder {

	public static BitMatrix deleteWhiteSpace(BitMatrix matrix){  
	    int[] rec = matrix.getEnclosingRectangle();  
	    int resWidth = rec[2] + 1;  
	    int resHeight = rec[3] + 1;  
	  
	    BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);  
	    resMatrix.clear();  
	    for (int x = 0; x < resWidth; x++) {  
	        for (int y = 0; y < resHeight; y++) {  
	            if (matrix.get(x + rec[0], y + rec[1]))  
	                resMatrix.set(x, y);  
	        }  
	    }  
	    return resMatrix;  
	}  
	
	public static Bitmap matrixToBitmap(BitMatrix matrix){  
	    int[] rec = matrix.getEnclosingRectangle();  
	    int resWidth = rec[2] + 1;  
	    int resHeight = rec[3] + 1;  
	  
		Bitmap bmp = Bitmap.createBitmap(resWidth, resHeight+10, Config.ARGB_8888);
		bmp.eraseColor(Color.WHITE);
		
        for (int y = 0; y < resHeight; y++) {  
        	for (int x = 0; x < resWidth; x++) {  
	            if (matrix.get(x + rec[0], y + rec[1]))
	            	bmp.setPixel(x, y, Color.BLACK);
	        }  
	    }  
	    return bmp;  
	}  
	
	private static Bitmap encodeBarcode(String text, int width, int height) throws WriterException {
		
        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();  

        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");  
        
        BitMatrix m = new MultiFormatWriter().encode(text,  
                BarcodeFormat.CODE_128, width, height, hints);

        return matrixToBitmap(m);
        
//	    int[] rec = m.getEnclosingRectangle();  
//	    int mw = rec[2] + 1;  
//	    int mh = rec[3] + 1;  
//
////        m = deleteWhiteSpace(m);
////        int mw = m.getWidth();
////		int mh = m.getHeight();
//        
//		Bitmap bmp = Bitmap.createBitmap(mw, mh+10, Config.ARGB_8888);
//		Canvas c = new Canvas(bmp);
//		Paint p = new Paint();
//		
//		c.drawColor(Color.WHITE);
//		p.setColor(Color.BLACK);
//			
//		for (int y = 0; y < mh; y++) {
//			for (int x = 0; x < mw; x++) {
//				if (m.get(x, y)) {
//					c.drawPoint(x, y, p);
//				}
//			}
//		}
//			
//		return bmp;
	}
	
	private static Bitmap encodeQRCode2(String text, ErrorCorrectionLevel ecl, int width, int height) throws WriterException {

		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.ERROR_CORRECTION, ecl);
	
		BitMatrix m = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        return matrixToBitmap(m);

//        m = deleteWhiteSpace(m);
//		int mw = m.getWidth();
//		int mh = m.getHeight();
//        
//		Bitmap bmp = Bitmap.createBitmap(width, height, Config.ARGB_8888);
//		Canvas c = new Canvas(bmp);
//		Paint p = new Paint();
//		
//		c.drawColor(Color.WHITE);
//		p.setColor(Color.BLACK);
//			
//		for (int y = 0; y < mh; y++) {
//			for (int x = 0; x < mw; x++) {
//				if (m.get(x, y)) {
//					c.drawPoint(x, y, p);
//				}
//			}
//		}
//			
//		return bmp;
	}
	
	private static Bitmap encodeQRCode(String text, ErrorCorrectionLevel errorCorrectionLevel,
			int scale) throws WriterException {

		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
		hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);
	
		QRCode code = new QRCode();
	
		Encoder.encode(text, errorCorrectionLevel, hints, code);
	
		ByteMatrix m = code.getMatrix();
		int mw = m.getWidth();
		int mh = m.getHeight();
	
		// תΪ��ɫͼ
		final int IMG_WIDTH = mw*scale;
		final int IMG_HEIGHT = mh*scale;
	
		Bitmap bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT+10, Config.ARGB_8888);
		Canvas c = new Canvas(bmp);
		Paint p = new Paint();
			
		c.drawColor(Color.WHITE);
		p.setColor(Color.BLACK);
			
		for (int y = 0; y < mh; y++) {
			for (int x = 0; x < mw; x++) {
				if (m.get(x, y) == 1) {
					c.drawRect(x*scale, y*scale, 
							(x+1)*scale, (y+1)*scale, p);
				}
			}
		}
			
		return bmp;
	}

	//public static final int MAX_BIT_WIDTH = 384;
	public static final int MAX_BIT_WIDTH = 576;

	private static byte[] genBitmapCode(Bitmap bm, boolean doubleWidth, boolean doubleHeight) {
        int w = bm.getWidth();
        int h = bm.getHeight();
        if(w > MAX_BIT_WIDTH)
            w = MAX_BIT_WIDTH;
        int bitw = ((w+7)/8)*8;
        int bith = h;
        int pitch = bitw / 8;
        byte[] cmd = {0x1D, 0x76, 0x30, 0x00, (byte)(pitch&0xff), (byte)((pitch>>8)&0xff), (byte) (bith&0xff), (byte) ((bith>>8)&0xff)};
        byte[] bits = new byte[bith*pitch];

        // ����
        if(doubleWidth)
        	cmd[3] |= 0x01;
        // ����
        if(doubleHeight)
        	cmd[3] |= 0x02;
        
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int color = bm.getPixel(x, y);
                if ((color&0xFF) < 128) {
                    bits[y * pitch + x/8] |= (0x80 >> (x%8));
                }
            }
        }
        ByteBuffer bb = ByteBuffer.allocate(cmd.length+bits.length);
        bb.put(cmd);
        bb.put(bits);
        return bb.array();
    }
	    
	public static byte[] genQrcodePrinterCommand(ErrorCorrectionLevel level, String text, int width, int height) throws WriterException {

		//Bitmap bmp = encodeQRCode(text, ErrorCorrectionLevel.L, scale);
		//Bitmap bmp = encodeQRCode(text, ErrorCorrectionLevel.M, 350, 1);
		//Bitmap bmp = encodeQRCode2(text, ErrorCorrectionLevel.H, width, height);
		Bitmap bmp = encodeQRCode2(text, level, width, height);
		
		return genBitmapCode(bmp, false, false);
		
	}
	
	public static byte[] genBarcodePrinterCommand(String text, int width, int height) throws WriterException {
		
		Bitmap bmp = encodeBarcode(text, width, height);
		
		return genBitmapCode(bmp, false, false);

	}
	
}
