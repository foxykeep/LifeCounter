package com.foxykeep.lifecounter;

import com.foxykeep.lifecounter.data.Background;
import com.foxykeep.lifecounter.sharedprefs.SharedPrefsConfig;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class BackgroundActivity extends Activity implements AdapterView.OnItemClickListener {

    private ImageView mBackgroundView;

    private BackgroundAdapter mBackgroundAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background);

        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        bindViews();

        populateAdapter();
        Background.loadSavedBackgroundOnView(mBackgroundView);
    }

    private void bindViews() {
        mBackgroundView = (ImageView) findViewById(R.id.background);

        mBackgroundAdapter = new BackgroundAdapter(this);
        GridView gridview = (GridView) findViewById(R.id.grid_view);
        gridview.setAdapter(mBackgroundAdapter);
        gridview.setOnItemClickListener(this);
    }

    private void populateAdapter() {
        mBackgroundAdapter.addAll(Background.getBackgrounds());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Background background = mBackgroundAdapter.getItem(position);
        SharedPrefsConfig.setInt(this, SharedPrefsConfig.BACKGROUND_ID, background.id);
        Background.loadSavedBackgroundOnView(mBackgroundView);
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

    private static final class ViewHolder {
        private final ImageView mImageView;

        public ViewHolder(View view) {
            mImageView = (ImageView) view.findViewById(R.id.image_view);
        }

        public void populateViews(Context context, Background background) {
            mImageView.setImageDrawable(background.getDrawable(context));
        }
    }

    private static final class BackgroundAdapter extends ArrayAdapter<Background> {

        private Context mContext;
        private LayoutInflater mInflater;

        public BackgroundAdapter(Context context) {
            super(context, 0);
            mContext = context;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.background_item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.populateViews(mContext, getItem(position));

            return convertView;
        }
    }
}
