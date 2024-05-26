package com.example.repondeur_java.elements;

import android.util.Log;

public class Response {
    private String text;
    private boolean isSpam;
    private boolean isAutomaticResponse;

    public Response(String text, boolean isSpam, boolean isAutomaticResponse) {
        this.text = text;
        this.isSpam = isSpam;
        this.isAutomaticResponse = isAutomaticResponse;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSpam() {
        return isSpam;
    }

    public void setSpam(boolean spam) {
        isSpam = spam;
    }

    public boolean isAutomaticResponse() {
        return isAutomaticResponse;
    }

    public void setAutomaticResponse(boolean automaticResponse) {
        isAutomaticResponse = automaticResponse;
    }
}
