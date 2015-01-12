package platramos.vox;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;

public class PhotoCaptureActivity extends Activity {

    protected Button _button;
    protected ImageView _image;
    protected TextView _field;
    protected String _pathForSavedImage;
    protected boolean _taken;

    protected static final String PHOTO_TAKEN	= "photo_taken";
    private String LOG_TAG = "Vox.PhotoCaptureActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        _image = ( ImageView ) findViewById( R.id.image );
        _field = ( TextView ) findViewById( R.id.field );
        _button = ( Button ) findViewById( R.id.button );
        _button.setOnClickListener(new ButtonClickHandler());

        _pathForSavedImage = Environment.getExternalStorageDirectory() + "/Vox/images/ocr.jpg";
    }

    public class ButtonClickHandler implements View.OnClickListener {
        public void onClick( View view ){
            Log.i(LOG_TAG, "ButtonClickHandler.onClick()");
            startCameraActivity();
        }
    }

    protected void startCameraActivity() {
        Log.i(LOG_TAG, "startCameraActivity()");
        File file = new File(_pathForSavedImage);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(LOG_TAG, "resultCode: " + resultCode);
        switch(resultCode) {
            case 0:
                Log.i(LOG_TAG, "User cancelled");
                break;

            case -1:
                onPhotoTaken();
                break;
        }
    }

    protected void onPhotoTaken() {
        Log.i(LOG_TAG, "onPhotoTaken");

        _taken = true;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        Bitmap bitmap = BitmapFactory.decodeFile(_pathForSavedImage, options);

        _image.setImageBitmap(bitmap);

        _field.setVisibility(View.GONE);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onRestoreInstanceState()");
        if( savedInstanceState.getBoolean(PhotoCaptureActivity.PHOTO_TAKEN)) {
            onPhotoTaken();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean( PhotoCaptureActivity.PHOTO_TAKEN, _taken );
    }
}
