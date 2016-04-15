package ru.popularsinger.sergeyd.popularsingers;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends ActionBarActivity
        implements DownloadSingers.DownloadSingersListener, LoaderManager.LoaderCallbacks<Cursor>
{
    private final String URL_ARTISTS_JSON = "http://download.cdn.yandex.net/mobilization-2016/artists.json";

    private ProgressDialog m_progressDialog;
    private SingersDatabase m_database;
    private DownloadSingers m_downloader;
    private SingersAdapter m_adapter;

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
/*
        // создаем экземпляр класса базы данных и подключаемся к ней
        m_database = new SingersDatabase(this);

        // создаем экземпляр класса скачивания списка исполнителей и назначаем слушателя
        m_downloader = new DownloadSingers(m_database, this);
        m_downloader.execute(URL_ARTISTS_JSON);

        // создаем адаптер
        m_adapter = new SingersAdapter(MainActivity.this, null);
        // находим список
        ListView lstSingers = (ListView) findViewById(R.id.lstSingers);
        // назначаем адаптер списку
        lstSingers.setAdapter(m_adapter);
        // добавляем View пустого списка
        lstSingers.setEmptyView(findViewById(R.id.lblEmptyList));
        // добавляем слушателя
        lstSingers.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            // выбор элемента
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                // создаем Intent
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                Cursor cursor = m_database.getCursorBySinger(l);
                // загружаем данные об исполнителе
                intent.putExtra("name", cursor.getString(cursor.getColumnIndex(SingersDatabase.QUERY_COL_NAME)));
                intent.putExtra("genres", cursor.getString(cursor.getColumnIndex(SingersDatabase.QUERY_COL_GENRES)));
                intent.putExtra("tracks", cursor.getInt(cursor.getColumnIndex(SingersDatabase.QUERY_COL_TRACKS)));
                intent.putExtra("albums", cursor.getInt(cursor.getColumnIndex(SingersDatabase.QUERY_COL_ALBUMS)));
                intent.putExtra("links", cursor.getString(cursor.getColumnIndex(SingersDatabase.QUERY_COL_LINK)));
                intent.putExtra("description", cursor.getString(cursor.getColumnIndex(SingersDatabase.QUERY_COL_DESCRIPTION)));
                intent.putExtra("coverBig", cursor.getString(cursor.getColumnIndex(SingersDatabase.QUERY_COL_COVER_BIG)));
                // запускаем новое Activity
                startActivity(intent);
            }
        });
        // создаем Loader для базы данных
        getSupportLoaderManager().initLoader(0, null, this);
*/
    }

    protected void onDestroy()
    {
        super.onDestroy();
        // закрываем подключение при выходе
        m_database.close();
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
        // заново запускаем скачивание
        m_downloader = new DownloadSingers(m_database, this);
        m_downloader.execute(URL_ARTISTS_JSON);
        return super.onOptionsItemSelected(item);
    }

    @Override
    // создание Loader'a для курсора
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        // создаем наш класс загрузки курсора
        return new SingersCursorLoader(this, m_database);
    }

    @Override
    // курсор загрузился
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        // меняем его на новый в адаптере
        m_adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }

    @Override
    // начало загрузки
    public void onBegin()
    {
        // создаем окно диалога
        m_progressDialog = new ProgressDialog(this);
        // заголовок
        m_progressDialog.setTitle(R.string.download_progress);
        // горизонтальная полоса загрузки
        m_progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // режим Busy
        m_progressDialog.setIndeterminate(true);
        // показываем
        m_progressDialog.show();
    }

    @Override
    // процесс загрузки
    public void onProgress(Integer state, Integer value)
    {
        switch ( state )
        {
            case DownloadSingers.STATUS_JSON_LOAD:
                // устанавливаем число исполнителей
                m_progressDialog.setMax(value);
                // обнуляем прогресс
                m_progressDialog.setProgress(0);
                // отключаем режим Busy
                m_progressDialog.setIndeterminate(false);
                break;
            case DownloadSingers.STATUS_JSON_PARSE_ITEM:
                // двигаем прогресс
                m_progressDialog.setProgress(value);
                break;
        }
    }

    @Override
    // ошибка при загрузке
    public void onFailure(Integer code)
    {
        int message;
        switch ( code )
        {
            case DownloadSingers.ERROR_URL:
            case DownloadSingers.ERROR_CONNECTION:
            case DownloadSingers.ERROR_UNKNOWN:
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

    @Override
    // окночание загрузки
    public void onEnd()
    {
        // закрываем прогресс бар
        m_progressDialog.dismiss();
        // обновляем курсор
        getSupportLoaderManager().getLoader(0).forceLoad();
    }

    // класс загрузки курсора
    static class SingersCursorLoader extends CursorLoader
    {
        SingersDatabase m_database;

        public SingersCursorLoader(Context context, SingersDatabase database)
        {
            super(context);
            m_database = database;
        }

        @Override
        public Cursor loadInBackground()
        {
            // получаем курсор
            return m_database.getCursorByView();
        }
    }
}
