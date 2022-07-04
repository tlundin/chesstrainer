package com.teraime.chesstrainer;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    String raw;
    String name;
    int stage;
    int rating;

    public User() {
        try {
            this.raw = new JSONObject()
                    .put("NAME", "Terje")
                    .put("STAGE", "10")
                    .put("RATING", "1500")
                    .toString();
            name = "Terje";
            stage = Integer.parseInt("1");
            rating = 1600;
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public User(String userData) {
        this.raw = userData;
    }
}


