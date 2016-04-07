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
    public SingersDataBase m_databaseSingers;

    private DownloadSingers m_downloader;
    private SingersAdapter m_adapterSingers;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // прописываем настройки по умолчанию для ImageLoader-а
        ImageLoaderConfiguration config = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(config);

        // создаем экземпляр класса базы данных и подключаемся к ней
        m_databaseSingers = new SingersDataBase(this);
        m_databaseSingers.open();

        m_downloader = new DownloadSingers(m_databaseSingers, new DownloadSingersListener() {

            private ProgressDialog m_progress;
            @Override
            public void onBegin()
            {
                // создаем окно диалога
                m_progress = new ProgressDialog(MainActivity.this);
                // заголовок
                m_progress.setTitle("Загрузка списка исполнителей");
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
            public void onSuccess()
            {
                // создаем адаптер
                m_adapterSingers = new SingersAdapter(MainActivity.this, m_databaseSingers.getAll());
                // назначаем его ListView
                ListView lstSingers = (ListView) findViewById(R.id.lstSingers);
                lstSingers.setAdapter(m_adapterSingers);
                lstSingers.setOnItemClickListener(MainActivity.this);
            }

            @Override
            public void onFailure(Integer code)
            {
                String message = "";
                switch ( code )
                {
                    case DownloadSingers.ERROR_URL:
                    case DownloadSingers.ERROR_CONNECTION:
                    case DownloadSingers.ERROR_UNKNOWN:
                        message = "Ошибка подключения к серверу";
                        break;
                    case DownloadSingers.ERROR_JSON:
                        message = "Ошибка чтения списка исполнителей";
                        break;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                // заголовок
                builder.setTitle("Ошибка");
                // сообщение
                builder.setMessage(message);
                // иконка
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                // кнопка
                builder.setPositiveButton("ОК", null);
                // создаем диалог
                AlertDialog alert = builder.create();
                alert.show();
            }

            @Override
            public void onEnd()
            {
                // закрываем прогресс бар
                m_progress.dismiss();
            }
        });
        m_downloader.execute("http://download.cdn.yandex.net/mobilization-2016/artists.json");
    }

    protected void onDestroy()
    {
        super.onDestroy();
        // закрываем подключение при выходе
        m_databaseSingers.close();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra("coverBig", m_databaseSingers.getCoverBig(l));
        intent.putExtra("name", m_databaseSingers.getName(l));
        intent.putExtra("genres", m_databaseSingers.getGenres(l));
        intent.putExtra("tracks", m_databaseSingers.getTracks(l));
        intent.putExtra("albums", m_databaseSingers.getAlbums(l));
        intent.putExtra("links", m_databaseSingers.getLink(l));
        intent.putExtra("description", m_databaseSingers.getDescription(l));

        startActivity(intent);
    }
}
