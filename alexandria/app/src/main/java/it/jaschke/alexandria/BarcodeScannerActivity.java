package it.jaschke.alexandria;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;


public class BarcodeScannerActivity extends ActionBarActivity {
    public static final int BARCODE_REQUEST_CODE = 1;
    public static final int BARCODE_RESULT = 1;
    public static final String BARCODE = "BARCODE";

    @Bind(R.id.btn_decode)
    Button mDecodeButton;

    @Bind(R.id.textView)
    TextView mTextView;

    @Bind(R.id.imageView)
    ImageView mImageView;

    Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        ButterKnife.bind(this);
        loadImage();
        checkForGooglePlayServices();
    }

    private void checkForGooglePlayServices() {
        final int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (status != ConnectionResult.SUCCESS) {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
        }
    }

    private void loadImage() {
        mBitmap  = BitmapFactory.decodeResource(getResources(), R.drawable.barcodesample);
        mImageView.setImageBitmap(mBitmap);
    }

    @OnClick(R.id.btn_decode)
    void decodeBitmap() {
        BarcodeDetector detector = new BarcodeDetector.Builder(getApplicationContext())
                .setBarcodeFormats(Barcode.ISBN | Barcode.EAN_13)
                .build();
        if(!detector.isOperational()){
            mTextView.setText("Error setting up the detector!");
            return;
        }
        Frame frame = new Frame.Builder().setBitmap(mBitmap).build();
        SparseArray<Barcode> barcodes = detector.detect(frame);
        mTextView.setText("");
        if (barcodes.size() == 0) {
            mTextView.setText("No code found :( ");
        }else {
            mTextView.setText(barcodes.valueAt(0).displayValue);
            setResult(RESULT_OK, new Intent().putExtra(BARCODE, mTextView.getText()));
            finish();
        }

    }

}
