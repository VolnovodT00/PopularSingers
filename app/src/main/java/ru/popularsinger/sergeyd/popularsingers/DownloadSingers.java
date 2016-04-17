package ru.popularsinger.sergeyd.popularsingers;

import android.content.Context;
import android.os.AsyncTask;

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
 * Created by sergeyd on 04/05/2016.
 */

public class DownloadSingers extends AsyncTask<String, Integer, Integer>
{
    public static final int STATUS_JSON_LOAD = 0;
    public static final int STATUS_JSON_PARSE_ITEM = 1;

    public static final int ERROR_NO_ERROR = 0;
    public static final int ERROR_URL = 1;
    public static final int ERROR_CONNECTION = 2;
    public static final int ERROR_JSON = 3;
    public static final int ERROR_UNKNOWN = 4;

    private DatabaseWriter m_writer;
    private DownloadSingersListener m_listener;

    public DownloadSingers(Context context, DownloadSingersListener listener)
    {
        m_writer = new DatabaseWriter(DatabaseHelper.getInstance(context));
        m_listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // отпрвляем, что начали скачивать
        m_listener.onBegin();
    }

    @Override
    protected Integer doInBackground(String... urls)
    {
        // получаем данные с внещнего ресурса
        try
        {
            // задаем адрес
            URL url = new URL(urls[0]);
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

            // отправляем сообщение о том, что Json скачали и сейчас будем загружать исполнителей
            publishProgress(STATUS_JSON_LOAD, jsonSingers.length());

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

                // посылаем сообщение об числе загруженных исполнетелей
                publishProgress(STATUS_JSON_PARSE_ITEM, i + 1);
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
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        m_listener.onProgress(values[0], values[1]);
    }

    @Override
    protected void onPostExecute(Integer result)
    {
        super.onPostExecute(result);

        // отправляем, что закончили
        m_listener.onEnd();

        if ( result != ERROR_NO_ERROR )
        { // выводим ошибку
            m_listener.onFailure(result);
        }
    }

    public interface DownloadSingersListener
    {
        // начинаем скачивание
        void onBegin();
        // процесс скачивания
        void onProgress(Integer state, Integer value);
        // получили ошибку
        void onFailure(Integer code);
        // закончили
        void onEnd();
    }
}
