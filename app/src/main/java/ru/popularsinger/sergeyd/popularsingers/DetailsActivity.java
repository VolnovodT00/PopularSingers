package ru.popularsinger.sergeyd.popularsingers;

import android.content.res.Configuration;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class DetailsActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
        {
            finish();
            return;
        }

        if (savedInstanceState == null)
        {
            DetailsFragment details = DetailsFragment.newInstance(getIntent().getLongExtra("index", 0));
            getSupportFragmentManager().beginTransaction().add(R.id.details_fragment, details).commit();
        }

        // добавляем кнопку назад
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch ( item.getItemId() )
        {
            // обработка кнопки назад
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
