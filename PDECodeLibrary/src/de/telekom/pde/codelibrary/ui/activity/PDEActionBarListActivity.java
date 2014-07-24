/* Deutsche Telekom AG owns the right of use concerning the following code taken from the Deutsche Telekom
 * Experience Toolbox. You can obtain a copy of the terms and conditions of the Experience Toolbox at
 * https://www.design.telekom.com/myaccount/terms-of-use/
 *
 * Copyright (c) 2013. Neuland Multimedia GmbH.
 */

package de.telekom.pde.codelibrary.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import de.telekom.pde.codelibrary.ui.PDECodeLibrary;
import de.telekom.pde.codelibrary.ui.R;
import de.telekom.pde.codelibrary.ui.elements.common.PDEDrawableDelimiter;
import de.telekom.pde.codelibrary.ui.inflater.PDEInflaterUtils;


@SuppressLint("Registered")
public class PDEActionBarListActivity extends ActionBarActivity {
    private ListView mListView;


    protected ListView getListView() {
        if (mListView == null) {
            mListView = (ListView) findViewById(R.id.list);
        }
        return mListView;
    }


    protected void setListAdapter(ListAdapter adapter) {
        getListView().setAdapter(adapter);
    }


    protected ListAdapter getListAdapter() {
        ListAdapter adapter = getListView().getAdapter();
        if (adapter instanceof HeaderViewListAdapter) {
            return ((HeaderViewListAdapter)adapter).getWrappedAdapter();
        } else {
            return adapter;
        }
    }


    private AdapterView.OnItemClickListener mOnClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View v, int position, long id)
        {
            onListItemClick((ListView)parent, v, position, id);
        }
    };


    private void ensureList() {
        if (mListView != null) {
            return;
        }
        setContentView(R.layout.pde_list_content_simple);
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PDECodeLibrary.getInstance().isAssignmentOfDefaultFontToTextViewsEnabled()) {
            // this won't work since the inflater factory is already set. See implemented in onCreateView instead.
            PDEInflaterUtils.setFontFactory(getLayoutInflater());
        }

        ensureList();

        getListView().setOnItemClickListener(mOnClickListener);
        getListView().setDivider(new PDEDrawableDelimiter());
    }


    /**
     * @brief Create TextViews already here, to be able to set telegrotesk font.
     */
    @Override
    public View onCreateView(String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = super.onCreateView(name, context, attrs);

        if (view == null) {
            view = PDEInflaterUtils.onCreateTextViewAndSetFont(name,context,attrs);
        }

        return view;
    }


    protected void onListItemClick(ListView l, View v, int position, long id) {
        // overwrite this
    }

}