package com.company;

public class TokenAndId {
    public String token;
    public int id;

    public TokenAndId(int id, String token){
        this.id = id;
        this.token = token;
    }

    @Override
    public String toString(){
        return id+"/"+token;
    }
}
