package com.gugugu.myapplication.chatgpt;

import com.android.volley.toolbox.HurlStack;
import com.gugugu.myapplication.utilities.Config;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;

public class ProxiedHurlStack extends HurlStack {
    @Override
    protected HttpURLConnection createConnection(URL url) throws IOException {
        // Start the connection by specifying a proxy server
        Proxy proxy = new Proxy(Proxy.Type.HTTP,
                InetSocketAddress.createUnresolved(Config.PROXY, Config.PORT));//the proxy server(Can be your laptop ip or company proxy)
        HttpURLConnection returnThis = (HttpURLConnection) url
                .openConnection(proxy);

        return returnThis;
    }
}
