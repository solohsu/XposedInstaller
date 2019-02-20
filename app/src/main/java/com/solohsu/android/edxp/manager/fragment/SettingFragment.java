package com.solohsu.android.edxp.manager.fragment;

import android.os.Bundle;

import com.solohsu.android.edxp.manager.R;
import com.solohsu.android.edxp.manager.adapter.AppHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;
import de.robv.android.xposed.installer.WelcomeActivity;

import static com.solohsu.android.edxp.manager.adapter.AppHelper.setDynamicModules;

public class SettingFragment extends BasePreferenceFragment {

    public SettingFragment() {

    }

    public static SettingFragment newInstance() {
        return new SettingFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((WelcomeActivity) getActivity()).getSupportActionBar();
        int toolBarDp = actionBar.getHeight() == 0 ? 196 : actionBar.getHeight();
        RecyclerView listView = getListView();
        listView.setPadding(listView.getPaddingLeft(), toolBarDp + listView.getPaddingTop(),
                listView.getPaddingRight(), listView.getPaddingBottom());
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setTitle(R.string.nav_title_settings);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_settings);
        SwitchPreference dynamicModulesPref = findPreference("is_dynamic_modules");
        dynamicModulesPref.setChecked(AppHelper.isDynamicModules());
        dynamicModulesPref.setOnPreferenceChangeListener(
                (preference, newValue) -> setDynamicModules((Boolean) newValue));
    }

}
