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

    protected double wiretapFoundImageX = 50;
    protected double wiretapFoundImageY = -40;
    protected int wiretapFoundImageH = 15;
    protected int wiretapFoundImageW = 15;

    protected double errorFoundImageX = 50;
    protected double errorFoundImageY = -20;
    protected int errorFoundImageH = 15;
    protected int errorFoundImageW = 15;

    protected double exclusionFoundImageX = 50;
    protected double exclusionFoundImageY = 0;
    protected int exclusionFoundImageH = 15;
    protected int exclusionFoundImageW = 15;

    protected double replayFoundImageX = 50;
    protected double replayFoundImageY = 20;
    protected int replayFoundImageH = 15;
    protected int replayFoundImageW = 15;

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
}
