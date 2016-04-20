package ru.popularsinger.sergeyd.popularsingers.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Dell on 16.04.2016.
 */
public class dbHelper extends SQLiteOpenHelper
{
    private static final String BASE_NAME = "singers_database";
    private static final int BASE_VERSION = 2;

    public static final String QUERY_COL_ID = "id";
    public static final String QUERY_COL_NAME = "name";
    public static final String QUERY_COL_GENRES = "genres";
    public static final String QUERY_COL_TRACKS = "tracks";
    public static final String QUERY_COL_ALBUMS = "albums";
    public static final String QUERY_COL_LINK = "link";
    public static final String QUERY_COL_DESCRIPTION = "description";
    public static final String QUERY_COL_COVER_SMALL = "cover_small";
    public static final String QUERY_COL_COVER_BIG = "cover_big";

    public static final String TABLE_SINGERS = "table_singers";
    public static final String TABLE_SINGERS_COL_ID = "id";
    public static final String TABLE_SINGERS_COL_NAME_ID = "name_id";
    public static final String TABLE_SINGERS_COL_NAME = "name";
    public static final String TABLE_SINGERS_COL_TRACKS = "tracks";
    public static final String TABLE_SINGERS_COL_ALBUMS = "albums";
    public static final String TABLE_SINGERS_COL_LINK = "link";
    public static final String TABLE_SINGERS_COL_DESCRIPTION = "description";
    public static final String TABLE_SINGERS_COL_COVER_SMALL = "cover_small";
    public static final String TABLE_SINGERS_COL_COVER_BIG = "cover_big";

    private static final String TABLE_SINGERS_CREATE =
            "CREATE TABLE " + TABLE_SINGERS + "(" +
                    TABLE_SINGERS_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TABLE_SINGERS_COL_NAME_ID + " INTEGER, " +
                    TABLE_SINGERS_COL_NAME + " TEXT, " +
                    TABLE_SINGERS_COL_TRACKS + " INTEGER, " +
                    TABLE_SINGERS_COL_ALBUMS + " INTEGER, " +
                    TABLE_SINGERS_COL_LINK + " TEXT, " +
                    TABLE_SINGERS_COL_DESCRIPTION + " TEXT, " +
                    TABLE_SINGERS_COL_COVER_SMALL + " TEXT, " +
                    TABLE_SINGERS_COL_COVER_BIG + " TEXT" +
                    ")";

    public static final String TABLE_GENRES = "table_genres";
    public static final String TABLE_GENRES_COL_ID = "id";
    public static final String TABLE_GENRES_COL_GENRE = "genre";

    private static final String TABLE_GENRES_CREATE =
            "CREATE TABLE " + TABLE_GENRES + "(" +
                    TABLE_GENRES_COL_ID + " INTEGER PRIMARY KEY, " +
                    TABLE_GENRES_COL_GENRE + " TEXT UNIQUE" +
                    ")";

    public static final String TABLE_COMBINATIONS = "table_combination";
    public static final String TABLE_COMBINATIONS_COL_ID = "id";
    public static final String TABLE_COMBINATIONS_COL_NAME_ID = "name_id";
    public static final String TABLE_COMBINATIONS_COL_GENRE_ID = "genre_id";

    private static final String TABLE_COMBINATIONS_CREATE =
            "CREATE TABLE " + TABLE_COMBINATIONS + "(" +
                    TABLE_COMBINATIONS_COL_ID + " INTEGER PRIMARY KEY, " +
                    TABLE_COMBINATIONS_COL_NAME_ID + " INTEGER, " +
                    TABLE_COMBINATIONS_COL_GENRE_ID + " INTEGER" +
                    ")";

    private static dbHelper m_instance = null;

    private dbHelper(Context context)
    {
        super(context, BASE_NAME, null, BASE_VERSION);
    }

    public static dbHelper getInstance(Context context)
    {
        if ( m_instance == null )
            m_instance = new dbHelper(context);
        return m_instance;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        // создаем все необходимые таблички
        sqLiteDatabase.execSQL(TABLE_SINGERS_CREATE);
        sqLiteDatabase.execSQL(TABLE_GENRES_CREATE);
        sqLiteDatabase.execSQL(TABLE_COMBINATIONS_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1)
    {
        sqLiteDatabase.beginTransaction();
        try
        {
            // удаляем старую таблицу
            sqLiteDatabase.execSQL("DROP TABLE singers");

            // создаем все необходимые таблички
            sqLiteDatabase.execSQL(TABLE_SINGERS_CREATE);
            sqLiteDatabase.execSQL(TABLE_GENRES_CREATE);
            sqLiteDatabase.execSQL(TABLE_COMBINATIONS_CREATE);

            sqLiteDatabase.setTransactionSuccessful();
        }
        finally
        {
            sqLiteDatabase.endTransaction();
        }
    }
}
