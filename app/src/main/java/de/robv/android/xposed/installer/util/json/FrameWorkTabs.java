package de.robv.android.xposed.installer.util.json;

import java.util.ArrayList;
import java.util.List;

public class FrameWorkTabs {
    private List<Yahfa> yahfa = new ArrayList<>();

    private List<Sandhook> sandhook = new ArrayList<>();

    public void setYahfa(List<Yahfa> yahfa) {
        this.yahfa = yahfa;
    }

    public List<Yahfa> getYahfa() {
        return this.yahfa;
    }

    public void setSandhook(List<Sandhook> sandhook) {
        this.sandhook = sandhook;
    }

    public List<Sandhook> getSandhook() {
        return this.sandhook;
    }

}