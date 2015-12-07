package nest.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import nest.R;

public class HomeView extends ImageView {

    private final Paint paint = new Paint();
    private final Paint strokePaint = new Paint();

    public HomeView(Context context) {
        super(context);
        init();
    }

    public HomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HomeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HomeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @SuppressWarnings("deprecation") // min api is 16
    private void init() {
        Resources res = getContext().getResources();

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(res.getColor(R.color.transparent_home_view_bg));

        strokePaint.setAntiAlias(true);
        strokePaint.setStrokeWidth(res.getDimensionPixelSize(R.dimen.home_view_state_stroke_width));
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(res.getColor(R.color.green));
    }

    public void setColor(int color) {
        strokePaint.setColor(color);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float position = getWidth() / 2f;
        canvas.drawCircle(position, position, position, paint);
        canvas.drawCircle(position, position, position * 0.9f , strokePaint);
        super.onDraw(canvas);
    }
}
