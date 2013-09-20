package com.foxykeep.lifecounter.gesture;

import com.foxykeep.lifecounter.MainActivity;
import com.foxykeep.lifecounter.R;

import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;

public final class GestureDetector implements ViewTreeObserver.OnGlobalLayoutListener {

    private static final String TAG = "GestureDetector";

    public static final int GESTURE_DECREMENT = 0;
    public static final int GESTURE_INCREMENT = 1;

    public static final int GESTURE_ON_PLAYER_1_VIEWS = 1;
    public static final int GESTURE_ON_PLAYER_2_VIEWS = 2;

    private static final int INVALID_ID = -1;

    private static final float DELTA_RATIO = 1.5f;

    private final MainActivity mMainActivity;
    private final View mRootView;

    private final RectF mRectFPlayer1 = new RectF();
    private final RectF mRectFPlayer2 = new RectF();

    private final ViewConfiguration mViewConfiguration;

    private int mPointerId = -1;
    private int mStartingX;
    private int mStartingY;
    private boolean mStartingInPlayer1Views;
    private boolean mStartingInPlayer2Views;
    private boolean mIsATap;

    private boolean mHasViewRects;

    @SuppressWarnings("ConstantConditions")
    public GestureDetector(MainActivity mainActivity, View rootView) {
        mMainActivity = mainActivity;
        mViewConfiguration = ViewConfiguration.get(mainActivity);

        mRootView = rootView;
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    public boolean onTouchEvent(MotionEvent event) {
        Log.d("fox", "mhasviewrect " + mHasViewRects);
        if (!mHasViewRects) {
            return false;
        }

        try {
            return onTouchEvent_inner(event);
        } catch (Exception e) {
            // Do nothing. Since this is an advanced feature, we just want to be bulletproof here.
            Log.w(TAG, "Exception in the gesture listener", e);
        }

        return false;
    }

    private boolean onTouchEvent_inner(MotionEvent event) {
        Log.d("fox", "onTouchEvent " + event);
        int pointerIndex = event.getActionIndex();
        int id = event.getPointerId(pointerIndex);
        if (mPointerId == INVALID_ID) {
            mPointerId = id;
        } else if (mPointerId != id) {
            // Not the first touch event on the screen so we do nothing with it
            return false;
        }

        int action = event.getActionMasked();
        int x = (int) event.getX(id);
        int y = (int) event.getY(id);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                mStartingX = x;
                mStartingY = y;
                mStartingInPlayer1Views = false;
                mStartingInPlayer2Views = false;

                if (mRectFPlayer1.contains(x, y)) {
                    mStartingInPlayer1Views = true;
                } else if (mRectFPlayer2.contains(x, y)) {
                    mStartingInPlayer2Views = true;
                }
                mIsATap = true;
                break;
            case MotionEvent.ACTION_MOVE:
                // A tap is considered as a tap if the user hasn't moved its
                // finger over a certain threshold (found in ViewConfiguration)
                if (mIsATap) {
                    double p1 = Math.pow(mStartingX - x, 2);
                    double p2 = Math.pow(mStartingY - y, 2);
                    if (Math.sqrt(p1 + p2) > mViewConfiguration.getScaledTouchSlop()) {
                        mIsATap = false;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mIsATap) {
                    break;
                }
                if (!mStartingInPlayer1Views && !mStartingInPlayer2Views) {
                    // We didn't get a valid starting input.
                    break;
                }

                // Let's check that we are still in the right views
                if (mStartingInPlayer1Views && !mRectFPlayer1.contains(x, y)) {
                    break;
                } else if (mStartingInPlayer2Views && !mRectFPlayer2.contains(x, y)) {
                    break;
                }

                // We have a valid gesture. Let's check which one it is.
                int dx = mStartingX - x;
                int dy = mStartingY - y;

                if (Math.abs(dx) > Math.abs(dy * DELTA_RATIO)) {
                    int gestureType;
                    if (dx > 0) { // It's a left
                        gestureType = GESTURE_DECREMENT;
                    } else { // It's a right
                        gestureType = GESTURE_INCREMENT;
                    }

                    int playerImpacted = mStartingInPlayer1Views
                            ? GESTURE_ON_PLAYER_1_VIEWS : GESTURE_ON_PLAYER_2_VIEWS;
                    mMainActivity.onGestureDetected(playerImpacted, gestureType);

                    // We have a valid gesture. If the event is an ACTION_UP, let's transform it
                    // into an ACTION_CANCEL so that the buttons below are not triggered.
                    if (action == MotionEvent.ACTION_UP) {
                        event.setAction(MotionEvent.ACTION_CANCEL);
                    }
                }
        }

        return false;
    }

    @Override
    public void onGlobalLayout() {
        int[] location = new int[2];

        View player1LifeContainer = mRootView.findViewById(R.id.player1_life_container);
        View player1PoisonContainer = mRootView.findViewById(R.id.player1_poison_container);

        player1LifeContainer.getLocationOnScreen(location);
        mRectFPlayer1.left = location[0];
        mRectFPlayer1.right = location[0] + player1LifeContainer.getWidth()
                + player1PoisonContainer.getWidth();
        mRectFPlayer1.top = location[1];
        mRectFPlayer1.bottom = location[1] + player1LifeContainer.getHeight();

        View player2Container = mRootView.findViewById(R.id.player2_life_container);
        View player2PoisonContainer = mRootView.findViewById(R.id.player2_poison_container);

        player2Container.getLocationOnScreen(location);
        mRectFPlayer2.left = location[0];
        mRectFPlayer2.right = location[0] + player2Container.getWidth()
                + player2PoisonContainer.getWidth();
        mRectFPlayer2.top = location[1];
        mRectFPlayer2.bottom = location[1] + player2Container.getHeight();

        mHasViewRects = true;
    }
}
