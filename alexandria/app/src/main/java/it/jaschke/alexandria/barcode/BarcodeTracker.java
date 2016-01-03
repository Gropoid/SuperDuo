package it.jaschke.alexandria.barcode;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

public class BarcodeTracker extends Tracker<Barcode> {

    BarcodeInterpreter mBarcodeInterpreter;

    public BarcodeTracker(BarcodeInterpreter mBarcodeInterpreter) {
        this.mBarcodeInterpreter = mBarcodeInterpreter;
    }

    @Override
    public void onNewItem(int id, Barcode item) {
        super.onNewItem(id, item);
        mBarcodeInterpreter.onBarcodeDecoded(item);
    }
}
