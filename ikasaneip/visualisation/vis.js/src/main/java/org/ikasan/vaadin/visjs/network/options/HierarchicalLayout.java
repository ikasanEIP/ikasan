package org.ikasan.vaadin.visjs.network.options;

/**
 * Created by roshans on 11/4/14.
 * Added new options treeSpacing, blockshifting, edgeMinimization, parentCentralization by Martin Prause 5.8.2017
 */
public class HierarchicalLayout {

    private boolean enabled = true;
    private int levelSeparation = 100;
    private int nodeSpacing = 100;
    private Direction direction = Direction.UD;
    private LayoutStyle layout = LayoutStyle.hubsize;
    private int treeSpacing=200;
    private boolean blockShifting=true;
    private boolean edgeMinimization=true;
    private boolean  parentCentralization=true;
      
    
    public int getTreeSpacing() {
		return treeSpacing;
	}

	public void setTreeSpacing(int treeSpacing) {
		this.treeSpacing = treeSpacing;
	}

	public boolean isBlockShifting() {
		return blockShifting;
	}

	public void setBlockShifting(boolean blockShifting) {
		this.blockShifting = blockShifting;
	}

	public boolean isEdgeMinimization() {
		return edgeMinimization;
	}

	public void setEdgeMinimization(boolean edgeMinimization) {
		this.edgeMinimization = edgeMinimization;
	}

	public boolean isParentCentralization() {
		return parentCentralization;
	}

	public void setParentCentralization(boolean parentCentralization) {
		this.parentCentralization = parentCentralization;
	}

	public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getLevelSeparation() {
        return levelSeparation;
    }

    public void setLevelSeparation(int levelSeparation) {
        this.levelSeparation = levelSeparation;
    }

    public int getNodeSpacing() {
        return nodeSpacing;
    }

    public void setNodeSpacing(int nodeSpacing) {
        this.nodeSpacing = nodeSpacing;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public LayoutStyle getLayout() {
        return layout;
    }

    public void setLayout(LayoutStyle layout) {
        this.layout = layout;
    }

    public static enum Direction {
        UD,
        DU,
        LR,
        RL;
    }

    public static enum  LayoutStyle{
        hubsize,
        direction;
    }
}
