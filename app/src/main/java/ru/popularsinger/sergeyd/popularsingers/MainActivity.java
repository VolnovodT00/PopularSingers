package ru.popularsinger.sergeyd.popularsingers;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends ActionBarActivity
    implements SingersFragment.onItemClickListener
{
    long m_currentIndex = 0;
    boolean m_showDetails = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // заголовок activiy
        setTitle(R.string.main_activity_name);
        Log.d("popularsingers", "main: settitle");

        // прописываем настройки по умолчанию для ImageLoader-а
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);

        if ( savedInstanceState != null )
            m_currentIndex = savedInstanceState.getLong("index");

        m_showDetails = (findViewById(R.id.main_details_fragment) != null);
        if (m_showDetails)
            itemClick(m_currentIndex);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("index", m_currentIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add("Обновиь список исполнителей");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return false;
    }

    @Override
    public void itemClick(long id)
    {
        // сохраняем id
        m_currentIndex = id;

        if (m_showDetails)
        { // если нужно показывать информацю об исполнителе в этом же окне
            DetailsFragment details = (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.main_details_fragment);
            if (details == null || details.getIndex() != m_currentIndex)
            {
                details = DetailsFragment.newInstance(m_currentIndex);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_details_fragment, details).commit();
            }
        }
        else
        { // если нужно показывать информацю об исполнителе в другом окне
            startActivity(new Intent(this, DetailsActivity.class).putExtra("index", m_currentIndex));
        }
    }
}
