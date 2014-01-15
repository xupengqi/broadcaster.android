package com.broadcaster.model;

import java.io.Serializable;

public class UserObj implements Serializable {
    private static final long serialVersionUID = 1L;

    public Integer id;
    public String username;
    public String email;
    public String token;
    public String fbId;
    public String gPlusId;
    public int usernameChange = 0;
    public int usingFb;
    public int usingGp;
}
