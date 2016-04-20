package ru.popularsinger.sergeyd.popularsingers;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ru.popularsinger.sergeyd.popularsingers.Database.dbAdapter;
import ru.popularsinger.sergeyd.popularsingers.Database.dbCursorLoader;
import ru.popularsinger.sergeyd.popularsingers.Database.dbTaskLoader;

public class SingersFragment extends ListFragment implements LoaderManager.LoaderCallbacks
{
    private final int CURSOR_LOADER_IND = 0;
    private final int DB_LOADER_IND = 1;

    private onItemClickListener m_listener;
    private dbAdapter m_adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        // создаем адаптер
        m_adapter = new dbAdapter(getActivity(), null);
        // назначаем адаптер списку
        setListAdapter(m_adapter);
        // создаем Loader для базы данных
        getActivity().getSupportLoaderManager().initLoader(DB_LOADER_IND, null, this);
        getActivity().getSupportLoaderManager().initLoader(CURSOR_LOADER_IND, null, this);
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
    // создание Loader'a
    public Loader onCreateLoader(int id, Bundle args)
    {
        Loader loader = null;
        switch ( id )
        {
            case CURSOR_LOADER_IND: // Loader загрузки курсора
                Log.d("popularsingers", "onCreateLoader: dbCursorLoader");
                loader = new dbCursorLoader(getActivity());
                break;
            case DB_LOADER_IND: // Loader загрузки BD
                Log.d("popularsingers", "onCreateLoader: dbTaskLoader");
                loader = new dbTaskLoader(getActivity());
        }

        // создаем наш класс загрузки курсора
        return loader;
    }

    @Override
    // окночание загрузки
    public void onLoadFinished(Loader loader, Object data)
    {
        switch ( loader.getId() )
        {
            case CURSOR_LOADER_IND: // Loader загрузки курсора
                Log.d("popularsingers", "onLoadFinished: dbCursorLoader");
                // подменям курор в адапетер
                m_adapter.changeCursor((Cursor) data);
                break;
            case DB_LOADER_IND: // Loader загрузки BD
                Log.d("popularsingers", "onLoadFinished: dbTaskLoader " + data.toString());// обновляем курсор
                getActivity().getSupportLoaderManager().getLoader(CURSOR_LOADER_IND).forceLoad();
                break;
        }
    }

    @Override
    // уничтожение Loader'a
    public void onLoaderReset(Loader loader)
    {

    }

    // интерфейс для Activity
    public interface onItemClickListener
    {
        void itemClick(long id);
    }
}
