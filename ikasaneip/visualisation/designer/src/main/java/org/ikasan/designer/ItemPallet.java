package org.ikasan.designer;


import com.vaadin.flow.component.Component;

public class ItemPallet {
    private String summary;
    private Component pallet;

    public ItemPallet(String summary, Component pallet) {
        this.summary = summary;
        this.pallet = pallet;
    }

    public String getSummary() {
        return summary;
    }

    public Component getPallet() {
        return pallet;
    }
}
