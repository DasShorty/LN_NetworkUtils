package com.laudynetwork.networkutils.api.gui.impl;

import com.laudynetwork.networkutils.api.gui.Layout;
import com.laudynetwork.networkutils.api.gui.UI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public abstract class LayoutUI extends UI {

    private Integer[] allowedSlots;

    public LayoutUI(Component displayName, int size, Layout layout, Player player) {
        super(displayName, size, player);

        switch (layout) {
            case FULL -> {
                var list = new ArrayList<Integer>();
                for (int i = 0; i < size - 1; i++) {
                    list.add(i);
                }
                allowedSlots = list.stream().toArray(Integer[]::new);
            }
            case OUTLINE -> {
                switch (size) {
                    case 9, 18 -> {
                        // ignore
                    }
                    case 27 -> {
                        allowedSlots = new Integer[]{10, 11, 12, 13, 14, 15, 16};
                    }
                    case 36 -> {
                        allowedSlots = new Integer[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25};
                    }
                    case 45 -> {
                        allowedSlots = new Integer[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
                    }
                    case 54 -> {
                        allowedSlots = new Integer[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
                    }
                }
            }
        }
    }

    public abstract void onGenerate(Player opener);


    @Override
    protected void generate() {
        onGenerate(getOpener());
    }
}
