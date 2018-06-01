package com.andry.medialoader;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileLoader {

    private static final String TARGET_BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/MediaFile/";
    private String urlString;
    private String uriString;
    private Callback callback;

    public FileLoader(String urlString, Callback callback) {
        this.urlString = urlString;
        this.callback = callback;
    }

    public void loadFile() {
        createFolderIfNeeded();
        new FetchItemsTask().execute();
        callback.onDownloadingStart("Downloading...");
    }

    private void createFolderIfNeeded() {
        if (!new File(TARGET_BASE_PATH).isDirectory()) {
            File dir = new File(TARGET_BASE_PATH);
            dir.mkdir();
        }
    }

    private void saveFileToFolder() throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            throw new IOException(connection.getResponseMessage() + ": with " + url);
        uriString = createUri();

        try (ByteArrayOutputStream outByteArray = new ByteArrayOutputStream();
             OutputStream outFileSave = new FileOutputStream(new File(uriString));
             InputStream in = connection.getInputStream();
        ) {
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                if (outByteArray.size() > 30 * 1024) {
                    outByteArray.writeTo(outFileSave);
                    outByteArray.reset();
                }
                outByteArray.write(buffer, 0, bytesRead);
            }
            outByteArray.writeTo(outFileSave);
        } finally {
            connection.disconnect();
        }
    }

    private String createUri() {
        int nameStartIndex = urlString.lastIndexOf("/");
        return TARGET_BASE_PATH + urlString.substring(++nameStartIndex);
    }

    private class FetchItemsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                saveFileToFolder();
                return uriString;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return urlString;
        }

        @Override
        protected void onPostExecute(String uri) {
            super.onPostExecute(uri);
            callback.onDownloadFinish(uri);
        }
    }

    public interface Callback {
        void onDownloadingStart(String string);
        void onDownloadFinish(String uriString);
    }
}
