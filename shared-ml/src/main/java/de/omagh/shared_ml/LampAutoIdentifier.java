package de.omagh.shared_ml;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;

import java.util.List;

import timber.log.Timber;

/**
 * High level helper that tries to identify lamps using the on-device
 * {@link LampIdentifier} classifier and falls back to barcode scanning when
 * classification is inconclusive.
 */
public class LampAutoIdentifier implements AutoCloseable {
    private static final String UNKNOWN = "Unknown";

    private final LampIdentifier classifier;
    private final BarcodeScanner barcodeScanner;

    public LampAutoIdentifier(Context context) {
        this.classifier = new LampIdentifier(
                context,
                new AssetModelProvider(MlConfig.LAMP_MODEL));
        this.barcodeScanner = BarcodeScanning.getClient();
    }

    /**
     * Attempts to identify the lamp shown in {@code bitmap}. The method first
     * performs image classification. If no prediction above the model's
     * threshold is produced, barcode scanning is tried as a fallback.
     *
     * @return LiveData emitting either the predicted label, a barcode value or
     * {@value #UNKNOWN} if nothing could be resolved.
     */
    public LiveData<String> identifyLamp(Bitmap bitmap) {
        MutableLiveData<String> result = new MutableLiveData<>();
        LiveData<IdentifierResult<List<LampIdentifier.Prediction>>> live = classifier.identifyLamp(bitmap);
        Observer<IdentifierResult<List<LampIdentifier.Prediction>>> observer = new Observer<>() {
            @Override
            public void onChanged(IdentifierResult<List<LampIdentifier.Prediction>> res) {
                live.removeObserver(this);
                if (res instanceof IdentifierResult.Success) {
                    List<LampIdentifier.Prediction> preds =
                            ((IdentifierResult.Success<List<LampIdentifier.Prediction>>) res).getValue();
                    if (preds != null && !preds.isEmpty() &&
                            preds.get(0).getConfidence() >= classifier.getThreshold()) {
                        result.postValue(preds.get(0).getLabel());
                    } else {
                        scanBarcode(bitmap, result);
                    }
                } else {
                    scanBarcode(bitmap, result);
                }
            }
        };
        live.observeForever(observer);
        return result;
    }

    private void scanBarcode(Bitmap bitmap, MutableLiveData<String> result) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        barcodeScanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (barcodes != null && !barcodes.isEmpty()) {
                        result.postValue(barcodes.get(0).getRawValue());
                    } else {
                        result.postValue(UNKNOWN);
                    }
                })
                .addOnFailureListener(e -> {
                    Timber.e(e, "Barcode scan failed");
                    result.postValue(UNKNOWN);
                });
    }

    @Override
    public void close() {
        classifier.close();
        barcodeScanner.close();
    }
}
