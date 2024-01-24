package com.teraime.chesstrainer;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    String raw;
    String name;
    public int stage;
    public int rating;
    public int level;

    public User() {
        try {
            this.raw = new JSONObject()
                    .put("NAME", "Terje")
                    .put("STAGE", "10")
                    .put("RATING", "1500")
                    .toString();
            name = "Terje";
            stage = Integer.parseInt("0");
            rating = 1400;
            level = 1;
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public User(String userData) {
        this.raw = userData;
    }
}


