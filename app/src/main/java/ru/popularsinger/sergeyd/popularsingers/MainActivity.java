package ru.popularsinger.sergeyd.popularsingers;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener
{
    private SingersDataBase m_database;
    private DownloadSingers m_downloader;
    private SingersAdapter m_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // прописываем настройки по умолчанию для ImageLoader-а
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);

        // создаем экземпляр класса базы данных и подключаемся к ней
        m_database = new SingersDataBase(this);
        m_database.open();

        m_downloader = new DownloadSingers(m_database, new DownloadSingersListener() {

            private ProgressDialog m_progress;
            @Override
            public void onBegin()
            {
                // создаем окно диалога
                m_progress = new ProgressDialog(MainActivity.this);
                // заголовок
                m_progress.setTitle(R.string.download_progress);
                // горизонтальная полоса загрузки
                m_progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                // режим Busy
                m_progress.setIndeterminate(true);
                // показываем
                m_progress.show();
            }

            @Override
            public void onProgress(Integer state, Integer value)
            {
                switch ( state )
                {
                    case DownloadSingers.STATUS_JSON_LOAD:
                        // устанавливаем число исполнителей
                        m_progress.setMax(value);
                        // обнуляем прогресс
                        m_progress.setProgress(0);
                        // отключаем режим Busy
                        m_progress.setIndeterminate(false);
                        break;
                    case DownloadSingers.STATUS_JSON_PARSE_ITEM:
                        // двигаем прогресс
                        m_progress.setProgress(value);
                        break;
                }
            }

            @Override
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

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                // заголовок
                builder.setTitle(R.string.download_error_title);
                // сообщение
                builder.setMessage(message);
                // иконка
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                // кнопка
                builder.setPositiveButton(R.string.button_ok, null);
                // создаем диалог
                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public void onEnd()
            {
                // закрываем прогресс бар
                m_progress.dismiss();

                if ( !m_database.isEmpty() )
                {
                    // создаем адаптер
                    m_adapter = new SingersAdapter(MainActivity.this, m_database.getAll());
                    // назначаем его ListView
                    ListView lstSingers = (ListView) findViewById(R.id.lstSingers);
                    lstSingers.setAdapter(m_adapter);
                    lstSingers.setOnItemClickListener(MainActivity.this);
                }
            }
        });
        m_downloader.execute("http://download.cdn.yandex.net/mobilization-2016/artists.json");
    }

    protected void onDestroy()
    {
        super.onDestroy();
        // закрываем подключение при выходе
        m_database.close();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("coverBig", m_database.getCoverBig(l));
        intent.putExtra("name", m_database.getName(l));
        intent.putExtra("genres", m_database.getGenres(l));
        intent.putExtra("tracks", m_database.getTracks(l));
        intent.putExtra("albums", m_database.getAlbums(l));
        intent.putExtra("links", m_database.getLink(l));
        intent.putExtra("description", m_database.getDescription(l));

        startActivity(intent);
    }
}
