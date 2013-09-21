package com.foxykeep.lifecounter;

import com.foxykeep.lifecounter.data.Background;
import com.foxykeep.lifecounter.gesture.GestureDetector;
import com.foxykeep.lifecounter.sharedprefs.SharedPrefsConfig;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String SAVED_STATE_PLAYER_1_LIFE = "savedStatePlayer1Life";
    private static final String SAVED_STATE_PLAYER_1_POISON = "savedStatePlayer1Poison";
    private static final String SAVED_STATE_PLAYER_2_LIFE = "savedStatePlayer2Life";
    private static final String SAVED_STATE_PLAYER_2_POISON = "savedStatePlayer2Poison";

    // Background view
    private ImageView mBackgroundView;

    // Player 1 views
    private TextView mPlayer1LifeView;
    private View mPlayer1LifeIncrementView;
    private View mPlayer1LifeDecrementView;
    private View mPlayer1LifeIncrementFlippedView;
    private View mPlayer1LifeDecrementFlippedView;
    private View mPlayer1PoisonContainer;
    private View mPlayer1PoisonAddView;
    private View mPlayer1PoisonRemoveView;
    private TextView mPlayer1PoisonView;

    // Player 2 views
    private TextView mPlayer2LifeView;
    private View mPlayer2LifeIncrementView;
    private View mPlayer2LifeDecrementView;
    private View mPlayer2PoisonContainer;
    private View mPlayer2PoisonRemoveView;
    private TextView mPlayer2PoisonView;

    // Settings
    private boolean mFlipCounter;
    private boolean mShowPoisonCounters;
    private int mStartingLife;

    // Counters
    private int mPlayer1Life;
    private int mPlayer1Poison;
    private int mPlayer2Life;
    private int mPlayer2Poison;

    // GestureDetector
    private GestureDetector mGestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bindViews();

        if (savedInstanceState != null) {
            mPlayer1Life = savedInstanceState.getInt(SAVED_STATE_PLAYER_1_LIFE);
            mPlayer1Poison = savedInstanceState.getInt(SAVED_STATE_PLAYER_1_POISON);
            mPlayer2Life = savedInstanceState.getInt(SAVED_STATE_PLAYER_2_LIFE);
            mPlayer2Poison = savedInstanceState.getInt(SAVED_STATE_PLAYER_2_POISON);
        } else {
            // We need to load it once here to be able to use it in resetLife() even if we are
            // loading it again in onStart()
            mStartingLife = SharedPrefsConfig.getInt(this, SharedPrefsConfig.STARTING_LIFE, 20);
            resetLife();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mFlipCounter = SharedPrefsConfig.getBoolean(this, SharedPrefsConfig.FLIP_COUNTER);
        mShowPoisonCounters = SharedPrefsConfig.getBoolean(this,
                SharedPrefsConfig.SHOW_POISON_COUNTERS);
        mStartingLife = SharedPrefsConfig.getInt(this, SharedPrefsConfig.STARTING_LIFE, 20);

        populateViews();

        boolean keepScreenAwake = SharedPrefsConfig.getBoolean(this,
                SharedPrefsConfig.KEEP_SCREEN_AWAKE, true);
        if (keepScreenAwake) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    @SuppressWarnings("NullableProblems")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(SAVED_STATE_PLAYER_1_LIFE, mPlayer1Life);
        outState.putInt(SAVED_STATE_PLAYER_1_POISON, mPlayer1Poison);
        outState.putInt(SAVED_STATE_PLAYER_2_LIFE, mPlayer2Life);
        outState.putInt(SAVED_STATE_PLAYER_2_POISON, mPlayer2Poison);
    }

    private void bindViews() {
        mBackgroundView = (ImageView) findViewById(R.id.background);

        // Player 1 views
        findViewById(R.id.player1_life_add).setOnClickListener(this);
        findViewById(R.id.player1_life_remove).setOnClickListener(this);
        mPlayer1LifeView = (TextView) findViewById(R.id.player1_life);
        mPlayer1LifeIncrementView = findViewById(R.id.player1_life_increment);
        mPlayer1LifeDecrementView = findViewById(R.id.player1_life_decrement);
        mPlayer1LifeIncrementFlippedView = findViewById(R.id.player1_life_increment_flipped);
        mPlayer1LifeDecrementFlippedView = findViewById(R.id.player1_life_decrement_flipped);

        mPlayer1PoisonContainer = findViewById(R.id.player1_poison_container);
        mPlayer1PoisonAddView = findViewById(R.id.player1_poison_add);
        mPlayer1PoisonAddView.setOnClickListener(this);
        mPlayer1PoisonRemoveView = findViewById(R.id.player1_poison_remove);
        mPlayer1PoisonRemoveView.setOnClickListener(this);
        mPlayer1PoisonView = (TextView) findViewById(R.id.player1_poison);

        // Reset view
        findViewById(R.id.reset).setOnClickListener(this);

        // Player 2 views
        findViewById(R.id.player2_life_add).setOnClickListener(this);
        findViewById(R.id.player2_life_remove).setOnClickListener(this);
        mPlayer2LifeView = (TextView) findViewById(R.id.player2_life);
        mPlayer2LifeIncrementView = findViewById(R.id.player2_life_increment);
        mPlayer2LifeDecrementView = findViewById(R.id.player2_life_decrement);

        mPlayer2PoisonContainer = findViewById(R.id.player2_poison_container);
        findViewById(R.id.player2_poison_add).setOnClickListener(this);
        mPlayer2PoisonRemoveView = findViewById(R.id.player2_poison_remove);
        mPlayer2PoisonRemoveView.setOnClickListener(this);
        mPlayer2PoisonView = (TextView) findViewById(R.id.player2_poison);

        mGestureDetector = new GestureDetector(this, findViewById(R.id.root_container));
    }

    private void populateViews() {
        Background.loadSavedBackgroundOnView(mBackgroundView);

        mPlayer1LifeView.setRotation(mFlipCounter ? 180f : 0f);
        mPlayer1LifeView.setText(String.valueOf(mPlayer1Life));
        mPlayer2LifeView.setText(String.valueOf(mPlayer2Life));

        if (mShowPoisonCounters) {
            mPlayer1PoisonContainer.setVisibility(View.VISIBLE);
            mPlayer1PoisonView.setVisibility(View.VISIBLE);
            mPlayer1PoisonView.setText(String.valueOf(mPlayer1Poison));
            int poisonIconResId = mPlayer1Poison > 0
                    ? R.drawable.ic_poison_normal : R.drawable.ic_poison_disabled;
            mPlayer1PoisonView.setCompoundDrawablesWithIntrinsicBounds(0 /* left */, 0 /* top */,
                    poisonIconResId /* right*/, 0 /* bottom */);

            if (mFlipCounter) {
                mPlayer1PoisonView.setRotation(180f);

                if (mPlayer1Poison == 0) {
                    mPlayer1PoisonAddView.setVisibility(View.INVISIBLE);
                    mPlayer1PoisonAddView.setEnabled(false);
                } else {
                    mPlayer1PoisonAddView.setVisibility(View.VISIBLE);
                    mPlayer1PoisonAddView.setEnabled(true);
                }

                // Reset the other view (in case we just flipped the counters)
                mPlayer1PoisonRemoveView.setVisibility(View.VISIBLE);
                mPlayer1PoisonRemoveView.setEnabled(true);
            } else {
                mPlayer1PoisonView.setRotation(0f);

                // Reset the other view (in case we just flipped the counters)
                mPlayer1PoisonAddView.setVisibility(View.VISIBLE);
                mPlayer1PoisonAddView.setEnabled(true);

                if (mPlayer1Poison == 0) {
                    mPlayer1PoisonRemoveView.setVisibility(View.INVISIBLE);
                    mPlayer1PoisonRemoveView.setEnabled(false);
                } else {
                    mPlayer1PoisonRemoveView.setVisibility(View.VISIBLE);
                    mPlayer1PoisonRemoveView.setEnabled(true);
                }
            }

            mPlayer2PoisonContainer.setVisibility(View.VISIBLE);
            mPlayer2PoisonView.setVisibility(View.VISIBLE);
            mPlayer2PoisonView.setText(String.valueOf(mPlayer2Poison));
            poisonIconResId = mPlayer2Poison > 0
                    ? R.drawable.ic_poison_normal : R.drawable.ic_poison_disabled;
            mPlayer2PoisonView.setCompoundDrawablesWithIntrinsicBounds(0 /* left */, 0 /* top */,
                    poisonIconResId /* right*/, 0 /* bottom */);

            if (mPlayer2Poison == 0) {
                mPlayer2PoisonRemoveView.setVisibility(View.INVISIBLE);
                mPlayer2PoisonRemoveView.setEnabled(false);
            } else {
                mPlayer2PoisonRemoveView.setVisibility(View.VISIBLE);
                mPlayer2PoisonRemoveView.setEnabled(true);
            }
        } else {
            mPlayer1PoisonContainer.setVisibility(View.GONE);
            mPlayer1PoisonView.setVisibility(View.GONE);

            mPlayer2PoisonContainer.setVisibility(View.GONE);
            mPlayer2PoisonView.setVisibility(View.GONE);
        }
    }

    private void resetLife() {
        mPlayer1Life = mStartingLife;
        mPlayer2Life = mStartingLife;
        mPlayer1Poison = 0;
        mPlayer2Poison = 0;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.player1_life_add:
                if (mFlipCounter) {
                    mPlayer1Life--;
                } else {
                    mPlayer1Life++;
                }
                break;
            case R.id.player1_life_remove:
                if (mFlipCounter) {
                    mPlayer1Life++;
                } else {
                    mPlayer1Life--;
                }
                break;
            case R.id.player1_poison_add:
                if (mFlipCounter) {
                    mPlayer1Poison--;
                } else {
                    mPlayer1Poison++;
                }
                break;
            case R.id.player1_poison_remove:
                if (mFlipCounter) {
                    mPlayer1Poison++;
                } else {
                    mPlayer1Poison--;
                }
                break;
            case R.id.reset:
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle(R.string.main_reset_dialog_title);
                b.setMessage(R.string.main_reset_dialog_message);
                b.setIcon(android.R.drawable.ic_dialog_alert);
                b.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        resetLife();
                        populateViews();
                    }
                });
                b.setNegativeButton(android.R.string.no, null);
                b.show();
                break;
            case R.id.player2_life_add:
                mPlayer2Life++;
                break;
            case R.id.player2_life_remove:
                mPlayer2Life--;
                break;
            case R.id.player2_poison_add:
                mPlayer2Poison++;
                break;
            case R.id.player2_poison_remove:
                mPlayer2Poison--;
                break;
        }

        populateViews();
    }

    @SuppressWarnings("ConstantConditions")
    public void onGestureDetected(int playerImpacted, int gestureType) {
        if (playerImpacted == GestureDetector.GESTURE_ON_PLAYER_1_VIEWS) {
            // Player 1 views
            if (gestureType == GestureDetector.GESTURE_DECREMENT) {
                if (mFlipCounter) {
                    mPlayer1Life += 5;
                    showAndAnimateIncrementView(mPlayer1LifeIncrementFlippedView,
                            R.anim.increment_flipped);
                } else {
                    mPlayer1Life -= 5;
                    showAndAnimateIncrementView(mPlayer1LifeDecrementView, R.anim.decrement);
                }
            } else {
                if (mFlipCounter) {
                    mPlayer1Life -= 5;
                    showAndAnimateIncrementView(mPlayer1LifeDecrementFlippedView,
                            R.anim.decrement_flipped);
                } else {
                    mPlayer1Life += 5;
                    showAndAnimateIncrementView(mPlayer1LifeIncrementView, R.anim.increment);
                }
            }
        } else {
            // Player 2 views
            if (gestureType == GestureDetector.GESTURE_DECREMENT) {
                mPlayer2Life -= 5;
                showAndAnimateIncrementView(mPlayer2LifeDecrementView, R.anim.decrement);
            } else {
                mPlayer2Life += 5;
                showAndAnimateIncrementView(mPlayer2LifeIncrementView, R.anim.increment);
            }
        }

        populateViews();
    }

    @SuppressWarnings("ConstantConditions")
    private void showAndAnimateIncrementView(final View incrementView, int animResId) {
        incrementView.setVisibility(View.VISIBLE);

        Animation animation = AnimationUtils.loadAnimation(this, animResId);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                incrementView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        incrementView.startAnimation(animation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
