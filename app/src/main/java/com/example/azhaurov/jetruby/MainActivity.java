package com.example.azhaurov.jetruby;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import  com.example.azhaurov.dirchooser.DirectoryChooserFragment;
import  com.example.azhaurov.dirchooser.DirectoryChooserConfig;

public class MainActivity extends AppCompatActivity implements DirectoryChooserFragment.OnFragmentInteractionListener {

    private TextView directoryTextView;
    private DirectoryChooserFragment dialogDirectoryChooserFragment;
    private Button buttonSelectFromDir, buttonStartView;
    private Switch switchLoadFromInet;
    private int directoryNotSet;
    private static String fileDir;
    private static boolean isDirectoryAsSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        directoryTextView = (TextView) findViewById(R.id.textDirectory);
        buttonSelectFromDir = (Button) findViewById(R.id.btnSelectFromDir);
        buttonStartView = (Button) findViewById(R.id.btnStartView);
        switchLoadFromInet = (Switch) findViewById(R.id.switchInet);
        directoryNotSet = getResources().getIdentifier("main_nonselected", "string", getPackageName());

        initMainActivityUI(switchLoadFromInet);

        // создаем диалог выбора директории
        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DialogSample")
                .build();
        dialogDirectoryChooserFragment = DirectoryChooserFragment.newInstance(config);
        findViewById(R.id.btnSelectFromDir)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogDirectoryChooserFragment.show(getFragmentManager(), null);
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
         if (id == R.id.menu_settings) {
            Intent intent = new Intent(this, Preferences.class);
            startActivity(intent);
        }
        if (id == R.id.menu_exit) {
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSelectDirectory(@NonNull final String path) {
        directoryTextView.setText(path);
        dialogDirectoryChooserFragment.dismiss();
        if (directoryTextView.getText().equals(getString(directoryNotSet))){
            buttonStartView.setVisibility(View.GONE);
        } else{
            buttonStartView.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onCancelChooser() {
        dialogDirectoryChooserFragment.dismiss();
    }

    // инициализация графических элементов главной формы
    public void initMainActivityUI(View v) {
        if (switchLoadFromInet.isChecked()) {
            isDirectoryAsSource = false;
            directoryTextView.setText("Загрузка из Интернета");
            buttonSelectFromDir.setVisibility(View.GONE);
            buttonStartView.setVisibility(View.VISIBLE);
        } else {
            isDirectoryAsSource = true;
            directoryTextView.setText(getString(directoryNotSet));
            buttonSelectFromDir.setVisibility(View.VISIBLE);
            buttonStartView.setVisibility(View.GONE);
        }
    }

    public void onBtnStartViewClick(View view) {
        Intent intent = new Intent(this, ImgSwitcher.class);
        fileDir = directoryTextView.getText().toString();

        startActivity(intent);
    }

    static public String getFileDir() {
        return fileDir;
    }

    static public boolean getSourceMode() {
        return isDirectoryAsSource;
    }
}

