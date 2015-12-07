package nest.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import nest.R;

public class ParallaxView extends ImageView {

    private final Paint paint = new Paint();

    private Bitmap image;
    private Rect srcRect;
    private RectF destRect;
    private float offset = 0;
    private long delay = System.currentTimeMillis();
    private int speed;

    public ParallaxView(Context context) {
        super(context);
        init();
    }

    public ParallaxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ParallaxView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ParallaxView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        paint.setAntiAlias(true);
        image = ((BitmapDrawable) getDrawable()).getBitmap();
        srcRect = new Rect(0, 0, image.getWidth(), image.getHeight());
        destRect = new RectF(0, 0, 0, 0);
        speed = getContext().getResources().getDimensionPixelSize(R.dimen.parallax_header_speed);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        long diff = System.currentTimeMillis() - delay;
        delay = System.currentTimeMillis();

        offset += diff / 1000f * speed;
        offset %= image.getWidth();

        float x = offset - image.getWidth();

        while (x < getWidth()) {
            destRect.set(x, 0, x + image.getWidth(), image.getHeight());
            canvas.drawBitmap(image, srcRect, destRect, paint);
            x += image.getWidth();
        }

        postInvalidate();
    }
}
