package com.example.dominique.barcode;


import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * A class to interact with opengtindb. Access is done via web-scraping and might be outdated at any time.
 */
public class GTINDatabase {


    public static String query(String barcode)  {

        try {

            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                        public void checkClientTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                        public void checkServerTrusted(
                                java.security.cert.X509Certificate[] certs, String authType) {
                        }
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());


            String urlParameters = "cmd=ean1&SID=&ean="+barcode;
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            String request = "https://opengtindb.org/index.php";
            URL url = new URL(request);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.write(postData);
            wr.flush();
            wr.close();
            conn.connect();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String html = org.apache.commons.io.IOUtils.toString(in, "UTF-8");
            Document doc = Jsoup.parse(html);


            String possibleError = doc.select("font[color=RED]").text();

            String itemName = doc.select("a").get(2).text();
            if(!possibleError.contains("Fehler"))
                return itemName;
            else
                return "Unknown: "+barcode;

        } catch (Exception exception) {
            Log.e("httpexception", exception.toString());
        }


        return null;
    }
}
