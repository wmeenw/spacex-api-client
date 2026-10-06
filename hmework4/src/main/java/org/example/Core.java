package org.example;

import com.google.gson.annotations.SerializedName;

public class Core {
    private String core;
    private int flight;
    @SerializedName("landing_attempt")
    private boolean landingAttempt;
    @SerializedName("landing_success")
    private Boolean landingSuccess;
    @SerializedName("landing_type")
    private String landingType;
    private String landpad;

    public String getCore() {
        return core;
    }

    public int getFlight() {
        return flight;
    }

    public boolean getlandingAttempt(){
        return landingAttempt;
    }

    public Boolean getLandingSuccess() {
        return landingSuccess;
    }

    public String getLandingType() {
        return landingType;
    }

    public String getLandpad() {
        return landpad;
    }
}
