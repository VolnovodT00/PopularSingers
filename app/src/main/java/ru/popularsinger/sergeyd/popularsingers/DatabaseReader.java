package ru.popularsinger.sergeyd.popularsingers;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Dell on 16.04.2016.
 */
public class DatabaseReader
{
    // экземпляр базы данных
    private SQLiteDatabase m_db = null;

    public DatabaseReader(DatabaseHelper helper)
    {
        // получаем базу данных на чтение
        m_db = helper.getReadableDatabase();
    }

    public Cursor getCursorByView()
    {
        String sql = "SELECT "+DatabaseHelper.TABLE_SINGERS+"."+DatabaseHelper.TABLE_SINGERS_COL_NAME_ID+" AS "+DatabaseHelper.QUERY_COL_ID+", "+
                DatabaseHelper.TABLE_SINGERS_COL_NAME+" AS "+DatabaseHelper.QUERY_COL_NAME+", "+
                "GROUP_CONCAT("+DatabaseHelper.TABLE_GENRES_COL_GENRE+", ', ') AS "+DatabaseHelper.QUERY_COL_GENRES+", "+
                DatabaseHelper.TABLE_SINGERS_COL_TRACKS+" AS "+DatabaseHelper.QUERY_COL_TRACKS+", "+
                DatabaseHelper.TABLE_SINGERS_COL_ALBUMS+" AS "+DatabaseHelper.QUERY_COL_ALBUMS+", "+
                DatabaseHelper.TABLE_SINGERS_COL_COVER_SMALL+" AS "+DatabaseHelper.QUERY_COL_COVER_SMALL+
                " FROM "+DatabaseHelper.TABLE_SINGERS+", "+DatabaseHelper.TABLE_GENRES+", "+DatabaseHelper.TABLE_COMBINATIONS+
                " WHERE "+DatabaseHelper.TABLE_SINGERS+"."+DatabaseHelper.TABLE_SINGERS_COL_NAME_ID+"="+DatabaseHelper.TABLE_COMBINATIONS+"."+DatabaseHelper.TABLE_COMBINATIONS_COL_NAME_ID+
                " AND "+DatabaseHelper.TABLE_GENRES+"."+DatabaseHelper.TABLE_GENRES_COL_ID+"="+DatabaseHelper.TABLE_COMBINATIONS+"."+DatabaseHelper.TABLE_COMBINATIONS_COL_GENRE_ID+
                " GROUP BY "+DatabaseHelper.TABLE_SINGERS_COL_NAME;

        Cursor cursor = m_db.rawQuery(sql, null);
        cursor.moveToFirst();

        return cursor;
    }

    public Cursor getCursorBySinger(long name_id)
    {
        String sql = "SELECT "+DatabaseHelper.TABLE_SINGERS_COL_NAME+" AS "+DatabaseHelper.QUERY_COL_NAME+", "+
                "GROUP_CONCAT("+DatabaseHelper.TABLE_GENRES_COL_GENRE+", ', ') AS "+DatabaseHelper.QUERY_COL_GENRES+", "+
                DatabaseHelper.TABLE_SINGERS_COL_TRACKS+" AS "+DatabaseHelper.QUERY_COL_TRACKS+", "+
                DatabaseHelper.TABLE_SINGERS_COL_ALBUMS+" AS "+DatabaseHelper.QUERY_COL_ALBUMS+", "+
                DatabaseHelper.TABLE_SINGERS_COL_LINK+" AS "+DatabaseHelper.QUERY_COL_LINK+", "+
                DatabaseHelper.TABLE_SINGERS_COL_DESCRIPTION+" AS "+DatabaseHelper.QUERY_COL_DESCRIPTION+", "+
                DatabaseHelper.TABLE_SINGERS_COL_COVER_BIG+" AS "+DatabaseHelper.QUERY_COL_COVER_BIG+
                " FROM "+DatabaseHelper.TABLE_SINGERS+", "+DatabaseHelper.TABLE_GENRES+", "+DatabaseHelper.TABLE_COMBINATIONS+
                " WHERE "+DatabaseHelper.TABLE_SINGERS+"."+DatabaseHelper.TABLE_SINGERS_COL_NAME_ID+"=?"+
                " AND "+DatabaseHelper.TABLE_SINGERS+"."+DatabaseHelper.TABLE_SINGERS_COL_NAME_ID+"="+DatabaseHelper.TABLE_COMBINATIONS+"."+DatabaseHelper.TABLE_COMBINATIONS_COL_NAME_ID+
                " AND "+DatabaseHelper.TABLE_GENRES+"."+DatabaseHelper.TABLE_GENRES_COL_ID+"="+DatabaseHelper.TABLE_COMBINATIONS+"."+DatabaseHelper.TABLE_COMBINATIONS_COL_GENRE_ID+
                " GROUP BY "+DatabaseHelper.TABLE_SINGERS_COL_NAME;

        Cursor cursor = m_db.rawQuery(sql, new String[]{Long.toString(name_id)});
        cursor.moveToFirst();

        return cursor;
    }

    public Cursor getCursorByIndex(int index)
    {
        String sql = "SELECT "+DatabaseHelper.TABLE_SINGERS_COL_NAME+" AS "+DatabaseHelper.QUERY_COL_NAME+", "+
                "GROUP_CONCAT("+DatabaseHelper.TABLE_GENRES_COL_GENRE+", ', ') AS "+DatabaseHelper.QUERY_COL_GENRES+", "+
                DatabaseHelper.TABLE_SINGERS_COL_TRACKS+" AS "+DatabaseHelper.QUERY_COL_TRACKS+", "+
                DatabaseHelper.TABLE_SINGERS_COL_ALBUMS+" AS "+DatabaseHelper.QUERY_COL_ALBUMS+", "+
                DatabaseHelper.TABLE_SINGERS_COL_LINK+" AS "+DatabaseHelper.QUERY_COL_LINK+", "+
                DatabaseHelper.TABLE_SINGERS_COL_DESCRIPTION+" AS "+DatabaseHelper.QUERY_COL_DESCRIPTION+", "+
                DatabaseHelper.TABLE_SINGERS_COL_COVER_BIG+" AS "+DatabaseHelper.QUERY_COL_COVER_BIG+
                " FROM "+DatabaseHelper.TABLE_SINGERS+", "+DatabaseHelper.TABLE_GENRES+", "+DatabaseHelper.TABLE_COMBINATIONS+
                " WHERE "+DatabaseHelper.TABLE_SINGERS+"."+DatabaseHelper.TABLE_SINGERS_COL_ID+"=?"+
                " AND "+DatabaseHelper.TABLE_SINGERS+"."+DatabaseHelper.TABLE_SINGERS_COL_NAME_ID+"="+DatabaseHelper.TABLE_COMBINATIONS+"."+DatabaseHelper.TABLE_COMBINATIONS_COL_NAME_ID+
                " AND "+DatabaseHelper.TABLE_GENRES+"."+DatabaseHelper.TABLE_GENRES_COL_ID+"="+DatabaseHelper.TABLE_COMBINATIONS+"."+DatabaseHelper.TABLE_COMBINATIONS_COL_GENRE_ID+
                " GROUP BY "+DatabaseHelper.TABLE_SINGERS_COL_NAME;

        Cursor cursor = m_db.rawQuery(sql, new String[]{Long.toString(index)});
        cursor.moveToFirst();

        return cursor;
    }
}
