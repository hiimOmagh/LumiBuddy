package de.omagh.core_infra.ar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Simple view that draws a heatmap grid to visualize light intensity.
 * Each cell color ranges from blue (low) to red (high).
 */
public class HeatmapOverlayView extends View {
    private final Paint paint = new Paint();
    private float[][] intensityGrid;

    public HeatmapOverlayView(Context context) {
        super(context);
        init();
    }

    public HeatmapOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeatmapOverlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.FILL);
        setAlpha(0.7f);
    }

    /**
     * Update the heatmap data and redraw.
     */
    public void setIntensityGrid(float[][] grid) {
        this.intensityGrid = grid;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (intensityGrid == null) return;
        int rows = intensityGrid.length;
        int cols = intensityGrid[0].length;
        int cellW = getWidth() / cols;
        int cellH = getHeight() / rows;

        float max = 0f;
        for (float[] row : intensityGrid) {
            for (float v : row) {
                if (v > max) max = v;
            }
        }
        if (max <= 0f) max = 1f;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                float val = intensityGrid[r][c];
                float ratio = val / max;
                int red = (int) (ratio * 255);
                int blue = 255 - red;
                paint.setColor(Color.argb(180, red, 0, blue));
                int left = c * cellW;
                int top = r * cellH;
                canvas.drawRect(left, top, left + cellW, top + cellH, paint);
            }
        }
    }
}
