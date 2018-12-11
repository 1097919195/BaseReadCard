package com.example.gxkj.cardnumbinding.bean;

/**
 * Created by Administrator on 2018/6/1 0001.
 */

public class LoginTokenData {
    /**
     * access_token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczpcL1wvbi5ucGNsby5jb21cL2FwaVwvY2xpZW50XC9sb2dpbiIsImlhdCI6MTUyNzgzMzgwMywiZXhwIjoxNTI3ODM3NDAzLCJuYmYiOjE1Mjc4MzM4MDMsImp0aSI6InF4aHNYQU1xUHVYNklnSHkiLCJzdWIiOiI1YjA2YTViMDkxMzRjYTA4NjYyZWM3NTgiLCJwcnYiOiIzYmJmMjcyYmVkNzRiZWRhYjA3MmIzNmQxYTUxYTAyNGY2MzhmM2ExIn0.2R58EsBNnkaNXHv63QVGRRGYyQe_ZQ0JQEK_XULb_No
     * token_type : bearer
     * expires_in : 3600
     */

    private String access_token;
    private String token_type;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

}
