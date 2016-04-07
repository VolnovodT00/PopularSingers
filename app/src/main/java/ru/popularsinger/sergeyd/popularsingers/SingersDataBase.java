package ru.popularsinger.sergeyd.popularsingers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sergeyd on 04/05/2016.
 */
public class SingersDataBase
{
    private static final String DB_NAME = "singers_database";
    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "singers";

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

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_UID = "uid";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_GENRES = "genres";
    public static final String COLUMN_TRACKS = "tracks";
    public static final String COLUMN_ALBUMS = "albums";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_COVER_SMALL = "cover_small";
    public static final String COLUMN_COVER_BIG = "cover_big";

    private static final String DB_CREATE =
            "create table " + DB_TABLE + "(" +
                    COLUMN_ID + " integer primary key autoincrement, " +
                    COLUMN_UID + " integer, " +
                    COLUMN_NAME + " text, " +
                    COLUMN_GENRES + " text, " +
                    COLUMN_TRACKS + " integer, " +
                    COLUMN_ALBUMS + " integer, " +
                    COLUMN_LINK + " text, " +
                    COLUMN_DESCRIPTION + " text, " +
                    COLUMN_COVER_SMALL + " text, " +
                    COLUMN_COVER_BIG + " text" +
                    ");";

    private final Context m_context;

    private SingerDataBaseHelper m_helper;
    private SQLiteDatabase m_database;

    public SingersDataBase(Context context)
    {
        m_context = context;
    }

    // открываем подключение
    public void open()
    {
        m_helper = new SingerDataBaseHelper(m_context, DB_NAME, null, DB_VERSION);
        m_database = m_helper.getWritableDatabase();
    }

    // закрываем подключение
    public void close()
    {
        if ( m_helper != null )
            m_helper.close();
    }

    public boolean isEmpty()
    {
        Cursor cursor = getAll();
        return (cursor.getCount() == 0);
    }

    // удаляем все записи
    public int clearAll()
    {
        return m_database.delete(DB_TABLE, null, null);
    }

    // проверить, есть ли запись с таким UID
    public boolean checkRecord(int uid)
    {
        String from[] = { COLUMN_UID };
        String where = COLUMN_UID + "=" + uid;
        Cursor cursor = m_database.query(DB_TABLE, from, where, null, null, null, null, null);
        return (cursor.getCount() > 0);
    }

    // добавляем новую запись
    public long add(int uid, String name, String genres, int tracks, int albums, String link,
                    String description, String coverSmall, String coverBig)
    {
        ContentValues values = new ContentValues();
        values.put(COLUMN_UID, uid);
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_GENRES, genres);
        values.put(COLUMN_TRACKS, tracks);
        values.put(COLUMN_ALBUMS, albums);
        values.put(COLUMN_LINK, link);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_COVER_SMALL, coverSmall);
        values.put(COLUMN_COVER_BIG, coverBig);
        // добавляем в базу
        return m_database.insert(DB_TABLE, null, values);
    }

    // удаляем запись
    public int delete(long id)
    {
        return m_database.delete(DB_TABLE, COLUMN_ID + " = " + id, null);
    }

    // получаем все данные из таблицы
    public Cursor getAll()
    {
        Cursor cursor = m_database.query(DB_TABLE, null, null, null, null, null, null);
        return cursor;
    }

    private Cursor get(long id, String column)
    {
        String from[] = { column };
        String where = COLUMN_ID + "=" + id;
        Cursor cursor = m_database.query(DB_TABLE, from, where, null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    public int getUID(long id)
    {
        Cursor cursor = get(id, COLUMN_UID);
        return cursor.getInt(cursor.getColumnIndex(COLUMN_UID));
    }

    public String getName(long id)
    {
        Cursor cursor = get(id, COLUMN_NAME);
        return cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
    }

    public String getGenres(long id)
    {
        Cursor cursor = get(id, COLUMN_GENRES);
        return cursor.getString(cursor.getColumnIndex(COLUMN_GENRES));
    }

    public int getTracks(long id)
    {
        Cursor cursor = get(id, COLUMN_TRACKS);
        return cursor.getInt(cursor.getColumnIndex(COLUMN_TRACKS));
    }

    public int getAlbums(long id)
    {
        Cursor cursor = get(id, COLUMN_ALBUMS);
        return cursor.getInt(cursor.getColumnIndex(COLUMN_ALBUMS));
    }

    public String getLink(long id)
    {
        Cursor cursor = get(id, COLUMN_LINK);
        return cursor.getString(cursor.getColumnIndex(COLUMN_LINK));
    }

    public String getDescription(long id)
    {
        Cursor cursor = get(id, COLUMN_DESCRIPTION);
        return cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION));
    }

    public String getCoverSmall(long id)
    {
        Cursor cursor = get(id, COLUMN_COVER_SMALL);
        return cursor.getString(cursor.getColumnIndex(COLUMN_COVER_SMALL));
    }

    public String getCoverBig(long id)
    {
        Cursor cursor = get(id, COLUMN_COVER_BIG);
        return cursor.getString(cursor.getColumnIndex(COLUMN_COVER_BIG));
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
            db.execSQL(DB_CREATE);
        }

        // обновление базы данных
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            // TODO: сдалать обновление
        }
    }
}
