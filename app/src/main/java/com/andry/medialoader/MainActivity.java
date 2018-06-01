package com.andry.medialoader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements FileLoader.Callback{

    private static final String URI_BUNDLE = "URI_BUNDLE";
    private TextView uriText;
    private EditText editText;
    private Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        uriText = findViewById(R.id.activity_main_uri_text);
        editText = findViewById(R.id.activity_main_edit);
        downloadButton = findViewById(R.id.activity_main_download_button);
        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String urlString = editText.getText().toString();
                new FileLoader(urlString, MainActivity.this).loadFile();
            }
        });
        if (savedInstanceState != null)
            uriText.setText(savedInstanceState.getString(URI_BUNDLE));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(URI_BUNDLE, uriText.getText().toString());
    }

    @Override
    public void onDownloadingStart(String message) {
        uriText.setText(message);
    }

    @Override
    public void onDownloadFinish(String uri) {
        uriText.setText(uri);
    }
}
