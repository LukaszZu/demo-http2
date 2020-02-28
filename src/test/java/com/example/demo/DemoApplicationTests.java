package com.example.demo;

import org.junit.Assert;
import org.junit.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DemoApplicationTests {

    @Test
    public void exceptionTest() throws IOException, InterruptedException, NoSuchAlgorithmException, KeyManagementException, KeyStoreException {
        HttpResponse<String> response = doTest(100000);
        Assert.assertEquals("Ok received", 200, response.statusCode());
    }

    private HttpResponse<String> doTest(int size) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, IOException, InterruptedException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sslContext.init(null, new TrustManager[]{tm}, null);

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .sslContext(sslContext)
                .build();

        String url = "https://localhost:8443/do-some";


        HttpRequest build1 = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(buildPayload(size)))
                .uri(URI.create(url))
                .build();

        return client.send(build1, HttpResponse.BodyHandlers.ofString());
    }

    private String buildPayload(int size) {
        return IntStream.range(0, size).boxed().map(i -> ".").collect(Collectors.joining());
    }

}
