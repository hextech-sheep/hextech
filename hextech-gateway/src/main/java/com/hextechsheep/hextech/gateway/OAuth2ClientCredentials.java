package com.hextechsheep.hextech.gateway;

public class OAuth2ClientCredentials {

    public static final String API_KEY = "";
    public static final String API_SECRET = "";
    public static final int PORT = 8080;
    public static final String DOMAIN = "127.0.0.1";

    public static void errorIfNotSpecified() {
        if (API_KEY.equals("") || API_SECRET.equals("")) {
            System.out.println("API KEY or API SECRET not specified");
            System.exit(1);
        }
    }
}
