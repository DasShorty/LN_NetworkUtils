package com.laudynetwork.networkutils.api.gui.impl;

import com.laudynetwork.networkutils.api.gui.GUIItem;
import com.laudynetwork.networkutils.api.gui.Layout;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class PaginationUI extends LayoutUI {
    private final int itemsPerPage;

    public PaginationUI(Component displayName, int size, Layout layout, Player player, int itemsPerPage) {
        super(displayName, size, layout, player);
        this.itemsPerPage = itemsPerPage;
    }

    public List<GUIItem> getPage(List<GUIItem> itemStacks, int page) {
        return paginate(itemStacks, 2, itemsPerPage);
    }

    public void setItems(List<GUIItem> items) {
        items.forEach(this::set);
    }


    /**
     * returns a view (not a new list) of the sourceList for the
     * range based on page and pageSize
     *
     * @param sourceList
     * @param page,      page number should start from 1
     * @param pageSize
     * @return custom error can be given instead of returning emptyList
     */
    private <T> List<T> paginate(List<T> sourceList, int page, int pageSize) {
        if (pageSize <= 0 || page <= 0) {
            throw new IllegalArgumentException("invalid page size: " + pageSize);
        }

        int fromIndex = (page - 1) * pageSize;
        if (sourceList == null || sourceList.size() <= fromIndex) {
            return Collections.emptyList();
        }

        // toIndex exclusive
        return sourceList.subList(fromIndex, Math.min(fromIndex + pageSize, sourceList.size()));
    }

    @Override
    public void onGenerate(Player opener) {

    }

    @Override
    protected void generate() {

    }
}
