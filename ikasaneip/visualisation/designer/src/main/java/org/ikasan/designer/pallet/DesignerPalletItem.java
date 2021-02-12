package org.ikasan.designer.pallet;

import com.vaadin.flow.component.html.Image;

import java.util.UUID;

public class DesignerPalletItem extends Image {
    private String identifier;
    private DesignerPalletItemType designerPalletItemType;
    private CanvasAddAction canvasAddAction;
    private int itemWidth;
    private int itemHeight;

    /**
     * Constructor
     *
     * @param imageSrc
     * @param designerPalletItemType
     * @param canvasAddAction
     * @param itemWidth
     * @param itemHeight
     */
    public DesignerPalletItem(String imageSrc, DesignerPalletItemType designerPalletItemType, CanvasAddAction canvasAddAction, int itemWidth, int itemHeight) {
        super(imageSrc, "");
        this.identifier = UUID.randomUUID().toString();
        this.designerPalletItemType = designerPalletItemType;
        this.canvasAddAction = canvasAddAction;
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
    }

    public void executeCanvasAddAction() {
        if(this.canvasAddAction != null) {
            this.canvasAddAction.execute();
        }
    }

    public DesignerPalletItemType getDesignerPalletItemType() {
        return designerPalletItemType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public int getItemHeight() {
        return itemHeight;
    }
}
