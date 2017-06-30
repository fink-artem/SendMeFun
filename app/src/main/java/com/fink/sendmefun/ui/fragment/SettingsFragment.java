package com.fink.sendmefun.ui.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.fink.sendmefun.model.History;
import com.fink.sendmefun.model.Mode;
import com.fink.sendmefun.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsFragment extends Fragment {

    @Bind(R.id.history_check_box)
    CheckBox checkBox;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        checkBox.setChecked(Mode.getInstance(getActivity()).getWriteMode());
        return view;
    }

    @OnClick(R.id.history_check_box)
    void onClickCheckBox(View view) {
        Mode.getInstance(getActivity()).setWriteMode(checkBox.isChecked());
    }

    @OnClick(R.id.clear_history_button)
    void OnClickButton(View view){
        History.getInstance(getActivity()).clearHistory();
    }
}
