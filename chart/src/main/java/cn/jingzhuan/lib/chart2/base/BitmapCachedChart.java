package cn.jingzhuan.lib.chart2.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import cn.jingzhuan.lib.chart.Viewport;;
import java.lang.ref.WeakReference;

public abstract class BitmapCachedChart extends View {

    protected WeakReference<Bitmap> mDrawBitmap;
    protected Canvas mBitmapCanvas;
    protected Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;

    /**
     * The current viewport. This rectangle represents the currently visible lib domain
     * and range. The currently visible lib X values are from this rectangle's left to its right.
     * The currently visible lib Y values are from this rectangle's top to its bottom.
     * <p>
     * Note that this rectangle's top is actually the smaller Y value, and its bottom is the larger
     * Y value. Since the lib is drawn onscreen in such a way that lib Y values increase
     * towards the top of the screen (decreasing pixel Y positions), this rectangle's "top" is drawn
     * above this rectangle's "bottom" value.
     *
     * @see #mContentRect
     */
    protected Viewport mCurrentViewport = new Viewport();

    /**
     * The current destination rectangle (in pixel coordinates) into which the lib data should
     * be drawn. Chart labels are drawn outside this area.
     *
     * @see #mCurrentViewport
     */
    protected Rect mContentRect = new Rect();

    public BitmapCachedChart(Context context) {
        super(context);
    }

    public BitmapCachedChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BitmapCachedChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BitmapCachedChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public Rect getContentRect() {
        return mContentRect;
    }

    public Viewport getCurrentViewport() {
        return mCurrentViewport;
    }

    public Canvas getBitmapCanvas() {
        return mBitmapCanvas;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawAxis(canvas); // 坐标轴刻度在最底层

        // Clips the next few drawing operations to the content area
        int clipRestoreCount = canvas.save();

        canvas.clipRect(mContentRect);
        createBitmapCache(canvas);

        if (getBitmapCanvas() != null) {
          drawGridLine(getBitmapCanvas());

          render(getBitmapCanvas());

          canvas.drawBitmap(getDrawBitmap(), 0, 0, getRenderPaint());
        }

        // Removes clipping rectangle
        canvas.restoreToCount(clipRestoreCount);

        drawLabels(canvas); // 坐标轴刻度在最上层
    }

    protected void createBitmapCache(Canvas canvas) {
        int width = getContentRect().width() + getContentRect().left;
        int height = getContentRect().height();

        if (mDrawBitmap == null
                || (mDrawBitmap.get().getWidth() != width)
                || (mDrawBitmap.get().getHeight() != height)) {

            if (width > 0 && height > 0) {
                mDrawBitmap = new WeakReference<>(Bitmap.createBitmap(width, height, mBitmapConfig));
                mBitmapCanvas = new Canvas(mDrawBitmap.get());
            } else
                return;
        }

        mDrawBitmap.get().eraseColor(Color.TRANSPARENT);
    }

    protected Bitmap getDrawBitmap() {
        return mDrawBitmap.get();
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (mBitmapCanvas != null) {
            mBitmapCanvas.setBitmap(null);
            mBitmapCanvas = null;
        }
        if (mDrawBitmap != null) {
            if (mDrawBitmap.get() != null) mDrawBitmap.get().recycle();
            mDrawBitmap.clear();
            mDrawBitmap = null;
        }
    }

    public void releaseBitmap() {
        if (mBitmapCanvas != null) {
            mBitmapCanvas.setBitmap(null);
            mBitmapCanvas = null;
        }
        if (mDrawBitmap != null) {
            if (mDrawBitmap.get() != null) mDrawBitmap.get().recycle();
            mDrawBitmap.clear();
            mDrawBitmap = null;
        }
    }

    protected abstract void drawAxis(Canvas canvas);
    protected abstract void drawGridLine(Canvas canvas);
    protected abstract void render(Canvas canvas);
    protected abstract Paint getRenderPaint();
    protected abstract void drawLabels(Canvas canvas);

}
