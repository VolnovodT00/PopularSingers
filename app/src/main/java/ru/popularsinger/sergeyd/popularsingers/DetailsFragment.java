package ru.popularsinger.sergeyd.popularsingers;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DetailsFragment extends Fragment
{
    String m_name;

    public static DetailsFragment newInstance(long index)
    {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putLong("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    public long getIndex()
    {
        return getArguments().getLong("index", 0);
    }

    public String getName()
    {
        return m_name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // получаем ссылки на View
        ImageView imgBigCover = (ImageView)view.findViewById(R.id.imgBigCover);
        TextView lblDetGenres = (TextView)view.findViewById(R.id.lblDetGenres);
        TextView lblDetAlbumsTracks = (TextView)view.findViewById(R.id.lblDetAlbumsTracks);
        TextView lblDetDescription = (TextView)view.findViewById(R.id.lblDetDescription);
        TextView lblDetLink = (TextView)view.findViewById(R.id.lblDetLink);

        long index = getIndex();
        if ( index != 0 )
        {
            Cursor cursor = new DatabaseReader(DatabaseHelper.getInstance(getActivity())).getCursorBySinger(index);
            // загружаем данные об исполнителе
            getActivity().setTitle(cursor.getString(cursor.getColumnIndex(DatabaseHelper.QUERY_COL_NAME)));
            Log.d("popularsingers", "frag: settitle");
            String genres = cursor.getString(cursor.getColumnIndex(DatabaseHelper.QUERY_COL_GENRES));
            int tracks = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.QUERY_COL_TRACKS));
            String tracksEnding = RightEndingString.getString(tracks,
                    getString(R.string.tracks_nominative),
                    getString(R.string.tracks_genitive),
                    getString(R.string.tracks_plural));
            int albums = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.QUERY_COL_ALBUMS));
            String albumsEnding = RightEndingString.getString(albums,
                    getString(R.string.albums_nominative),
                    getString(R.string.albums_genitive),
                    getString(R.string.albums_plural));
            String links = cursor.getString(cursor.getColumnIndex(DatabaseHelper.QUERY_COL_LINK));
            String description = cursor.getString(cursor.getColumnIndex(DatabaseHelper
                    .QUERY_COL_DESCRIPTION));
            if (!description.isEmpty())
            { // здесь нужно сделать заглавной первую букву
                description = description.substring(0, 1).toUpperCase() + description.substring(1);
            }
            String coverBig = cursor.getString(cursor.getColumnIndex(DatabaseHelper
                    .QUERY_COL_COVER_BIG));

            // загружаем картинку
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.empty_cover_big)
                    .showImageForEmptyUri(R.drawable.empty_cover_big)
                    .showImageOnFail(R.drawable.empty_cover_big)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .build();

            ImageLoader.getInstance().displayImage(coverBig, imgBigCover, options);
            // заполняем остальные View
            lblDetGenres.setText(genres);
            lblDetAlbumsTracks.setText(albums + " " + albumsEnding + "  \u2022  " + tracks + " " + tracksEnding);
            lblDetDescription.setText(description);
            lblDetLink.setText(links);
        }

        return view;
    }
}
