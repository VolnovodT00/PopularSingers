package ru.popularsinger.sergeyd.popularsingers;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * Created by sergeyd on 04/07/2016.
 */
public class SingersAdapter extends BaseAdapter
{
    private Cursor m_cursor;
    private Context m_context;
    private LayoutInflater m_layoutInflater;
    private DisplayImageOptions m_options;

    public SingersAdapter(Context context, Cursor cursor)
    {
        // заполняем переменные
        m_cursor = cursor;
        m_context = context;
        // настраиваем опиции для DisplayImage
        m_options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_cover_small)
                .showImageForEmptyUri(R.drawable.empty_cover_small)
                .showImageOnFail(R.drawable.empty_cover_small)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }

    @Override
    public int getCount()
    { // получаем количество записей
        return m_cursor.getCount();
    }

    @Override
    public Object getItem(int i)
    { // получаем Item
        m_cursor.moveToPosition(i);
        return m_cursor;
    }

    @Override
    public long getItemId(int i)
    { // получаем индекс Item'a
        m_cursor.moveToPosition(i);
        return m_cursor.getLong(m_cursor.getColumnIndex(SingersDataBase.COLUMN_ID));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        // получаем holder
        ViewHolder holder;
        if ( view == null )
        {
            // View еще не создано - создаем
            view = LayoutInflater.from(m_context).inflate(R.layout.item, viewGroup, false);
            // создаем holder и заполняем
            holder = new ViewHolder();
            holder.image = (ImageView)view.findViewById(R.id.imgSmallCover);
            holder.name = (TextView)view.findViewById(R.id.lblName);
            holder.genres = (TextView)view.findViewById(R.id.lblGenres);
            holder.albumsTracks = (TextView)view.findViewById(R.id.lblAlbumsTracks);
            // сохраняем holder
            view.setTag(holder);
        }
        else
        {
            // получаем holder
            holder = (ViewHolder)view.getTag();
        }

        // получаем курор на текущую строку
        Cursor cursor = (Cursor)getItem(i);
        // читаем нужные колонки
        String url = cursor.getString(cursor.getColumnIndex(SingersDataBase.COLUMN_COVER_SMALL));
        String name = cursor.getString(cursor.getColumnIndex(SingersDataBase.COLUMN_NAME));
        String genres = cursor.getString(cursor.getColumnIndex(SingersDataBase.COLUMN_GENRES));
        int albums = cursor.getInt(cursor.getColumnIndex(SingersDataBase.COLUMN_ALBUMS));
        String albumsEnding = RightEndingString.getString(albums,
                m_context.getString(R.string.albums_nominative),
                m_context.getString(R.string.albums_genitive),
                m_context.getString(R.string.albums_plural));
        int tracks = cursor.getInt(cursor.getColumnIndex(SingersDataBase.COLUMN_TRACKS));
        String tracksEnding = RightEndingString.getString(tracks,
                m_context.getString(R.string.tracks_nominative),
                m_context.getString(R.string.tracks_genitive),
                m_context.getString(R.string.tracks_plural));

        // загружаем картинку
        ImageLoader.getInstance().displayImage(url, holder.image, m_options);
        // имя
        holder.name.setText(name);
        // жанры
        holder.genres.setText(genres);
        // число албомов и песен
        holder.albumsTracks.setText(albums + " " + albumsEnding + ", " + tracks + " " + tracksEnding);

        return view;
    }
    // holder для списка
    class ViewHolder
    {
        ImageView image;
        TextView name;
        TextView genres;
        TextView albumsTracks;
    }
}
