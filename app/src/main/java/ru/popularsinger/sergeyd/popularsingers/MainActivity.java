package ru.popularsinger.sergeyd.popularsingers;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import ru.popularsinger.sergeyd.popularsingers.Database.dbCursorLoader;
import ru.popularsinger.sergeyd.popularsingers.Database.dbTaskLoader;

public class MainActivity extends ActionBarActivity
    implements ListItemsFragment.onItemClickListener, LoaderManager.LoaderCallbacks
{
    public final int CURSOR_LOADER_IND = 0;
    public final int DB_LOADER_IND = 1;

    long m_currentIndex = 0;
    boolean m_showDetails = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // заголовок activiy
        setTitle(R.string.main_activity_name);

        // прописываем настройки по умолчанию для ImageLoader-а
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);

        // создаем Loader для базы данных
        getSupportLoaderManager().initLoader(DB_LOADER_IND, null, this);
        getSupportLoaderManager().initLoader(CURSOR_LOADER_IND, null, this);

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
        getSupportLoaderManager().restartLoader(DB_LOADER_IND, null, this);
        return super.onOptionsItemSelected(item);
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

    @Override
    // создание Loader'a
    public Loader onCreateLoader(int id, Bundle args)
    {
        Loader loader = null;
        switch ( id )
        {
            case CURSOR_LOADER_IND: // Loader загрузки курсора
                loader = new dbCursorLoader(this);
                break;
            case DB_LOADER_IND: // Loader загрузки BD
                loader = new dbTaskLoader(this);

                // показываем ProgressBar
                findViewById(R.id.pbLoading).setVisibility(View.VISIBLE);

                break;
        }

        // создаем наш класс загрузки курсора
        return loader;
    }

    @Override
    // окночание загрузки
    public void onLoadFinished(Loader loader, Object data)
    {
        switch ( loader.getId() )
        {
            case CURSOR_LOADER_IND: // Loader загрузки курсора
                // подменям курор в адапетер
                ListItemsFragment listFragment = (ListItemsFragment) getSupportFragmentManager().findFragmentById(R.id.main_list_fragment);
                listFragment.reloadCursor((Cursor) data);
                break;
            case DB_LOADER_IND: // Loader загрузки BD

                int result = (int)data;
                if ( result != dbTaskLoader.ERROR_NO_ERROR )
                {
                    int message;
                    switch ( result )
                    {
                        case dbTaskLoader.ERROR_URL:
                        case dbTaskLoader.ERROR_CONNECTION:
                        case dbTaskLoader.ERROR_UNKNOWN:
                            message = R.string.download_error_connect;
                            break;
                        default:
                            message = R.string.download_error_parse;
                            break;
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    // сообщение
                    builder.setTitle(message);
                    // иконка
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                    // кнопка
                    builder.setPositiveButton(R.string.button_ok, null);
                    // создаем диалог
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                getSupportLoaderManager().getLoader(CURSOR_LOADER_IND).forceLoad();

                // скрываем ProgressBar
                findViewById(R.id.pbLoading).setVisibility(View.GONE);

                break;
        }
    }

    @Override
    // уничтожение Loader'a
    public void onLoaderReset(Loader loader)
    {

    }
}
