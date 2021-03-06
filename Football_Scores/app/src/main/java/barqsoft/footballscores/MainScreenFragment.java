package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import barqsoft.footballscores.service.myFetchService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>
{
    private final static String TAG = MainScreenFragment.class.getSimpleName();

    private ScoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] mFragmentDate = new String[1];
    private ListView mScoreList;
    private View mEmptyScoreList;

    public MainScreenFragment()
    {
    }

    private void update_scores()
    {
        Intent service_start = new Intent(getActivity(), myFetchService.class);
        getActivity().startService(service_start);
    }
    public void setFragmentDate(String date)
    {
        mFragmentDate[0] = date;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        update_scores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mScoreList = (ListView) rootView.findViewById(R.id.scores_list);
        mEmptyScoreList =  rootView.findViewById(R.id.scores_list_empty);
        mAdapter = new ScoresAdapter(getActivity(),null,0);
        mScoreList.setAdapter(mAdapter);
        getLoaderManager().initLoader(SCORES_LOADER,null,this);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle)
    {
        return new CursorLoader(getActivity(),DatabaseContract.scores_table.buildScoreWithDate(),
                null,null, mFragmentDate,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor)
    {
        //Log.v(FetchScoreTask.LOG_TAG,"loader finished");

        cursor.moveToFirst();
        if (cursor.isAfterLast()) {
            displayEmpty();
        } else {
            displayContents();
        }
        mAdapter.swapCursor(cursor);
    }

    private void displayEmpty() {
        mScoreList.setVisibility(View.GONE);
        mEmptyScoreList.setVisibility(View.VISIBLE);
    }

    private void displayContents() {
        mScoreList.setVisibility(View.VISIBLE);
        mEmptyScoreList.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader)
    {
        mAdapter.swapCursor(null);
    }


}
