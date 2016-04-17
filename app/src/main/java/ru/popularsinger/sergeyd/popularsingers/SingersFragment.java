package ru.popularsinger.sergeyd.popularsingers;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SingersFragment extends android.support.v4.app.ListFragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private onItemClickListener m_listener;
    private SingersAdapter m_adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // создаем адаптер
        m_adapter = new SingersAdapter(getActivity(), null);
        // назначаем адаптер списку
        setListAdapter(m_adapter);
        // создаем Loader для базы данных
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_list, null);
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        m_listener = (onItemClickListener) activity;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        m_listener.itemClick(id);
    }

    @Override
    // создание Loader'a для курсора
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        // создаем наш класс загрузки курсора
        return new SingersCursorLoader(getActivity());
    }

    @Override
    // курсор загрузился
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        // меняем его на новый в адаптере
        m_adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {

    }

    // класс загрузки курсора
    static class SingersCursorLoader extends CursorLoader
    {
        DatabaseReader m_reader;

        public SingersCursorLoader(Context context)
        {
            super(context);
            m_reader = new DatabaseReader(DatabaseHelper.getInstance(context));
        }

        @Override
        public Cursor loadInBackground()
        {
            // получаем курсор
            return m_reader.getCursorByView();
        }
    }

    public interface onItemClickListener
    {
        void itemClick(long id);
    }

}
