package ru.popularsinger.sergeyd.popularsingers;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import ru.popularsinger.sergeyd.popularsingers.Database.dbAdapter;

public class ListItemsFragment extends ListFragment
{
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_listitems, null);
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

    public void reloadCursor(Cursor cursor)
    {
        m_adapter.changeCursor(cursor);
    }

    // интерфейс для Activity
    public interface onItemClickListener
    {
        void itemClick(long id);
    }
}
