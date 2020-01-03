package de.robv.android.xposed.installer.installation;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.solohsu.android.edxp.manager.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.robv.android.xposed.installer.util.json.FrameWorkTabs;
import de.robv.android.xposed.installer.util.json.Sandhook;
import de.robv.android.xposed.installer.util.json.Yahfa;

public class AntiViolenceFragment extends Fragment {

    private View rootView;
    private FrameWorkTabs tabs;
    private List<Yahfa> yahfas;
    private List<Sandhook> sandhooks;
    private Class type;
    private int selected = 0;

    public AntiViolenceFragment(FrameWorkTabs tabs, Class type) {
        this.type = type;
        this.tabs = tabs;
        yahfas = tabs.getYahfa();
        sandhooks = tabs.getSandhook();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.anti_violence_layout, container, false);
        }

        Spinner spinner = rootView.findViewById(R.id.chooserInstallers);
        MaterialButton install = rootView.findViewById(R.id.btnInstall);

        ArrayAdapter spinnerAdapter = null;

        if (type == Yahfa.class) {
            if (yahfas.size() > 0) {
                List<String> strings = new ArrayList<>();
                for (Yahfa yahfa : yahfas) {
                    strings.add(yahfa.getName() + "-" + yahfa.getVersion());
                }
                spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, strings);
            }
        } else if (type == Sandhook.class) {
            if (sandhooks.size() > 0) {
                List<String> strings = new ArrayList<>();
                for (Sandhook sandhook : sandhooks) {
                    strings.add(sandhook.getName() + "-" + sandhook.getVersion());
                }
                spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, strings);
            }
        }
        if (spinnerAdapter != null) {
            spinner.setAdapter(spinnerAdapter);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        install.setOnClickListener(v -> {
            if (type == Yahfa.class) {
                copy(yahfas.get(selected).getLink());
                Toast.makeText(requireContext(), "已将下载链接复制到剪贴板", Toast.LENGTH_SHORT).show();
            } else if (type == Sandhook.class) {
                copy(sandhooks.get(selected).getLink());
                Toast.makeText(requireContext(), "已将下载链接复制到剪贴板", Toast.LENGTH_SHORT).show();
            }
        });
        return rootView;
    }

    private boolean copy(String copyStr) {
        try {
            //获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            // 将ClipData内容放到系统剪贴板里。
            Objects.requireNonNull(cm).setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
