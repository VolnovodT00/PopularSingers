package ru.popularsinger.sergeyd.popularsingers;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DetailActivity extends ActionBarActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // получаем ссылки на View
        ImageView imgBigCover = (ImageView)findViewById(R.id.imgBigCover);
        TextView lblDetGenres = (TextView)findViewById(R.id.lblDetGenres);
        TextView lblDetAlbumsTracks = (TextView)findViewById(R.id.lblDetAlbumsTracks);
        TextView lblDetDescription = (TextView)findViewById(R.id.lblDetDescription);

        // вынимаем из Intent данные об исполнителе
        Intent intent = getIntent();
        String url = intent.getStringExtra("coverBig");
        String name = intent.getStringExtra("name");
        String genres = intent.getStringExtra("genres");
        int tracks  = intent.getIntExtra("tracks", 0);
        String tracksEnding = RightEndingString.getString(tracks,
                getString(R.string.tracks_nominative),
                getString(R.string.tracks_genitive),
                getString(R.string.tracks_plural));
        int albums = intent.getIntExtra("albums", 0);
        String albumsEnding = RightEndingString.getString(albums,
                getString(R.string.albums_nominative),
                getString(R.string.albums_genitive),
                getString(R.string.albums_plural));
        String links = intent.getStringExtra("links");
        String description = intent.getStringExtra("description");

        // загружаем картинку
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_cover_small)
                .showImageForEmptyUri(R.drawable.empty_cover_small)
                .showImageOnFail(R.drawable.empty_cover_small)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();

        ImageLoader.getInstance().displayImage(url, imgBigCover, options);
        // заполняем остальные View
        lblDetGenres.setText(genres);
        lblDetAlbumsTracks.setText(albums + " " + albumsEnding + " * " + tracks + " " + tracksEnding);
        lblDetDescription.setText(description);
    }

}
