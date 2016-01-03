package it.jaschke.alexandria.barcode;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {

    private BarcodeInterpreter mBarcodeInterpreter;

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        return new BarcodeTracker(mBarcodeInterpreter);
    }

    public void setBarcodeInterpreter(BarcodeInterpreter barcodeInterpreter) {
        mBarcodeInterpreter = barcodeInterpreter;
    }

}
