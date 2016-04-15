package ru.popularsinger.sergeyd.popularsingers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sergeyd on 04/05/2016.
 */
public class SingersDatabase
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

    private static final String TABLE_SINGERS = "table_singers";
    private static final String TABLE_SINGERS_COL_ID = "id";
    private static final String TABLE_SINGERS_COL_NAME_ID = "name_id";
    private static final String TABLE_SINGERS_COL_NAME = "name";
    private static final String TABLE_SINGERS_COL_TRACKS = "tracks";
    private static final String TABLE_SINGERS_COL_ALBUMS = "albums";
    private static final String TABLE_SINGERS_COL_LINK = "link";
    private static final String TABLE_SINGERS_COL_DESCRIPTION = "description";
    private static final String TABLE_SINGERS_COL_COVER_SMALL = "cover_small";
    private static final String TABLE_SINGERS_COL_COVER_BIG = "cover_big";

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

    private static final String TABLE_GENRES = "table_genres";
    private static final String TABLE_GENRES_COL_ID = "id";
    private static final String TABLE_GENRES_COL_GENRE = "genre";

    private static final String TABLE_GENRES_CREATE =
            "CREATE TABLE " + TABLE_GENRES + "(" +
                    TABLE_GENRES_COL_ID + " INTEGER PRIMARY KEY, " +
                    TABLE_GENRES_COL_GENRE + " TEXT UNIQUE" +
                    ")";

    private static final String TABLE_COMBINATIONS = "table_combination";
    private static final String TABLE_COMBINATIONS_COL_ID = "id";
    private static final String TABLE_COMBINATIONS_COL_NAME_ID = "name_id";
    private static final String TABLE_COMBINATIONS_COL_GENRE_ID = "genre_id";

    private static final String TABLE_COMBINATIONS_CREATE =
            "CREATE TABLE " + TABLE_COMBINATIONS + "(" +
                    TABLE_COMBINATIONS_COL_ID + " INTEGER PRIMARY KEY, " +
                    TABLE_COMBINATIONS_COL_NAME_ID + " INTEGER, " +
                    TABLE_COMBINATIONS_COL_GENRE_ID + " INTEGER" +
                    ")";

    /*
    {
        "id":1080505,
        "name":"Tove Lo",
        "genres":["pop","dance","electronics"],
        "tracks":81,
        "albums":22,
        "link":"http://www.tove-lo.com/",
        "description":"...",
        "cover":
            {
            "small":"http://.../300x300",
            "big":"http://.../1000x1000"
            }
    },
    */

    private SingerDataBaseHelper m_helper;
    private SQLiteDatabase m_database;

    public SingersDatabase(Context context)
    {
        m_helper = new SingerDataBaseHelper(context, BASE_NAME, null, BASE_VERSION);
        m_database = m_helper.getWritableDatabase();
    }

    // закрываем подключение
    public void close()
    {
        if ( m_helper != null )
            m_helper.close();
    }

    // проверить, есть ли запись с таким UID
    public boolean checkRecord(int name_id)
    {
        Cursor cursor = m_database.query(TABLE_SINGERS, new String[]{TABLE_SINGERS_COL_NAME_ID}, TABLE_SINGERS_COL_NAME_ID + "=" + name_id, null, null, null, null, null);
        return (cursor.getCount() > 0);
    }

    // добавляем новую запись
    public void add(int name_id, String name, String[] genres, int tracks, int albums, String link,
                    String description, String coverSmall, String coverBig)
    {
        ContentValues values = new ContentValues();

        m_database.beginTransaction();
        try
        {
            // заполняем основную табличку
            values.put(TABLE_SINGERS_COL_NAME_ID, name_id);
            values.put(TABLE_SINGERS_COL_NAME, name);
            values.put(TABLE_SINGERS_COL_TRACKS, tracks);
            values.put(TABLE_SINGERS_COL_ALBUMS, albums);
            values.put(TABLE_SINGERS_COL_LINK, link);
            values.put(TABLE_SINGERS_COL_DESCRIPTION, description);
            values.put(TABLE_SINGERS_COL_COVER_SMALL, coverSmall);
            values.put(TABLE_SINGERS_COL_COVER_BIG, coverBig);
            m_database.insert(TABLE_SINGERS, null, values);

            // добавляем новые записи в табличку жанров и табличку пересечений
            for (String genre : genres)
            {
                genre = genre.trim();

                values.clear();
                values.put(TABLE_GENRES_COL_GENRE, genre);
                // пишем в таблицу без повторов
                long genre_id = m_database.insertWithOnConflict(TABLE_GENRES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if ( genre_id == -1 )
                {
                    // получаем курсор на _id жанра
                    Cursor cursor = m_database.query(TABLE_GENRES, new String[] { TABLE_GENRES_COL_ID }, TABLE_GENRES_COL_GENRE + "='"+genre+"'", null, null, null, null);
                    cursor.moveToFirst();
                    // получаем _id жанра
                    genre_id = cursor.getInt(cursor.getColumnIndex(TABLE_GENRES_COL_ID));
                    cursor.close();
                }

                values.clear();
                values.put(TABLE_COMBINATIONS_COL_NAME_ID, name_id);
                values.put(TABLE_COMBINATIONS_COL_GENRE_ID, genre_id);
                m_database.insert(TABLE_COMBINATIONS, null, values);
            }

            m_database.setTransactionSuccessful();
        }
        finally
        {
            m_database.endTransaction();
        }
    }

    public Cursor getCursorByView()
    {
        String sql = "SELECT "+TABLE_SINGERS+"."+TABLE_SINGERS_COL_NAME_ID+" AS "+QUERY_COL_ID+", "+
                TABLE_SINGERS_COL_NAME+" AS "+QUERY_COL_NAME+", "+
                "GROUP_CONCAT("+TABLE_GENRES_COL_GENRE+", ', ') AS "+QUERY_COL_GENRES+", "+
                TABLE_SINGERS_COL_TRACKS+" AS "+QUERY_COL_TRACKS+", "+
                TABLE_SINGERS_COL_ALBUMS+" AS "+QUERY_COL_ALBUMS+", "+
                TABLE_SINGERS_COL_COVER_SMALL+" AS "+QUERY_COL_COVER_SMALL+
                " FROM "+TABLE_SINGERS+", "+TABLE_GENRES+", "+TABLE_COMBINATIONS+
                " WHERE "+TABLE_SINGERS+"."+TABLE_SINGERS_COL_NAME_ID+"="+TABLE_COMBINATIONS+"."+TABLE_COMBINATIONS_COL_NAME_ID+
                " AND "+TABLE_GENRES+"."+TABLE_GENRES_COL_ID+"="+TABLE_COMBINATIONS+"."+TABLE_COMBINATIONS_COL_GENRE_ID+
                " GROUP BY "+TABLE_SINGERS_COL_NAME;
        return m_database.rawQuery(sql, null);
    }

    public Cursor getCursorBySinger(long name_id)
    {
        String sql = "SELECT "+TABLE_SINGERS_COL_NAME+" AS "+QUERY_COL_NAME+", "+
                "GROUP_CONCAT("+TABLE_GENRES_COL_GENRE+", ', ') AS "+QUERY_COL_GENRES+", "+
                TABLE_SINGERS_COL_TRACKS+" AS "+QUERY_COL_TRACKS+", "+
                TABLE_SINGERS_COL_ALBUMS+" AS "+QUERY_COL_ALBUMS+", "+
                TABLE_SINGERS_COL_LINK+" AS "+QUERY_COL_LINK+", "+
                TABLE_SINGERS_COL_DESCRIPTION+" AS "+QUERY_COL_DESCRIPTION+", "+
                TABLE_SINGERS_COL_COVER_BIG+" AS "+QUERY_COL_COVER_BIG+
                " FROM "+TABLE_SINGERS+", "+TABLE_GENRES+", "+TABLE_COMBINATIONS+
                " WHERE "+TABLE_SINGERS+"."+TABLE_SINGERS_COL_NAME_ID+"=?"+
                " AND "+TABLE_SINGERS+"."+TABLE_SINGERS_COL_NAME_ID+"="+TABLE_COMBINATIONS+"."+TABLE_COMBINATIONS_COL_NAME_ID+
                " AND "+TABLE_GENRES+"."+TABLE_GENRES_COL_ID+"="+TABLE_COMBINATIONS+"."+TABLE_COMBINATIONS_COL_GENRE_ID+
                " GROUP BY "+TABLE_SINGERS_COL_NAME;
        Cursor cursor = m_database.rawQuery(sql, new String[]{Long.toString(name_id)});
        cursor.moveToFirst();
        return cursor;
    }

    // класс по созданию и управлению базой данных
    private class SingerDataBaseHelper extends SQLiteOpenHelper
    {
        public SingerDataBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }

        // создаем базу данных
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            // создаем все необходимые таблички
            db.execSQL(TABLE_SINGERS_CREATE);
            db.execSQL(TABLE_GENRES_CREATE);
            db.execSQL(TABLE_COMBINATIONS_CREATE);
        }

        // обновление базы данных
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            if ( oldVersion == 1 && newVersion == 2 )
            {
                ContentValues values = new ContentValues();
                Cursor mainTableCursor;

                db.beginTransaction();
                try
                {
                    // создаем все необходимые таблички
                    db.execSQL(TABLE_SINGERS_CREATE);
                    db.execSQL(TABLE_GENRES_CREATE);
                    db.execSQL(TABLE_COMBINATIONS_CREATE);

                    // получаем курсор на старую основную таблицу
                    mainTableCursor = db.query("singers", null, null, null, null, null, null);
                    if ( mainTableCursor.moveToFirst() )
                    {
                        do
                        {
                            // получаем данные из старой таблицы
                            int name_id = mainTableCursor.getInt(mainTableCursor.getColumnIndex("uid"));
                            String name = mainTableCursor.getString(mainTableCursor.getColumnIndex("name"));
                            String[] genres = mainTableCursor.getString(mainTableCursor.getColumnIndex("genres")).split(",");
                            int tracks = mainTableCursor.getInt(mainTableCursor.getColumnIndex("tracks"));
                            int albums = mainTableCursor.getInt(mainTableCursor.getColumnIndex("albums"));
                            String link = mainTableCursor.getString(mainTableCursor.getColumnIndex("link"));
                            String description = mainTableCursor.getString(mainTableCursor.getColumnIndex("description"));
                            String coverSmall = mainTableCursor.getString(mainTableCursor.getColumnIndex("cover_small"));
                            String coverBig = mainTableCursor.getString(mainTableCursor.getColumnIndex("cover_big"));

                            // заполняем основную табличку
                            values.clear();
                            values.put(TABLE_SINGERS_COL_NAME_ID, name_id);
                            values.put(TABLE_SINGERS_COL_NAME, name);
                            values.put(TABLE_SINGERS_COL_TRACKS, tracks);
                            values.put(TABLE_SINGERS_COL_ALBUMS, albums);
                            values.put(TABLE_SINGERS_COL_LINK, link);
                            values.put(TABLE_SINGERS_COL_DESCRIPTION, description);
                            values.put(TABLE_SINGERS_COL_COVER_SMALL, coverSmall);
                            values.put(TABLE_SINGERS_COL_COVER_BIG, coverBig);
                            db.insert(TABLE_SINGERS, null, values);

                            // добавляем новые записи в табличку жанров и табличку пересечений
                            for (String genre : genres)
                            {
                                genre = genre.trim();

                                values.clear();
                                values.put(TABLE_GENRES_COL_GENRE, genre);
                                // пишем в таблицу без повторов
                                long genre_id = db.insertWithOnConflict(TABLE_GENRES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                                if ( genre_id == -1 )
                                {
                                    // получаем курсор на _id жанра
                                    Cursor cursor = db.query(TABLE_GENRES, new String[] { TABLE_GENRES_COL_ID }, TABLE_GENRES_COL_GENRE + "='"+genre+"'", null, null, null, null);
                                    cursor.moveToFirst();
                                    // получаем _id жанра
                                    genre_id = cursor.getInt(cursor.getColumnIndex(TABLE_GENRES_COL_ID));
                                    cursor.close();
                                }

                                values.clear();
                                values.put(TABLE_COMBINATIONS_COL_NAME_ID, name_id);
                                values.put(TABLE_COMBINATIONS_COL_GENRE_ID, genre_id);
                                db.insert(TABLE_COMBINATIONS, null, values);
                            }
                        }
                        while (mainTableCursor.moveToNext());
                    }

                    // удаляем временную таблицу
                    db.execSQL("DROP TABLE singers");
                    db.setTransactionSuccessful();
                }
                finally
                {
                    db.endTransaction();
                }
            }
        }
    }
}
