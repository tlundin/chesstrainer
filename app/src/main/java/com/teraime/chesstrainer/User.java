package com.teraime.chesstrainer;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

    String raw;
    String name;
    int stage;

    public User() {
        try {
            this.raw = new JSONObject()
                    .put("NAME", "Terje")
                    .put("STAGE", "10")
                    .put("RATING", "1500")
                    .toString();
            name = "Terje";
            stage = Integer.parseInt("10");
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public User(String userData) {
        this.raw = userData;
    }
}


