package com.example.nitesh.myapplication.network;

import android.test.AndroidTestCase;

import com.google.mockwebserver.MockResponse;
import com.google.mockwebserver.MockWebServer;
import com.google.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by nitesh on 7/10/17.
 */


public class HttpHandlerTest {

    private MockWebServer mServer;
    private HttpHandler mClient;

    @Before
    public void setUp() throws Exception {
        mServer = new MockWebServer();
        mServer.play();
        mClient = new HttpHandler(mServer.getUrl("/string"));
    }

    @After
    public void tearDown() throws Exception {
        mServer.shutdown();
    }

    @Test
    public void makeServiceCall() throws Exception {
        mServer.enqueue(new MockResponse().setResponseCode(200).setBody("myString"));
        String str = mClient.makeServiceCall();
        RecordedRequest req = mServer.takeRequest();
        assertEquals("/string", req.getPath());
        assertEquals("GET", req.getMethod());
        assertNotNull(str);
        assertEquals("myString\n", str);
    }
}