package org.ikasan.dashboard.ui.visualisation.component.util;

public class SearchFoundStatus {
    private Boolean wiretapFound = false;
    private Boolean errorFound = false;
    private Boolean exclusionFound = false;
    private Boolean replayFound = false;
    private String searchTerm;
    private Long startTime;
    private Long endTime;
    private boolean negated;

    public Boolean getWiretapFound() {
        return wiretapFound;
    }

    public void setWiretapFound(Boolean wiretapFound) {
        this.wiretapFound = wiretapFound;
    }

    public Boolean getErrorFound() {
        return errorFound;
    }

    public void setErrorFound(Boolean errorFound) {
        this.errorFound = errorFound;
    }

    public Boolean getExclusionFound() {
        return exclusionFound;
    }

    public void setExclusionFound(Boolean exclusionFound) {
        this.exclusionFound = exclusionFound;
    }

    public Boolean getReplayFound() {
        return replayFound;
    }

    public void setReplayFound(Boolean replayFound) {
        this.replayFound = replayFound;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public boolean isNegated() {
        return negated;
    }

    public void setNegated(boolean negated) {
        this.negated = negated;
    }
}