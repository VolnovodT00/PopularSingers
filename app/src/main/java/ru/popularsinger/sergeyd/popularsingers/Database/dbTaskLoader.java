package ru.popularsinger.sergeyd.popularsingers.Database;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by sergeyd on 04/19/2016.
 */
public class dbTaskLoader extends AsyncTaskLoader<Integer>
{
    private final String URL_ARTISTS_JSON = "http://download.cdn.yandex.net/mobilization-2016/artists.json";

    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_URL = 1;
    public static final int ERROR_CONNECTION = 2;
    public static final int ERROR_JSON = 3;
    public static final int ERROR_UNKNOWN = 4;

    private dbWriter m_writer;

    public dbTaskLoader(Context context)
    {
        super(context);
        m_writer = new dbWriter(dbHelper.getInstance(context));
    }

    @Override
    public Integer loadInBackground()
    {
        // получаем данные с внещнего ресурса
        try
        {
            // задаем адрес
            URL url = new URL(URL_ARTISTS_JSON);
            // подключаемся для чтения
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.connect();
            // проверяем на подключение
            if ( connection.getResponseCode() != HttpURLConnection.HTTP_OK )
            {
                return ERROR_CONNECTION;
            }

            // начинаем чтение
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder json = new StringBuilder();
            while ( (line = reader.readLine()) != null ) {
                json.append(line);
            }
            // закрываем буфер чтения
            reader.close();
            // отключаемся
            connection.disconnect();


            // распарсиваем полученную строку
            JSONArray jsonSingers = new JSONArray(json.toString());
            for ( int i=0; i<jsonSingers.length(); i++ )
            {
                // получаем JSON объект (информация о певце)
                JSONObject jsonSinger = jsonSingers.getJSONObject(i);
                // получаем ID
                int id = jsonSinger.optInt("id");
                // полчаем имя
                String name = jsonSinger.optString("name");
                // полчаем массив жанров
                JSONArray jsonGenres = jsonSinger.optJSONArray("genres");

                String[] genres;
                if ( jsonGenres.length() != 0 )
                {
                    genres = new String[jsonGenres.length()];
                    for (int j = 0; j < jsonGenres.length(); j++)
                        genres[j] = jsonGenres.optString(j);
                }
                else
                {
                    // пустой жанр
                    genres = new String[] {""};
                }
                // получаем число треков
                int tracks = jsonSinger.optInt("tracks");
                // получаем число альбомов
                int albums = jsonSinger.optInt("albums");
                // получаем ссылку
                String link = jsonSinger.optString("link");
                // получаем описание
                String description = jsonSinger.optString("description");
                // получаем ссылки на изображения
                JSONObject jsonCover = jsonSinger.optJSONObject("cover");
                String coverSmall = jsonCover.optString("small");
                String coverBig = jsonCover.optString("big");

                // записываем данные в таблицу
                if ( !m_writer.checkRecord(id) )
                    m_writer.add(id, name, genres, tracks, albums, link, description, coverSmall, coverBig);
            }
        }
        catch ( MalformedURLException e ) // if spec could not be parsed as a URL.
        {
            return ERROR_URL;
        }
        catch ( IOException e ) // if an error occurs while opening the connection.
        // if this reader is closed or some other I/O error occurs.
        {
            return ERROR_CONNECTION;
        }
        catch ( JSONException e )   // if the parse fails or doesn't yield a JSONArray.
        // if the value doesn't exist or is not a JSONObject.
        {
            return ERROR_JSON;
        }
        catch ( Exception e )
        {
            return ERROR_UNKNOWN;
        }

        return ERROR_NO_ERROR;
    }

    @Override
    protected void onStartLoading()
    {
        super.onStartLoading();

        this.forceLoad();
    }

}
