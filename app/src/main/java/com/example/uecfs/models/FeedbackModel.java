package com.example.uecfs.models;

import androidx.annotation.NonNull;

public class FeedbackModel {
    private String location, usersId, status, photo, cleanliness, feedback, title, id, coordinates, action, like, responseImage;

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getResponseImage() {
        return responseImage;
    }

    public void setResponseImage(String responseImage) {
        this.responseImage = responseImage;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUsersId() {
        return usersId;
    }

    public void setUsersId(String usersId) {
        this.usersId = usersId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCleanliness() {
        return cleanliness;
    }

    public void setCleanliness(String cleanliness) {
        this.cleanliness = cleanliness;
    }

    @Override
    public String toString() {
        return "FeedbackModel{" +
                "location='" + location + '\'' +
                ", usersId='" + usersId + '\'' +
                ", status='" + status + '\'' +
                ", photo='" + photo + '\'' +
                ", cleanliness='" + cleanliness + '\'' +
                ", feedback='" + feedback + '\'' +
                ", title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", coordinates='" + coordinates + '\'' +
                ", action='" + action + '\'' +
                '}';
    }

}
