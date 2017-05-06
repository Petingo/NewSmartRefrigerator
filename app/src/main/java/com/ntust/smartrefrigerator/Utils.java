package com.ntust.smartrefrigerator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;


public class Utils {
    public static Bitmap QRCodeGenerate(String QRCodeContent){
        int QRCodeWidth = 280;
        int QRCodeHeight = 280;
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result = null;
        try
        {
            result = writer.encode(QRCodeContent, BarcodeFormat.QR_CODE, QRCodeWidth, QRCodeHeight, hints);
        }
        catch (WriterException e)
        {
            e.printStackTrace();
        }
        Bitmap bitmap = Bitmap.createBitmap(QRCodeWidth, QRCodeHeight, Bitmap.Config.ARGB_8888);
        for (int y = 0; y<QRCodeHeight; y++)
        {
            for (int x = 0;x<QRCodeWidth; x++)
            {
                bitmap.setPixel(x, y, result.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();//定義一個畫筆
        paint.setColor(Color.BLACK);//黑色畫筆
        paint.setTextSize(30);
        canvas.drawText("2017/5/6", 75, QRCodeHeight-10 , paint);//繪製文案
        return bitmap;
    }

	public static String byteArrayToHexString(byte [] inarray) {
	    int i, j, in;
	    String [] hex = {"0","1","2","3","4","5","6","7","8","9","A","B","C","D","E","F"};
	    String out= "";
	
	    for(j = 0 ; j < inarray.length ; ++j) {
	        in = (int) inarray[j] & 0xff;
	        i = (in >> 4) & 0x0f;
	        out += hex[i];
	        i = in & 0x0f;
	        out += hex[i];
	    }
	    
	    return out;
	}

    public static String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("yyyy-MM-dd HH:mm", cal).toString();
        return date;
    }


}
