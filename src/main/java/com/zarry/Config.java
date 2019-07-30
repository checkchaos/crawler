package com.zarry;

public class Config {
    public static void proxy(String host,String port){
        String proxyHost = host;
        String proxyPort = port;
        // 对http开启代理
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        // 对https也开启代理
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
    }
}
