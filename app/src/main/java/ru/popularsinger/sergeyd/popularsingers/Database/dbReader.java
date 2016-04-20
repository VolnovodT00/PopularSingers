package ru.popularsinger.sergeyd.popularsingers.Database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Dell on 16.04.2016.
 */
public class dbReader
{
    // экземпляр базы данных
    private SQLiteDatabase m_db = null;

    public dbReader(dbHelper helper)
    {
        // получаем базу данных на чтение
        m_db = helper.getReadableDatabase();
    }

    public Cursor getCursorByView()
    {
        String sql = "SELECT "+ dbHelper.TABLE_SINGERS+"."+ dbHelper.TABLE_SINGERS_COL_NAME_ID+" AS "+ dbHelper.QUERY_COL_ID+", "+
                dbHelper.TABLE_SINGERS_COL_NAME+" AS "+ dbHelper.QUERY_COL_NAME+", "+
                "GROUP_CONCAT("+ dbHelper.TABLE_GENRES_COL_GENRE+", ', ') AS "+ dbHelper.QUERY_COL_GENRES+", "+
                dbHelper.TABLE_SINGERS_COL_TRACKS+" AS "+ dbHelper.QUERY_COL_TRACKS+", "+
                dbHelper.TABLE_SINGERS_COL_ALBUMS+" AS "+ dbHelper.QUERY_COL_ALBUMS+", "+
                dbHelper.TABLE_SINGERS_COL_COVER_SMALL+" AS "+ dbHelper.QUERY_COL_COVER_SMALL+
                " FROM "+ dbHelper.TABLE_SINGERS+", "+ dbHelper.TABLE_GENRES+", "+ dbHelper.TABLE_COMBINATIONS+
                " WHERE "+ dbHelper.TABLE_SINGERS+"."+ dbHelper.TABLE_SINGERS_COL_NAME_ID+"="+ dbHelper.TABLE_COMBINATIONS+"."+ dbHelper.TABLE_COMBINATIONS_COL_NAME_ID+
                " AND "+ dbHelper.TABLE_GENRES+"."+ dbHelper.TABLE_GENRES_COL_ID+"="+ dbHelper.TABLE_COMBINATIONS+"."+ dbHelper.TABLE_COMBINATIONS_COL_GENRE_ID+
                " GROUP BY "+ dbHelper.TABLE_SINGERS_COL_NAME;

        Cursor cursor = m_db.rawQuery(sql, null);
        cursor.moveToFirst();

        return cursor;
    }

    public Cursor getCursorBySinger(long name_id)
    {
        String sql = "SELECT "+ dbHelper.TABLE_SINGERS_COL_NAME+" AS "+ dbHelper.QUERY_COL_NAME+", "+
                "GROUP_CONCAT("+ dbHelper.TABLE_GENRES_COL_GENRE+", ', ') AS "+ dbHelper.QUERY_COL_GENRES+", "+
                dbHelper.TABLE_SINGERS_COL_TRACKS+" AS "+ dbHelper.QUERY_COL_TRACKS+", "+
                dbHelper.TABLE_SINGERS_COL_ALBUMS+" AS "+ dbHelper.QUERY_COL_ALBUMS+", "+
                dbHelper.TABLE_SINGERS_COL_LINK+" AS "+ dbHelper.QUERY_COL_LINK+", "+
                dbHelper.TABLE_SINGERS_COL_DESCRIPTION+" AS "+ dbHelper.QUERY_COL_DESCRIPTION+", "+
                dbHelper.TABLE_SINGERS_COL_COVER_BIG+" AS "+ dbHelper.QUERY_COL_COVER_BIG+
                " FROM "+ dbHelper.TABLE_SINGERS+", "+ dbHelper.TABLE_GENRES+", "+ dbHelper.TABLE_COMBINATIONS+
                " WHERE "+ dbHelper.TABLE_SINGERS+"."+ dbHelper.TABLE_SINGERS_COL_NAME_ID+"=?"+
                " AND "+ dbHelper.TABLE_SINGERS+"."+ dbHelper.TABLE_SINGERS_COL_NAME_ID+"="+ dbHelper.TABLE_COMBINATIONS+"."+ dbHelper.TABLE_COMBINATIONS_COL_NAME_ID+
                " AND "+ dbHelper.TABLE_GENRES+"."+ dbHelper.TABLE_GENRES_COL_ID+"="+ dbHelper.TABLE_COMBINATIONS+"."+ dbHelper.TABLE_COMBINATIONS_COL_GENRE_ID+
                " GROUP BY "+ dbHelper.TABLE_SINGERS_COL_NAME;

        Cursor cursor = m_db.rawQuery(sql, new String[]{Long.toString(name_id)});
        cursor.moveToFirst();

        return cursor;
    }

    public Cursor getCursorByIndex(int index)
    {
        String sql = "SELECT "+ dbHelper.TABLE_SINGERS_COL_NAME+" AS "+ dbHelper.QUERY_COL_NAME+", "+
                "GROUP_CONCAT("+ dbHelper.TABLE_GENRES_COL_GENRE+", ', ') AS "+ dbHelper.QUERY_COL_GENRES+", "+
                dbHelper.TABLE_SINGERS_COL_TRACKS+" AS "+ dbHelper.QUERY_COL_TRACKS+", "+
                dbHelper.TABLE_SINGERS_COL_ALBUMS+" AS "+ dbHelper.QUERY_COL_ALBUMS+", "+
                dbHelper.TABLE_SINGERS_COL_LINK+" AS "+ dbHelper.QUERY_COL_LINK+", "+
                dbHelper.TABLE_SINGERS_COL_DESCRIPTION+" AS "+ dbHelper.QUERY_COL_DESCRIPTION+", "+
                dbHelper.TABLE_SINGERS_COL_COVER_BIG+" AS "+ dbHelper.QUERY_COL_COVER_BIG+
                " FROM "+ dbHelper.TABLE_SINGERS+", "+ dbHelper.TABLE_GENRES+", "+ dbHelper.TABLE_COMBINATIONS+
                " WHERE "+ dbHelper.TABLE_SINGERS+"."+ dbHelper.TABLE_SINGERS_COL_ID+"=?"+
                " AND "+ dbHelper.TABLE_SINGERS+"."+ dbHelper.TABLE_SINGERS_COL_NAME_ID+"="+ dbHelper.TABLE_COMBINATIONS+"."+ dbHelper.TABLE_COMBINATIONS_COL_NAME_ID+
                " AND "+ dbHelper.TABLE_GENRES+"."+ dbHelper.TABLE_GENRES_COL_ID+"="+ dbHelper.TABLE_COMBINATIONS+"."+ dbHelper.TABLE_COMBINATIONS_COL_GENRE_ID+
                " GROUP BY "+ dbHelper.TABLE_SINGERS_COL_NAME;

        Cursor cursor = m_db.rawQuery(sql, new String[]{Long.toString(index)});
        cursor.moveToFirst();

        return cursor;
    }
}
