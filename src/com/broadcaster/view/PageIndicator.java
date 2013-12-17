package com.broadcaster.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.broadcaster.R;
import com.broadcaster.util.Util;

public class PageIndicator extends LinearLayout {
    private int mCurrentPosition = 0;

    public PageIndicator(Context context) {
        super(context);
    }

    public PageIndicator(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public void initIndicators(int size) {
        for (int i=0; i<size; i++) {
            ImageView iv = new ImageView(getContext());
            if (i == mCurrentPosition) {
                iv.setImageResource(R.drawable.page_indicator_selected);
                iv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, Util.dpToPixel(getContext(), 3), 1));
            }
            else {
                iv.setImageResource(R.drawable.page_indicator);
                iv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, Util.dpToPixel(getContext(), 3), 1));
            }
            this.addView(iv);
        }
    }

    public void setIndicator(int position) {
        ImageView iv1 = (ImageView)this.getChildAt(mCurrentPosition);
        iv1.setImageResource(R.drawable.page_indicator);
        iv1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, Util.dpToPixel(getContext(), 3), 1));

        ImageView iv2 = (ImageView)this.getChildAt(position);
        iv2.setImageResource(R.drawable.page_indicator_selected);
        iv2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, Util.dpToPixel(getContext(), 3), 1));

        mCurrentPosition = position;
    }
}
