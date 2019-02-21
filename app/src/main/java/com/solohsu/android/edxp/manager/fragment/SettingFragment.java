package com.solohsu.android.edxp.manager.fragment;

import android.os.Bundle;

import com.solohsu.android.edxp.manager.R;
import com.solohsu.android.edxp.manager.adapter.AppHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;
import de.robv.android.xposed.installer.WelcomeActivity;

import static com.solohsu.android.edxp.manager.adapter.AppHelper.setBlackWhiteListEnabled;
import static com.solohsu.android.edxp.manager.adapter.AppHelper.setDynamicModulesEnabled;

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

        SwitchPreference dynamicModulesPref = findPreference("dynamic_modules_enabled");
        dynamicModulesPref.setChecked(AppHelper.dynamicModulesEnabled());
        dynamicModulesPref.setOnPreferenceChangeListener(
                (preference, newValue) -> setDynamicModulesEnabled((Boolean) newValue));

        SwitchPreference blackListPref = findPreference("black_white_list_enabled");
        blackListPref.setChecked(AppHelper.blackWhiteListEnabled());
        blackListPref.setOnPreferenceChangeListener(
                (preference, newValue) -> setBlackWhiteListEnabled((Boolean) newValue));
    }

}
