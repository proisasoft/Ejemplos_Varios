package com.mobile.proisa.fotosvehiculoslibrary.Clases;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;

public class MyMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private MediaScannerConnection mMs;
    private Activity context;
    private Uri uri;

    public MyMediaScanner(Activity context, Uri uri) {
        this.context = context;
        this.uri = uri;
        this.mMs = new MediaScannerConnection(context,this);
        this.mMs.connect();
    }

    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(uri.getPath(), null);
    }

    @Override
    public void onScanCompleted(String s, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);

        context.startActivity(intent);

        mMs.disconnect();
    }
}
