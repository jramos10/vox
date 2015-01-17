package platramos.vox;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.IOException;

public class OCRActivity extends Activity {

    public static final String LOG_TAG = "Vox.OCRActivity";
    public static final String LANG = "eng";
    private String DATA_PATH;
    private String IMAGE_PATH;
    private Bitmap bitmap;

    //Needed for assembleRelease
    public OCRActivity() {
    }

    public OCRActivity(Bitmap bitmap, String dataPath, String imagePath) {
        this.bitmap = bitmap;
        this.DATA_PATH = dataPath;
        this.IMAGE_PATH = imagePath;
    }

    protected void performOCR() {

        try {
            bitmap = rotateImage(bitmap);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Rotate or conversion failed: " + e.toString());
        }

        ImageView iv = (ImageView) findViewById(R.id.image);
        iv.setImageBitmap(bitmap);
        iv.setVisibility(View.VISIBLE);

        String recognizedText = extractTextFromImage(bitmap);

        // clean up and show
        if (LANG.equalsIgnoreCase("eng")) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }
        if (recognizedText.length() != 0) {
            ((TextView) findViewById(R.id.field)).setText(recognizedText.trim());
        }
    }

    private String extractTextFromImage(Bitmap bitmap) {
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, LANG);
        baseApi.setImage(bitmap);
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();

        Log.v(LOG_TAG, "Extraction Result: " + recognizedText);
        return recognizedText;
    }

    private Bitmap rotateImage(Bitmap bitmap) throws IOException {

        ExifInterface exif = new ExifInterface(IMAGE_PATH);
        int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        Log.v(LOG_TAG, "Orientation: " + exifOrientation);

        int rotate = 0;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
        }

        Log.v(LOG_TAG, "Rotation: " + rotate);

        if (rotate != 0) {

            // Getting width & height of the given image.
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            // Setting pre rotate
            Matrix matrix = new Matrix();
            matrix.preRotate(rotate);

            // Rotating Bitmap
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

            // tesseract req. ARGB_8888
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        }
        return bitmap;
    }
}
