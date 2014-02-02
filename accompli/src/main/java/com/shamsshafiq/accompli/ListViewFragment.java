package com.shamsshafiq.accompli;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shamsshafiq.accompli.db.AcompliSQLiteOpenHelper;
import com.shamsshafiq.accompli.events.DatabaseModifiedEvent;

import de.greenrobot.event.EventBus;

public class ListViewFragment extends Fragment {

    private DataAdapter mAdapter;
    private AcompliSQLiteOpenHelper mSqLiteOpenHelper;

    public ListViewFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_listview, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mSqLiteOpenHelper = new AcompliSQLiteOpenHelper(getActivity().getApplicationContext());

        mAdapter = new DataAdapter(getActivity().getApplicationContext(), mSqLiteOpenHelper.getAllEntries());
                ((ListView) view.findViewById(R.id.list_view)).setAdapter(mAdapter);

        showNoEntriesOverlayIfApplicable();

        view.findViewById(R.id.button_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSqLiteOpenHelper.deleteAllEntires();
                EventBus.getDefault().post(new DatabaseModifiedEvent());
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    private void showNoEntriesOverlayIfApplicable() {
        if (mAdapter.getCount() > 0) {
            getView().findViewById(R.id.text_empty).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.text_empty).setVisibility(View.VISIBLE);
        }
    }

    public void onEventMainThread(DatabaseModifiedEvent event) {
        if (mAdapter != null) {
            if (mSqLiteOpenHelper == null) {
                mSqLiteOpenHelper = new AcompliSQLiteOpenHelper(getActivity().getApplicationContext());
            }
            mAdapter.updateEntries(mSqLiteOpenHelper.getAllEntries());
            showNoEntriesOverlayIfApplicable();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
