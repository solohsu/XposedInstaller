package com.solohsu.android.edxp.manager.fragment;

import android.os.Bundle;
import android.widget.Toast;

import com.solohsu.android.edxp.manager.R;
import com.solohsu.android.edxp.manager.adapter.AppHelper;

import java.io.File;
import java.io.IOException;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.preference.CheckBoxPreference;
import androidx.preference.Preference;
import androidx.preference.SwitchPreference;
import androidx.recyclerview.widget.RecyclerView;
import de.robv.android.xposed.installer.WelcomeActivity;
import de.robv.android.xposed.installer.XposedApp;

import static com.solohsu.android.edxp.manager.adapter.AppHelper.setBlackWhiteListEnabled;
import static com.solohsu.android.edxp.manager.adapter.AppHelper.setBootImageDeoptEnabled;
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
        CheckBoxPreference enableDeoptPref = findPreference("enable_boot_image_deopt");
        enableDeoptPref.setChecked(AppHelper.bootImageDeoptEnabled());
        enableDeoptPref.setOnPreferenceChangeListener(
                (preference, newValue) -> setBootImageDeoptEnabled((Boolean) newValue));

        final File disableVerboseLogsFlag = new File(XposedApp.BASE_DIR + "conf/disable_verbose_log");
        CheckBoxPreference prefDisableResources = findPreference("disable_verbose_log");
        prefDisableResources.setChecked(disableVerboseLogsFlag.exists());
        prefDisableResources.setOnPreferenceChangeListener((preference, newValue) -> {
            boolean enabled = (Boolean) newValue;
            if (enabled) {
                try {
                    disableVerboseLogsFlag.createNewFile();
                } catch (IOException e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                disableVerboseLogsFlag.delete();
            }
            return (enabled == disableVerboseLogsFlag.exists());
        });
    }

}
