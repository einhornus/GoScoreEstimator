package com.company;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jdk.nashorn.internal.parser.JSONParser;

public class GameInfo {
    public int id;
    public ArrayList<Integer> users = new ArrayList<>();
    public String title;
    public String settings;
    public int max;
    public String status;
    public int server;

    public static GameInfo deserializeJson(String jsonString){
        try {
            JSONObject parser = new JSONObject(jsonString);
            GameInfo res = new GameInfo();
            res.title = parser.getString("title");
            res.settings = parser.getString("settings");
            res.id = parser.getInt("id");
            res.max = parser.getInt("max");
            res.status = parser.getString("status");
            res.server = parser.getInt("server");
            JSONArray usersArray = parser.getJSONArray("users");
            for (int i = 0; i < usersArray.length(); i++) {
                res.users.add((Integer) usersArray.get(i));
            }
            return res;
        }
        catch (JSONException e){
            e.printStackTrace();
            return null;
        }
    }

    public GameInfo copy()
    {
        GameInfo res = new GameInfo();
        res.id = id;
        res.users = new ArrayList<Integer>();
        if (users != null)
        {
            for (int i = 0; i < users.size(); i++)
            {
                res.users.add(users.get(i));
            }
        }
        res.title = title;
        res.settings = settings;
        res.max = max;
        res.status = status;
        res.server = server;
        return res;
    }
}
