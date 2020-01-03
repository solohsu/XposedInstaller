package de.robv.android.xposed.installer.installation;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.solohsu.android.edxp.manager.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import de.robv.android.xposed.installer.XposedApp;
import de.robv.android.xposed.installer.util.AssetUtil;
import de.robv.android.xposed.installer.util.NavUtil;
import de.robv.android.xposed.installer.util.RootUtil;
import de.robv.android.xposed.installer.util.json.FrameWorkTabs;
import de.robv.android.xposed.installer.util.json.JSONUtils;
import de.robv.android.xposed.installer.util.json.Sandhook;
import de.robv.android.xposed.installer.util.json.Yahfa;

public class AdvancedInstallerFragment extends Fragment {

    private static ViewPager mPager;
    private TabLayout mTabLayout;
    private RootUtil mRootUtil = new RootUtil();
    private TabsAdapter tabsAdapter;
    private String JSON_DATA = "";

    public static void gotoPage(int page) {
        mPager.setCurrentItem(page);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_advanced_installer, container, false);
        mPager = view.findViewById(R.id.pager);
        mTabLayout = view.findViewById(R.id.tab_layout);

        tabsAdapter = new TabsAdapter(getChildFragmentManager());
        tabsAdapter.notifyDataSetChanged();
        mPager.setAdapter(tabsAdapter);
        mTabLayout.setupWithViewPager(mPager);

        setHasOptionsMenu(true);
        new TabsLoader().execute();

        if (!XposedApp.getPreferences().getBoolean("hide_install_warning", false)) {
            final View dontShowAgainView = inflater.inflate(R.layout.dialog_install_warning, null);

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.install_warning_title)
                    .setMessage(R.string.install_warning)
                    .setNeutralButton(R.string.dont_show_again, (dialog, which) -> {
                        XposedApp.getPreferences().edit().putBoolean("hide_install_warning", true).apply();
                        dialog.dismiss();
                    })
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.dismiss()).show();
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //mTabLayout.setBackgroundColor(XposedApp.getColor(getContext()));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_installer, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dexopt_all:
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.areyousure)
                        .setMessage(R.string.take_while_cannot_resore)
                        .setPositiveButton(android.R.string.yes, (dg, which) -> {
                            new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                                    .title(R.string.dexopt_now)
                                    .content(R.string.this_may_take_a_while)
                                    .progress(true, 0)
                                    .cancelable(false)
                                    .showListener(dialog -> new Thread("dexopt") {
                                        @Override
                                        public void run() {
                                            RootUtil rootUtil = new RootUtil();
                                            if (!rootUtil.startShell()) {
                                                dialog.dismiss();
                                                NavUtil.showMessage(Objects.requireNonNull(getActivity()), getString(R.string.root_failed));
                                                return;
                                            }

                                            rootUtil.execute("cmd package bg-dexopt-job", new ArrayList<>());

                                            dialog.dismiss();
                                            XposedApp.runOnUiThread(() -> Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_LONG).show());
                                        }
                                    }.start()).show();
                            dg.dismiss();
                        }).show();
                break;
            case R.id.speed_all:
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle(R.string.areyousure)
                        .setMessage(R.string.take_while_cannot_resore)
                        .setPositiveButton(android.R.string.yes, (dg, which) -> {
                            new MaterialDialog.Builder(Objects.requireNonNull(getActivity()))
                                    .title(R.string.speed_now)
                                    .content(R.string.this_may_take_a_while)
                                    .progress(true, 0)
                                    .cancelable(false)
                                    .showListener(dialog -> new Thread("dex2oat") {
                                        @Override
                                        public void run() {
                                            RootUtil rootUtil = new RootUtil();
                                            if (!rootUtil.startShell()) {
                                                dialog.dismiss();
                                                NavUtil.showMessage(Objects.requireNonNull(getActivity()), getString(R.string.root_failed));
                                                return;
                                            }

                                            rootUtil.execute("cmd package compile -m speed -a", new ArrayList<>());

                                            dialog.dismiss();
                                            XposedApp.runOnUiThread(() -> Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_LONG).show());
                                        }
                                    }.start()).show();
                            dg.dismiss();
                        }).show();
                break;
            case R.id.reboot:
                if (XposedApp.getPreferences().getBoolean("confirm_reboots", true)) {
                    areYouSure(R.string.reboot, 0);
                } else {
                    reboot(null);
                }
                break;
            case R.id.soft_reboot:
                if (XposedApp.getPreferences().getBoolean("confirm_reboots", true)) {
                    areYouSure(R.string.soft_reboot, 1);
                } else {
                    softReboot();
                }
                break;
            case R.id.reboot_recovery:
                if (XposedApp.getPreferences().getBoolean("confirm_reboots", true)) {
                    areYouSure(R.string.reboot_recovery, 2);
                } else {
                    reboot("recovery");
                }
                break;
            case R.id.reboot_bootloader:
                if (XposedApp.getPreferences().getBoolean("confirm_reboots", true)) {
                    areYouSure(R.string.reboot_bootloader, 3);
                } else {
                    reboot("bootloader");
                }
                break;
            case R.id.reboot_download:
                if (XposedApp.getPreferences().getBoolean("confirm_reboots", true)) {
                    areYouSure(R.string.reboot_download, 4);
                } else {
                    reboot("download");
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public String getJSON_DATA() {
        return JSON_DATA;
    }

    public void setJSON_DATA(String json) {
        this.JSON_DATA = json;
        tabsAdapter.notifyDataSetChanged();
        mPager.setAdapter(tabsAdapter);
        mTabLayout.setupWithViewPager(mPager);
    }

    private boolean startShell() {
        if (mRootUtil.startShell())
            return true;

        showAlert(getString(R.string.root_failed));
        return false;
    }

    private void areYouSure(int contentTextId, int mode) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.areyousure)
                .setMessage(contentTextId)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    switch (mode) {
                        case 0:
                            reboot(null);
                            break;
                        case 1:
                            softReboot();
                            break;
                        case 2:
                            reboot("recovery");
                            break;
                        case 3:
                            reboot("bootloader");
                            break;
                        case 4:
                            reboot("download");
                            break;
                    }
                    dialog.dismiss();
                }).show();


//        new MaterialDialog.Builder(getActivity()).title(R.string.areyousure)
//                .content(contentTextId)
//                .iconAttr(android.R.attr.alertDialogIcon)
//                .positiveText(android.R.string.yes)
//                .negativeText(android.R.string.no).callback(yesHandler).show();
    }

    private void showAlert(final String result) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Objects.requireNonNull(getActivity()).runOnUiThread(() -> showAlert(result));
            return;
        }

        new MaterialAlertDialogBuilder(requireContext()).setMessage(result).setPositiveButton(android.R.string.ok, ((dialog, which) -> dialog.dismiss()));
    }

    private void softReboot() {
        if (!startShell())
            return;

        List<String> messages = new LinkedList<>();
        if (mRootUtil.execute("setprop ctl.restart surfaceflinger; setprop ctl.restart zygote", messages) != 0) {
            messages.add("");
            messages.add(getString(R.string.reboot_failed));
            showAlert(TextUtils.join("\n", messages).trim());
        }
    }

    private void reboot(String mode) {
        if (!startShell())
            return;

        List<String> messages = new LinkedList<>();

        String command = "reboot";
        if (mode != null) {
            command += " " + mode;
            if (mode.equals("recovery"))
                // create a flag used by some kernels to boot into recovery
                mRootUtil.executeWithBusybox("touch /cache/recovery/boot", messages);
        }

        if (mRootUtil.executeWithBusybox(command, messages) != 0) {
            messages.add("");
            messages.add(getString(R.string.reboot_failed));
            showAlert(TextUtils.join("\n", messages).trim());
        }
        AssetUtil.removeBusybox();
    }

    @SuppressLint("StaticFieldLeak")
    public class TabsLoader extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                String s = JSONUtils.getFileContent(JSONUtils.JSON_LINK);
                final FrameWorkTabs tabs = new Gson().fromJson(s, FrameWorkTabs.class);
                tabsAdapter.addFragment("Yahfa", new AntiViolenceFragment(tabs, Yahfa.class));
                tabsAdapter.addFragment("Sandhook", new AntiViolenceFragment(tabs, Sandhook.class));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            try {
                tabsAdapter.notifyDataSetChanged();
            } catch (Exception ignored) {
            }
        }
    }

    public class TabsAdapter extends FragmentPagerAdapter {

        private final ArrayList<String> titles = new ArrayList<>();
        private final ArrayList<Fragment> listFragment = new ArrayList<>();

        TabsAdapter(FragmentManager mgr) {
            super(mgr);
            addFragment(getString(R.string.status), new StatusInstallerFragment());
        }

        void addFragment(String title, Fragment fragment) {
            titles.add(title);
            listFragment.add(fragment);
        }

        @Override
        public int getCount() {
            return listFragment.size();
        }

        @Override
        public Fragment getItem(int position) {
            return listFragment.get(position);
        }

        @Override
        public String getPageTitle(int position) {
            return titles.get(position);
        }
    }


}
