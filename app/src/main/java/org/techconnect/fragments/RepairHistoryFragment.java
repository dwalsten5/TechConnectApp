package org.techconnect.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.centum.techconnect.R;
import org.techconnect.activities.SessionActivity;
import org.techconnect.adapters.SessionCursorAdapter;
import org.techconnect.sql.TCDatabaseHelper;
import org.techconnect.views.SessionListItemView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Used to facilitate accessing the repair history stored in the phone
 */
public class RepairHistoryFragment extends Fragment implements
        TextWatcher,
        LoaderManager.LoaderCallbacks<Cursor> {

    //All of the binds

    //Loader options
    private static final int SESSION_LOADER = 0;
    @Bind(R.id.progressBar)
    ProgressBar progressBar;
    @Bind(R.id.content_linearLayout)
    LinearLayout contentLinearLayout;
    @Bind(R.id.session_ListView)
    ListView sessionListView;
    private SessionCursorAdapter adapter;
    private Cursor current_adapter;
    private boolean isLoading = false;

    public RepairHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_repair_history, container, false);
        ButterKnife.bind(this, view);


        //Don't want LoaderManager because we have no control over UI when the thing is done
        getLoaderManager().initLoader(SESSION_LOADER, null, this);
        //CursorLoader loader = TCDatabaseHelper.get(this.getContext()).getActiveSessionsCursorLoader();
        adapter = new SessionCursorAdapter(this.getContext());
        sessionListView.setAdapter(adapter);
        sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SessionListItemView sessionView = ((SessionListItemView) view);
                Intent intent = new Intent(getContext(), SessionActivity.class);
                // Get the non-stub chart and open
                intent.putExtra(SessionActivity.EXTRA_SESSION,
                        sessionView.getSession()); //Maybe? Not sure if this is a good idea
                startActivity(intent);
            }
        });
        setHasOptionsMenu(true);
        Log.d("Repair History Setup", "View Initialized");

        return view;
    }

    @Override
    public void onResume() {
        Log.d("Resume Session", "Resume Fragment");
        super.onResume();
        if (getActivity() != null) {
            getActivity().setTitle(R.string.repair_history);
        }
        //onRefresh();
    }


    public void onRefresh() {
        Log.d("Resume Session", "Refresh Session List");
        if (getActivity() != null) {
            getLoaderManager().restartLoader(SESSION_LOADER, null, this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        //onRefresh();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == SESSION_LOADER) {
            Log.d("Resume Session", "Initiate Cursor Loader");
            return TCDatabaseHelper.get(this.getContext()).getActiveSessionsCursorLoader();
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        adapter.notifyDataSetChanged();
        Log.d("Resume Session", String.format("Update Adapter, %d", adapter.getCount()));

        Runnable r = new Runnable() {
            @Override
            public void run() {
                sessionListView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        };

        Handler h = new Handler();
        h.postDelayed(r, 500);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_sort);
        item.setVisible(true);
        //Initially, will have date be the initial way to sort the sessions
        item.getSubMenu().findItem(R.id.date_item).setChecked(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.isChecked())
            item.setChecked(false);
        else
            item.setChecked(true);
        return true;
    }
}
