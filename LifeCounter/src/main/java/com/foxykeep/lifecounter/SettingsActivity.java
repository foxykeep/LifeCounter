package com.foxykeep.lifecounter;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

public final class SettingsActivity extends Activity implements View.OnClickListener {

    private Switch mFlipCounterSwitch;
    private Switch mPoisonCountersSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        bindViews();
    }

    private void bindViews() {
        findViewById(R.id.item_background_color).setOnClickListener(this);

        findViewById(R.id.item_flip_counter).setOnClickListener(this);
        mFlipCounterSwitch = (Switch) findViewById(R.id.item_flip_counter_switch);
        findViewById(R.id.item_poison_counters).setOnClickListener(this);
        mPoisonCountersSwitch = (Switch) findViewById(R.id.item_poison_counters_switch);
        findViewById(R.id.item_starting_life).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.item_background_color:
                break;
            case R.id.item_flip_counter:
                mFlipCounterSwitch.toggle();
                break;
            case R.id.item_poison_counters:
                mPoisonCountersSwitch.toggle();
                break;
            case R.id.item_starting_life:
                break;
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
