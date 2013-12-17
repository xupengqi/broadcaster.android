package com.broadcaster.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.ListView;

import com.broadcaster.util.Constants;
import com.broadcaster.util.Util;

public class ListViewWithOverScroll extends ListView {
    public boolean interceptOnTouch = true;
    
    private OnOverScrollActionListener mOnOverScrollAction = null;
    private boolean endOfY = false;
    private boolean started = false;
    private int startY = -1;
    private int maxDistance = 0;

    public ListViewWithOverScroll(Context context) {
        super(context);
        init();
    }

    public ListViewWithOverScroll(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }
    public ListViewWithOverScroll(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        maxDistance = (int) (Util.getWindowHeight(this.getContext()) / 2.5);
        this.setOnScrollListener(new ListViewOSOnScroll());
    }

    public void resetCounters() {
        started = false;
        endOfY = false;
        startY = -1;
    }

    @Override
    protected void onOverScrolled (int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        endOfY = clampedY;  
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) { 
        if(!interceptOnTouch) {
            return super.onTouchEvent(event);
        }

        int y = (int)event.getY();
        int action = MotionEventCompat.getActionMasked(event);
        switch(action) {
        case (MotionEvent.ACTION_MOVE) :
            if (startY < 0) {
                startY = y;
            }
            
            if (y > startY && endOfY) {
                if (!started) {
                    started = true;
                    mOnOverScrollAction.onOverScrollStart();
                }
                else if ((y - startY) > maxDistance) {
                    mOnOverScrollAction.onOverScrollReload();
                }
                else {
                    mOnOverScrollAction.onOverScrollConfirm((double)(y - startY) / maxDistance);
                }
            }
        break;
        case (MotionEvent.ACTION_UP) :
            resetCounters();
            mOnOverScrollAction.onOverScrollCancel();
        break;
        }
        
        return super.onTouchEvent(event);
    }
    
    public void disableOnTouch() {
        interceptOnTouch = false;
    }
    
    public void enableOnTouch() {
        interceptOnTouch = true;
        resetCounters();
    }

    public void setOnOverScrollActionListener(OnOverScrollActionListener listener) {
        mOnOverScrollAction = listener;
    }

    public interface OnOverScrollActionListener {
        public void onOverScrollStart();
        public void onOverScrollCancel();
        public void onOverScrollConfirm(double d);
        public void onOverScrollReload();
        public void onOverScrollLoadMore();
    }

    public class ListViewOSOnScroll implements OnScrollListener {
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            boolean loadMore = (totalItemCount >= Constants.POST_PAGE_SIZE) && (firstVisibleItem + visibleItemCount >= totalItemCount);
            if(loadMore) {
                mOnOverScrollAction.onOverScrollLoadMore();
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) { }
    }
}
