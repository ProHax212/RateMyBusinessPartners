package seniordesign.ratemybusinesspartners.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import seniordesign.ratemybusinesspartners.R;

/**
 * TODO: document your custom view class.
 */
public class LikeButton extends View {
    private Drawable mDrawable = Drawable.createFromPath("R.mipmap.like_button");

    public LikeButton(Context context) {
        super(context);
        init(null, 0);
    }

    public LikeButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public LikeButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.LikeButton, defStyle, 0);

        if (a.hasValue(R.styleable.LikeButton_drawable)) {
            mDrawable = a.getDrawable(
                    R.styleable.LikeButton_drawable);
            mDrawable.setCallback(this);
        }

        a.recycle();

        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;

        // Draw the example drawable on top of the text.
        if (mDrawable != null) {
            mDrawable.setBounds(paddingLeft, paddingTop,
                    paddingLeft + contentWidth, paddingTop + contentHeight);
            mDrawable.draw(canvas);
        }
    }

    /**
     * Gets the example drawable attribute value.
     *
     * @return The example drawable attribute value.
     */
    public Drawable getDrawable() {
        return mDrawable;
    }

    /**
     * Sets the view's example drawable attribute value. In the example view, this drawable is
     * drawn above the text.
     *
     * @param exampleDrawable The example drawable attribute value to use.
     */
    public void setmDrawable(Drawable exampleDrawable) {

        mDrawable = exampleDrawable;

        invalidate();
        requestLayout();

    }
}
