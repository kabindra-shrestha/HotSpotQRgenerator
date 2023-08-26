package com.kabindra.hotspotqrgenerator;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.TRANSPARENT;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.HashMap;
import java.util.Map;

public class QRCodeUtils {

    public Bitmap generateQRCode(String qrCode, int width, int height) {
        // setting size of qr code
        int smallestDimension = width < height ? width : height;

        // setting parameters for qr code
        String charset = "UTF-8";
        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        return CreateQRCode(qrCode, charset, hintMap, smallestDimension, smallestDimension);
    }

    private Bitmap CreateQRCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth) {
        try {
            // generating qr code.
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset), BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);

            // converting bitmatrix to bitmap
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    // for black and white
                    pixels[offset + x] = matrix.get(x, y) ? BLACK : TRANSPARENT;
                    // for custom color
//                    pixels[offset + x] = matrix.get(x, y) ? ResourcesCompat.getColor(getResources(),R.color.colorB,null) :WHITE;
                }
            }

            // creating bitmap
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            // getting the logo
//            Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.drawable.launcher);
            // returning bitmap to image view
            return bitmap;
        } catch (Exception er) {
            Log.e("QrGenerate", er.getMessage());
        }
        return null;
    }

    public Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);

        int centreX = (canvasWidth - overlay.getWidth()) / 2;
        int centreY = (canvasHeight - overlay.getHeight()) / 2;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        return combined;
    }

}
