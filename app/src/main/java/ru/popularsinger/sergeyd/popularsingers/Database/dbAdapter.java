package ru.popularsinger.sergeyd.popularsingers.Database;

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

import ru.popularsinger.sergeyd.popularsingers.Database.dbHelper;
import ru.popularsinger.sergeyd.popularsingers.R;
import ru.popularsinger.sergeyd.popularsingers.RightEndingString;

/**
 * Created by sergeyd on 04/07/2016.
 */
public class dbAdapter extends BaseAdapter
{
    private Cursor m_cursor;
    private Context m_context;
    private DisplayImageOptions m_options;

    public dbAdapter(Context context, Cursor cursor)
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
        if ( m_cursor == null )
            return 0;
        return m_cursor.getCount();
    }

    @Override
    public Object getItem(int i)
    { // получаем Item
        if ( m_cursor == null )
            return null;

        m_cursor.moveToPosition(i);
        return m_cursor;
    }

    @Override
    public long getItemId(int i)
    { // получаем индекс Item'a
        if ( m_cursor == null )
            return 0;

        m_cursor.moveToPosition(i);
        return m_cursor.getLong(m_cursor.getColumnIndex(dbHelper.QUERY_COL_ID));
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
        if ( cursor == null )
            return view;

        // имя исполнителя
        String name = cursor.getString(cursor.getColumnIndex(dbHelper.QUERY_COL_NAME));
        String genres = cursor.getString(cursor.getColumnIndex(dbHelper.QUERY_COL_GENRES));
        int tracks = cursor.getInt(cursor.getColumnIndex(dbHelper.QUERY_COL_TRACKS));
        String tracksEnding = RightEndingString.getString(tracks,
                m_context.getString(R.string.tracks_nominative),
                m_context.getString(R.string.tracks_genitive),
                m_context.getString(R.string.tracks_plural));
        int albums = cursor.getInt(cursor.getColumnIndex(dbHelper.QUERY_COL_ALBUMS));
        String albumsEnding = RightEndingString.getString(albums,
                m_context.getString(R.string.albums_nominative),
                m_context.getString(R.string.albums_genitive),
                m_context.getString(R.string.albums_plural));
        String coverSmall = cursor.getString(cursor.getColumnIndex(dbHelper.QUERY_COL_COVER_SMALL));

        // загружаем картинку
        ImageLoader.getInstance().displayImage(coverSmall, holder.image, m_options);
        // имя
        holder.name.setText(name);
        // жанры
        holder.genres.setText(genres);
        // число албомов и песен
        holder.albumsTracks.setText(albums + " " + albumsEnding + ", " + tracks + " " + tracksEnding);

        return view;
    }

    public void changeCursor(Cursor cursor)
    {
        // устанавливаем новый курсор
        m_cursor = cursor;
        // вызываем функцию перерисовки
        notifyDataSetChanged();
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
