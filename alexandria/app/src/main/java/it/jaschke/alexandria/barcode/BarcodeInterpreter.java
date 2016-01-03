package it.jaschke.alexandria.barcode;

import com.google.android.gms.vision.barcode.Barcode;

public interface BarcodeInterpreter {
    void onBarcodeDecoded(Barcode barcode);
}
