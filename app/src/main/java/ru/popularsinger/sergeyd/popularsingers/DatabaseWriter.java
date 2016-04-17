package ru.popularsinger.sergeyd.popularsingers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Dell on 16.04.2016.
 */
public class DatabaseWriter
{
    // экземпляр базы данных
    private SQLiteDatabase m_db = null;

    public DatabaseWriter(DatabaseHelper helper)
    {
        m_db = helper.getWritableDatabase();
    }

    // проверить, есть ли запись с таким UID
    public boolean checkRecord(int name_id)
    {
        Cursor cursor = m_db.query(DatabaseHelper.TABLE_SINGERS,
                new String[]{DatabaseHelper.TABLE_SINGERS_COL_NAME_ID},
                DatabaseHelper.TABLE_SINGERS_COL_NAME_ID + "=" + name_id,
                null, null, null, null, null);
        return (cursor.getCount() > 0);
    }

    // добавляем новую запись
    public void add(int name_id, String name, String[] genres, int tracks, int albums, String link,
                    String description, String coverSmall, String coverBig)
    {
        ContentValues values = new ContentValues();

        m_db.beginTransaction();
        try
        {
            // заполняем основную табличку
            values.put(DatabaseHelper.TABLE_SINGERS_COL_NAME_ID, name_id);
            values.put(DatabaseHelper.TABLE_SINGERS_COL_NAME, name);
            values.put(DatabaseHelper.TABLE_SINGERS_COL_TRACKS, tracks);
            values.put(DatabaseHelper.TABLE_SINGERS_COL_ALBUMS, albums);
            values.put(DatabaseHelper.TABLE_SINGERS_COL_LINK, link);
            values.put(DatabaseHelper.TABLE_SINGERS_COL_DESCRIPTION, description);
            values.put(DatabaseHelper.TABLE_SINGERS_COL_COVER_SMALL, coverSmall);
            values.put(DatabaseHelper.TABLE_SINGERS_COL_COVER_BIG, coverBig);
            m_db.insert(DatabaseHelper.TABLE_SINGERS, null, values);

            // добавляем новые записи в табличку жанров и табличку пересечений
            for (String genre : genres)
            {
                genre = genre.trim();

                values.clear();
                values.put(DatabaseHelper.TABLE_GENRES_COL_GENRE, genre);
                // пишем в таблицу без повторов
                long genre_id = m_db.insertWithOnConflict(DatabaseHelper.TABLE_GENRES, null, values, SQLiteDatabase.CONFLICT_IGNORE);
                if ( genre_id == -1 )
                {
                    // получаем курсор на _id жанра
                    Cursor cursor = m_db.query(DatabaseHelper.TABLE_GENRES,
                            new String[] { DatabaseHelper.TABLE_GENRES_COL_ID },
                            DatabaseHelper.TABLE_GENRES_COL_GENRE + "='"+genre+"'",
                            null, null, null, null);
                    cursor.moveToFirst();
                    // получаем _id жанра
                    genre_id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.TABLE_GENRES_COL_ID));
                    cursor.close();
                }

                values.clear();
                values.put(DatabaseHelper.TABLE_COMBINATIONS_COL_NAME_ID, name_id);
                values.put(DatabaseHelper.TABLE_COMBINATIONS_COL_GENRE_ID, genre_id);
                m_db.insert(DatabaseHelper.TABLE_COMBINATIONS, null, values);
            }

            m_db.setTransactionSuccessful();
        }
        finally
        {
            m_db.endTransaction();
        }
    }
}
