package com.fink.sendmefun.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.fink.sendmefun.ui.adapter.Adapter;
import com.fink.sendmefun.model.History;
import com.fink.sendmefun.R;
import com.fink.sendmefun.model.message.Message;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class HistoryFragment extends Fragment {

    @Bind(R.id.listView)
    ListView listView;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, view);
        List<Message> messages = History.getInstance(getActivity()).readHistory();
        if (messages.size() == 0) {
            listView.addHeaderView(createHeader(), null, false);
        }
        listView.setAdapter(new Adapter(getActivity(), R.layout.item_layout, messages));
        return view;
    }

    private View createHeader() {
        View v = getActivity().getLayoutInflater().inflate(R.layout.header, null);
        return v;
    }
}
