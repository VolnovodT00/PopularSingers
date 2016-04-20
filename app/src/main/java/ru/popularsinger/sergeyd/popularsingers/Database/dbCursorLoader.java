package ru.popularsinger.sergeyd.popularsingers.Database;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;

/**
 * Created by sergeyd on 04/20/2016.
 */
public class dbCursorLoader extends CursorLoader
{
    dbReader m_reader;

    public dbCursorLoader(Context context)
    {
        super(context);
        m_reader = new dbReader(dbHelper.getInstance(context));
    }

    @Override
    public Cursor loadInBackground()
    {
        // получаем курсор
        return m_reader.getCursorByView();
    }
}
