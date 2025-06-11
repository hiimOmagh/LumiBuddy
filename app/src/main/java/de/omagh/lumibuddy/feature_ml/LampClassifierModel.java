package de.omagh.lumibuddy.feature_ml;

import android.graphics.Bitmap;

/**
 * Interface for grow light or lamp classifiers.
 */
public interface LampClassifierModel {
    void loadModel();

    void analyzeImage(Bitmap input);

    String getResult();
}