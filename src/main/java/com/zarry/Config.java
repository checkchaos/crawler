package com.zarry;

public class Config {
    static {
        String proxyHost = "10.4.200.230";
        String proxyPort = "80";
        // 对http开启代理
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        // 对https也开启代理
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
    }
}
