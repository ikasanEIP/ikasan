package org.ikasan.vaadin.visjs.network;

import org.ikasan.vaadin.visjs.network.options.nodes.Nodes;

public class Node extends Nodes
{
    private String id;
    private String edgeColour = "rgba(0, 255, 0, 0.8)";
    private String fillColour = "rgba(0, 255, 0, 0.2)";
    private String wiretapFoundStatus = NodeFoundStatus.EMPTY;
    private String wiretapFoundImage = null;
    private String errorFoundStatus = NodeFoundStatus.EMPTY;
    private String errorFoundImage = null;
    private String exclusionFoundStatus = NodeFoundStatus.EMPTY;
    private String exclusionFoundImage = null;
    private String replayFoundStatus = NodeFoundStatus.EMPTY;
    private String replayFoundImage = null;
    private String wiretapBeforeStatus = NodeFoundStatus.EMPTY;
    private String wiretapBeforeImage = null;
    private String wiretapAfterStatus = NodeFoundStatus.EMPTY;
    private String wiretapAfterImage = null;
    private String logWiretapBeforeStatus = NodeFoundStatus.EMPTY;
    private String logWiretapBeforeImage = null;
    private String logWiretapAfterStatus = NodeFoundStatus.EMPTY;
    private String logWiretapAfterImage = null;

    protected double wiretapFoundImageX = -80;
    protected double wiretapFoundImageY = -40;
    protected int wiretapFoundImageH = 30;
    protected int wiretapFoundImageW = 30;

    protected double errorFoundImageX = -80;
    protected double errorFoundImageY = 10;
    protected int errorFoundImageH = 30;
    protected int errorFoundImageW = 30;

    protected double exclusionFoundImageX = 50;
    protected double exclusionFoundImageY = -40;
    protected int exclusionFoundImageH = 30;
    protected int exclusionFoundImageW = 30;

    protected double replayFoundImageX = 50;
    protected double replayFoundImageY = 10;
    protected int replayFoundImageH = 30;
    protected int replayFoundImageW = 30;

    protected double wiretapBeforeImageX = -80;
    protected double wiretapBeforeImageY = -40;
    protected int wiretapBeforeImageH = 30;
    protected int wiretapBeforeImageW = 30;

    protected double wiretapAfterImageX = 50;
    protected double wiretapAfterImageY = -40;
    protected int wiretapAfterImageH = 30;
    protected int wiretapAfterImageW = 30;

    protected double logWiretapBeforeImageX = -80;
    protected double logWiretapBeforeImageY = -80;
    protected int logWiretapBeforeImageH = 30;
    protected int logWiretapBeforeImageW = 30;

    protected double logWiretapAfterImageX = 50;
    protected double logWiretapAfterImageY = -80;
    protected int logWiretapAfterImageH = 30;
    protected int logWiretapAfterImageW = 30;


    public Node()
    {
    }

    public Node(final String id)
    {
        this.id = id;
    }

    public Node(final String id, Builder builder)
    {
        super(builder);
        this.id = id;
    }

    public Node(String id, String label)
    {
        this.id = id;
        this.setLabel(label);
    }

    public Node(String id, String label, final Builder builder)
    {
        super(builder);
        this.id = id;
        this.setLabel(label);
    }

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getEdgeColour()
    {
        return edgeColour;
    }

    public void setEdgeColour(String edgeColour)
    {
        this.edgeColour = edgeColour;
    }

    public String getFillColour()
    {
        return fillColour;
    }

    public void setFillColour(String fillColour)
    {
        this.fillColour = fillColour;
    }

    public String getWiretapFoundStatus()
    {
        return wiretapFoundStatus;
    }

    public void setWiretapFoundStatus(String wiretapFoundStatus)
    {
        this.wiretapFoundStatus = wiretapFoundStatus;
    }

    public String getWiretapFoundImage()
    {
        return wiretapFoundImage;
    }

    public void setWiretapFoundImage(String wiretapFoundImage)
    {
        this.wiretapFoundImage = wiretapFoundImage;
    }

    public String getErrorFoundStatus() {
        return errorFoundStatus;
    }

    public void setErrorFoundStatus(String errorFoundStatus) {
        this.errorFoundStatus = errorFoundStatus;
    }

    public String getErrorFoundImage() {
        return errorFoundImage;
    }

    public void setErrorFoundImage(String errorFoundImage) {
        this.errorFoundImage = errorFoundImage;
    }

    public String getExclusionFoundStatus() {
        return exclusionFoundStatus;
    }

    public void setExclusionFoundStatus(String exclusionFoundStatus) {
        this.exclusionFoundStatus = exclusionFoundStatus;
    }

    public String getExclusionFoundImage() {
        return exclusionFoundImage;
    }

    public void setExclusionFoundImage(String exclusionFoundImage) {
        this.exclusionFoundImage = exclusionFoundImage;
    }

    public String getReplayFoundStatus() {
        return replayFoundStatus;
    }

    public void setReplayFoundStatus(String replayFoundStatus) {
        this.replayFoundStatus = replayFoundStatus;
    }

    public String getReplayFoundImage() {
        return replayFoundImage;
    }

    public void setReplayFoundImage(String replayFoundImage) {
        this.replayFoundImage = replayFoundImage;
    }

    public double getWiretapFoundImageX() {
        return wiretapFoundImageX;
    }

    public void setWiretapFoundImageX(double wiretapFoundImageX) {
        this.wiretapFoundImageX = wiretapFoundImageX;
    }

    public double getWiretapFoundImageY() {
        return wiretapFoundImageY;
    }

    public void setWiretapFoundImageY(double wiretapFoundImageY) {
        this.wiretapFoundImageY = wiretapFoundImageY;
    }

    public int getWiretapFoundImageH() {
        return wiretapFoundImageH;
    }

    public void setWiretapFoundImageH(int wiretapFoundImageH) {
        this.wiretapFoundImageH = wiretapFoundImageH;
    }

    public int getWiretapFoundImageW() {
        return wiretapFoundImageW;
    }

    public void setWiretapFoundImageW(int wiretapFoundImageW) {
        this.wiretapFoundImageW = wiretapFoundImageW;
    }

    public double getErrorFoundImageX() {
        return errorFoundImageX;
    }

    public void setErrorFoundImageX(double errorFoundImageX) {
        this.errorFoundImageX = errorFoundImageX;
    }

    public double getErrorFoundImageY() {
        return errorFoundImageY;
    }

    public void setErrorFoundImageY(double errorFoundImageY) {
        this.errorFoundImageY = errorFoundImageY;
    }

    public int getErrorFoundImageH() {
        return errorFoundImageH;
    }

    public void setErrorFoundImageH(int errorFoundImageH) {
        this.errorFoundImageH = errorFoundImageH;
    }

    public int getErrorFoundImageW() {
        return errorFoundImageW;
    }

    public void setErrorFoundImageW(int errorFoundImageW) {
        this.errorFoundImageW = errorFoundImageW;
    }

    public double getExclusionFoundImageX() {
        return exclusionFoundImageX;
    }

    public void setExclusionFoundImageX(double exclusionFoundImageX) {
        this.exclusionFoundImageX = exclusionFoundImageX;
    }

    public double getExclusionFoundImageY() {
        return exclusionFoundImageY;
    }

    public void setExclusionFoundImageY(double exclusionFoundImageY) {
        this.exclusionFoundImageY = exclusionFoundImageY;
    }

    public int getExclusionFoundImageH() {
        return exclusionFoundImageH;
    }

    public void setExclusionFoundImageH(int exclusionFoundImageH) {
        this.exclusionFoundImageH = exclusionFoundImageH;
    }

    public int getExclusionFoundImageW() {
        return exclusionFoundImageW;
    }

    public void setExclusionFoundImageW(int exclusionFoundImageW) {
        this.exclusionFoundImageW = exclusionFoundImageW;
    }

    public double getReplayFoundImageX() {
        return replayFoundImageX;
    }

    public void setReplayFoundImageX(double replayFoundImageX) {
        this.replayFoundImageX = replayFoundImageX;
    }

    public double getReplayFoundImageY() {
        return replayFoundImageY;
    }

    public void setReplayFoundImageY(double replayFoundImageY) {
        this.replayFoundImageY = replayFoundImageY;
    }

    public int getReplayFoundImageH() {
        return replayFoundImageH;
    }

    public void setReplayFoundImageH(int replayFoundImageH) {
        this.replayFoundImageH = replayFoundImageH;
    }

    public int getReplayFoundImageW() {
        return replayFoundImageW;
    }

    public void setReplayFoundImageW(int replayFoundImageW) {
        this.replayFoundImageW = replayFoundImageW;
    }

    public String getWiretapBeforeStatus() {
        return wiretapBeforeStatus;
    }

    public void setWiretapBeforeStatus(String wiretapBeforeStatus) {
        this.wiretapBeforeStatus = wiretapBeforeStatus;
    }

    public String getWiretapBeforeImage() {
        return wiretapBeforeImage;
    }

    public void setWiretapBeforeImage(String wiretapBeforeImage) {
        this.wiretapBeforeImage = wiretapBeforeImage;
    }

    public String getWiretapAfterStatus() {
        return wiretapAfterStatus;
    }

    public void setWiretapAfterStatus(String wiretapAfterStatus) {
        this.wiretapAfterStatus = wiretapAfterStatus;
    }

    public String getWiretapAfterImage() {
        return wiretapAfterImage;
    }

    public void setWiretapAfterImage(String wiretapAfterImage) {
        this.wiretapAfterImage = wiretapAfterImage;
    }

    public double getWiretapBeforeImageX() {
        return wiretapBeforeImageX;
    }

    public void setWiretapBeforeImageX(double wiretapBeforeImageX) {
        this.wiretapBeforeImageX = wiretapBeforeImageX;
    }

    public double getWiretapBeforeImageY() {
        return wiretapBeforeImageY;
    }

    public void setWiretapBeforeImageY(double wiretapBeforeImageY) {
        this.wiretapBeforeImageY = wiretapBeforeImageY;
    }

    public int getWiretapBeforeImageH() {
        return wiretapBeforeImageH;
    }

    public void setWiretapBeforeImageH(int wiretapBeforeImageH) {
        this.wiretapBeforeImageH = wiretapBeforeImageH;
    }

    public int getWiretapBeforeImageW() {
        return wiretapBeforeImageW;
    }

    public void setWiretapBeforeImageW(int wiretapBeforeImageW) {
        this.wiretapBeforeImageW = wiretapBeforeImageW;
    }

    public double getWiretapAfterImageX() {
        return wiretapAfterImageX;
    }

    public void setWiretapAfterImageX(double wiretapAfterImageX) {
        this.wiretapAfterImageX = wiretapAfterImageX;
    }

    public double getWiretapAfterImageY() {
        return wiretapAfterImageY;
    }

    public void setWiretapAfterImageY(double wiretapAfterImageY) {
        this.wiretapAfterImageY = wiretapAfterImageY;
    }

    public int getWiretapAfterImageH() {
        return wiretapAfterImageH;
    }

    public void setWiretapAfterImageH(int wiretapAfterImageH) {
        this.wiretapAfterImageH = wiretapAfterImageH;
    }

    public int getWiretapAfterImageW() {
        return wiretapAfterImageW;
    }

    public void setWiretapAfterImageW(int wiretapAfterImageW) {
        this.wiretapAfterImageW = wiretapAfterImageW;
    }

    public String getLogWiretapBeforeStatus() {
        return logWiretapBeforeStatus;
    }

    public void setLogWiretapBeforeStatus(String logWiretapBeforeStatus) {
        this.logWiretapBeforeStatus = logWiretapBeforeStatus;
    }

    public String getLogWiretapBeforeImage() {
        return logWiretapBeforeImage;
    }

    public void setLogWiretapBeforeImage(String logWiretapBeforeImage) {
        this.logWiretapBeforeImage = logWiretapBeforeImage;
    }

    public String getLogWiretapAfterStatus() {
        return logWiretapAfterStatus;
    }

    public void setLogWiretapAfterStatus(String logWiretapAfterStatus) {
        this.logWiretapAfterStatus = logWiretapAfterStatus;
    }

    public String getLogWiretapAfterImage() {
        return logWiretapAfterImage;
    }

    public void setLogWiretapAfterImage(String logWiretapAfterImage) {
        this.logWiretapAfterImage = logWiretapAfterImage;
    }

    public double getLogWiretapBeforeImageX() {
        return logWiretapBeforeImageX;
    }

    public void setLogWiretapBeforeImageX(double logWiretapBeforeImageX) {
        this.logWiretapBeforeImageX = logWiretapBeforeImageX;
    }

    public double getLogWiretapBeforeImageY() {
        if(!this.wiretapBeforeStatus.equals(NodeFoundStatus.FOUND)){
            return this.wiretapBeforeImageY;
        }
        return logWiretapBeforeImageY;
    }

    public void setLogWiretapBeforeImageY(double logWiretapBeforeImageY) {
        this.logWiretapBeforeImageY = logWiretapBeforeImageY;
    }

    public int getLogWiretapBeforeImageH() {
        return logWiretapBeforeImageH;
    }

    public void setLogWiretapBeforeImageH(int logWiretapBeforeImageH) {
        this.logWiretapBeforeImageH = logWiretapBeforeImageH;
    }

    public int getLogWiretapBeforeImageW() {
        return logWiretapBeforeImageW;
    }

    public void setLogWiretapBeforeImageW(int logWiretapBeforeImageW) {
        this.logWiretapBeforeImageW = logWiretapBeforeImageW;
    }

    public double getLogWiretapAfterImageX() {
        return logWiretapAfterImageX;
    }

    public void setLogWiretapAfterImageX(double logWiretapAfterImageX) {
        this.logWiretapAfterImageX = logWiretapAfterImageX;
    }

    public double getLogWiretapAfterImageY() {
        if(!this.wiretapAfterStatus.equals(NodeFoundStatus.FOUND)){
            return this.wiretapAfterImageY;
        }
        return logWiretapAfterImageY;
    }

    public void setLogWiretapAfterImageY(double logWiretapAfterImageY) {
        this.logWiretapAfterImageY = logWiretapAfterImageY;
    }

    public int getLogWiretapAfterImageH() {
        return logWiretapAfterImageH;
    }

    public void setLogWiretapAfterImageH(int logWiretapAfterImageH) {
        this.logWiretapAfterImageH = logWiretapAfterImageH;
    }

    public int getLogWiretapAfterImageW() {
        return logWiretapAfterImageW;
    }

    public void setLogWiretapAfterImageW(int logWiretapAfterImageW) {
        this.logWiretapAfterImageW = logWiretapAfterImageW;
    }
}
