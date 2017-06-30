package com.fink.sendmefun.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.fink.sendmefun.BusProvider;
import com.fink.sendmefun.R;
import com.fink.sendmefun.model.Event;
import com.fink.sendmefun.model.message.Message;
import com.fink.sendmefun.model.message.SentMessage;
import com.fink.sendmefun.net.Client;
import com.fink.sendmefun.ui.adapter.Adapter;
import com.squareup.otto.Subscribe;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BaseFragment extends Fragment {

    private static final String MESSAGE = "message";
    private static final int ATTEMP_NUMBER = 3;
    private static final int TIMEOUT = 3;
    private List<Message> messages;

    @Bind(R.id.editText)
    EditText editText;
    @Bind(R.id.listView)
    ListView listView;

    public static BaseFragment newInstance(List<Message> messages) {
        BaseFragment baseFragment = new BaseFragment();
        Bundle data = new Bundle();
        data.putSerializable(MESSAGE, (Serializable) messages);
        baseFragment.setArguments(data);
        return baseFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, container, false);
        ButterKnife.bind(this, view);
        Bundle data = getArguments();
        messages = (List<Message>) data.getSerializable(MESSAGE);
        listView.setAdapter(new Adapter(getActivity(), R.layout.item_layout, messages));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @OnClick(R.id.send_button)
    void onClick(View view) {
        if (!editText.getText().toString().isEmpty()) {
            SentMessage message = new SentMessage(editText.getText().toString());
            editText.getText().clear();
            message.setStatus(SentMessage.PROGRESS);
            BusProvider.getInstance().post(message);
            Observable.create(Client.createClient(message, getActivity()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .timeout(TIMEOUT, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .retry(ATTEMP_NUMBER)
                    .onErrorReturn(Client.createRequestErrorHandler(message))
                    .subscribe(Client.createClientSubscriber());
        }
    }

    @Subscribe
    public void addMessage(Event event) {
        listView.setAdapter(new Adapter(getActivity(), R.layout.item_layout, messages));
        listView.setSelection(messages.size() - 1);
    }
}
