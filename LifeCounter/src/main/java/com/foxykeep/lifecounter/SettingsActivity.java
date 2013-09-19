package com.foxykeep.lifecounter;

import com.foxykeep.lifecounter.sharedprefs.SharedPrefsConfig;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

public final class SettingsActivity extends Activity implements View.OnClickListener {

    private Switch mFlipCounterSwitch;
    private Switch mPoisonCountersSwitch;
    private TextView mStartingLifeTextView;

    private Switch mKeepScreenAwakeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        bindViews();

        populateViews();
    }

    private void bindViews() {
        findViewById(R.id.item_background_color).setOnClickListener(this);

        findViewById(R.id.item_flip_counter).setOnClickListener(this);
        mFlipCounterSwitch = (Switch) findViewById(R.id.item_flip_counter_switch);

        findViewById(R.id.item_poison_counters).setOnClickListener(this);
        mPoisonCountersSwitch = (Switch) findViewById(R.id.item_poison_counters_switch);

        mStartingLifeTextView = (TextView) findViewById(R.id.item_starting_life);
        mStartingLifeTextView.setOnClickListener(this);

        findViewById(R.id.item_keep_screen_awake).setOnClickListener(this);
        mKeepScreenAwakeSwitch = (Switch) findViewById(R.id.item_keep_screen_awake_switch);

        findViewById(R.id.item_credits).setOnClickListener(this);
    }

    private void populateViews() {
        mFlipCounterSwitch.setChecked(SharedPrefsConfig.getBoolean(this,
                SharedPrefsConfig.FLIP_COUNTER));

        mPoisonCountersSwitch.setChecked(SharedPrefsConfig.getBoolean(this,
                SharedPrefsConfig.SHOW_POISON_COUNTERS));

        int startingLife = SharedPrefsConfig.getInt(this, SharedPrefsConfig.STARTING_LIFE, 20);
        mStartingLifeTextView.setText(getString(R.string.settings_item_starting_life_format,
                startingLife));

        mKeepScreenAwakeSwitch.setChecked(SharedPrefsConfig.getBoolean(this,
                SharedPrefsConfig.KEEP_SCREEN_AWAKE, true));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_background_color:
                Intent intent = new Intent(this, BackgroundActivity.class);
                startActivity(intent);
                break;

            case R.id.item_flip_counter:
                mFlipCounterSwitch.toggle();
                SharedPrefsConfig.setBoolean(this, SharedPrefsConfig.FLIP_COUNTER,
                        mFlipCounterSwitch.isChecked());
                break;

            case R.id.item_poison_counters:
                mPoisonCountersSwitch.toggle();
                SharedPrefsConfig.setBoolean(this, SharedPrefsConfig.SHOW_POISON_COUNTERS,
                        mPoisonCountersSwitch.isChecked());
                break;

            case R.id.item_starting_life: {
                LayoutInflater inflater = getLayoutInflater();
                final NumberPicker numberPicker =
                        (NumberPicker) inflater.inflate(R.layout.number_picker_dialog, null);
                //noinspection ConstantConditions
                numberPicker.setMinValue(0);
                numberPicker.setMaxValue(Integer.MAX_VALUE);
                numberPicker.setValue(SharedPrefsConfig.getInt(this,
                        SharedPrefsConfig.STARTING_LIFE, 20));

                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle(R.string.number_picker_dialog_title);
                b.setView(numberPicker);
                b.setPositiveButton(R.string.number_picker_dialog_button_set,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int startingLife = numberPicker.getValue();

                                SharedPrefsConfig.setInt(SettingsActivity.this,
                                        SharedPrefsConfig.STARTING_LIFE, startingLife);
                                mStartingLifeTextView.setText(
                                        getString(R.string.settings_item_starting_life_format,
                                                startingLife));
                            }
                        });
                b.setNegativeButton(android.R.string.cancel, null);
                b.show();
                break;
            }

            case R.id.item_keep_screen_awake:
                mKeepScreenAwakeSwitch.toggle();
                SharedPrefsConfig.setBoolean(this, SharedPrefsConfig.KEEP_SCREEN_AWAKE,
                        mKeepScreenAwakeSwitch.isChecked());
                break;

            case R.id.item_credits: {
                LayoutInflater inflater = getLayoutInflater();
                View view = inflater.inflate(R.layout.credits_dialog, null);

                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle(R.string.credits_dialog_title);
                b.setView(view);
                b.setNeutralButton(R.string.credits_dialog_button_close, null);
                b.show();
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}