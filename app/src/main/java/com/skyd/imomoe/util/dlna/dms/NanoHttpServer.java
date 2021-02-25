package com.skyd.imomoe.util.dlna.dms;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

class NanoHttpServer extends NanoHTTPD implements IResourceServer {

    private static final Map<String, String> MIME_TYPE = new HashMap<>();
    private static final String MIME_PLAINTEXT = "text/plain";

    static {
        MIME_TYPE.put("jpg", "image/*");
        MIME_TYPE.put("jpeg", "image/*");
        MIME_TYPE.put("png", "image/*");
        MIME_TYPE.put("mp3", "audio/*");
        MIME_TYPE.put("mp4", "video/*");
        MIME_TYPE.put("wav", "video/*");
    }

    public NanoHttpServer(int port) {
        super(port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        System.out.println("uri: " + session.getUri());
        System.out.println("header: " + session.getHeaders().toString());
        System.out.println("params: " + session.getParms().toString());
        String uri = session.getUri();
        if (TextUtils.isEmpty(uri) || !uri.startsWith("/")) {
            return NanoHTTPD.newChunkedResponse(Response.Status.BAD_REQUEST, MIME_PLAINTEXT, null);
        }
        File file = new File(uri);
        if (!file.exists() || file.isDirectory()) {
            return NanoHTTPD.newChunkedResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, null);
        }
        String type = uri.substring(Math.min(uri.length(), uri.lastIndexOf(".") + 1)).toLowerCase(Locale.US);
        String mimeType = MIME_TYPE.get(type);
        if (TextUtils.isEmpty(mimeType)) {
            mimeType = MIME_PLAINTEXT;
        }
        try {
            return NanoHTTPD.newChunkedResponse(Response.Status.OK, mimeType, new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return NanoHTTPD.newChunkedResponse(Response.Status.SERVICE_UNAVAILABLE, mimeType, null);
        }
    }

    @Override
    public void startServer() {
        try {
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stopServer() {
        stop();
    }
}
