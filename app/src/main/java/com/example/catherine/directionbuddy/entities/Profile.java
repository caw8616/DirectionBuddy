package com.example.catherine.directionbuddy.entities;

import android.net.Uri;

public class Profile {
    String id;
    String username;
    String displayName;
    Uri photoUri;

    public Profile(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public Profile(String id, String username, String displayName, Uri photoUri) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.photoUri = photoUri;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", photoUri=" + photoUri +
                '}';
    }
}
