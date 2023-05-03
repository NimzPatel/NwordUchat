package com.nimesh.uchat.model;

import java.util.Date;
import java.util.Objects;

public class ReportedPost {
    private String postID;
    private String reportedBy;
    private String reportedUsername;
    private String ImageUrl;

    public ReportedPost() {
    }

    public ReportedPost(String postID, String reportedBy, String reportedUsername, String imageUrl) {
        this.postID = postID;
        this.reportedBy = reportedBy;
        this.reportedUsername = reportedUsername;
        ImageUrl = imageUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReportedPost)) return false;
        ReportedPost that = (ReportedPost) o;
        return postID.equals(that.postID) && reportedBy.equals(that.reportedBy) && reportedUsername.equals(that.reportedUsername) && ImageUrl.equals(that.ImageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postID, reportedBy, reportedUsername, ImageUrl);
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getReportedUsername() {
        return reportedUsername;
    }

    public void setReportedUsername(String reportedUsername) {
        this.reportedUsername = reportedUsername;
    }
}
