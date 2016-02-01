package com.example.azhaurov.jetruby;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ViewSwitcher.ViewFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ImgSwitcher extends AppCompatActivity {
    private ImageSwitcher imageSwitcher;
    private Animation in, out;
    private int curIndex = 1, switch_interval;
    String visualEffects;
    private ArrayList<Uri> imagesArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imageswitcher);
        // в зависимости от источника данных выбираем нужные методы отображения
        if (MainActivity.getSourceMode()) {
            initializeDirectoryMode();
            initializeImageSwitcher();
            startViewDir();
        } else {
            initializeImageSwitcher();
            startViewInet();
        }
    }

    // создание массива изображений, если в качестве источника данных выбрана директория
    private void initializeDirectoryMode() {
        // добавление URI файлов из выбранной директории в список ArrayList
        // добавляются только файлы с перечисленными расширениями
        File dir = new File(MainActivity.getFileDir());
        imagesArrayList = new ArrayList<Uri>();
        try {
            for (File file : dir.listFiles()) {
                if ((file.isFile()) &
                        (file.getName().substring(file.getName().length() - 3).equals("jpg") ||
                                file.getName().substring(file.getName().length() - 3).equals("png") ||
                                file.getName().substring(file.getName().length() - 3).equals("gif") ||
                                file.getName().substring(file.getName().length() - 3).equals("bmp")))
                    imagesArrayList.add(Uri.fromFile(file));
            }
        } catch (Exception e) {
            // при выборе некоторых (корневых либо RO?) директорий возникают исключения, причину не успел выяснить
        }
        Toast.makeText(getApplicationContext(),"Найдено изображений: " + imagesArrayList.size(), Toast.LENGTH_SHORT).show();
        if (imagesArrayList.isEmpty()) finish();
    }

    // подготовка компонента ImageSwitcher и настройка параметров показа изображений (длительность, эффекты)
    private void initializeImageSwitcher() {
        imageSwitcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
        SharedPreferences settingsActivity = getSharedPreferences("GlobalSettings", MODE_PRIVATE);
        switch_interval = Integer.parseInt(settingsActivity.getString("TimeValue","2"))*1000;
        visualEffects = settingsActivity.getString("VisualEffectsValue", "slide");
        switch (visualEffects){
            case "slide":
                in = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
                out = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
                break;
            case "fade":
                in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
                out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
                break;
            default:
                break;
        }
        imageSwitcher.setInAnimation(in);
        imageSwitcher.setOutAnimation(out);
        imageSwitcher.setFactory(new ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView = new ImageView(ImgSwitcher.this);
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
                return imageView;
            }
        });
    }

    // просмотр изображений, загружаемых из выбранной директории, после показа последнего изображения выполняется переход в main activity
    private void startViewDir() {
       if (!imagesArrayList.isEmpty()) {
           imageSwitcher.setImageURI(imagesArrayList.get(0));
            curIndex = 1;
            imageSwitcher.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (curIndex == (imagesArrayList.size()))
                        finish();
                    else
                        imageSwitcher.setImageURI(imagesArrayList.get(curIndex++));
                    imageSwitcher.postDelayed(this, switch_interval);
                }
            }, switch_interval);
        }
    }

    // просмотр изображений, загружаемых из Интернета, при помощи рекурсивного метода showInternetImage
    private void startViewInet() {
        Toast.makeText(getApplicationContext(),"Найдено изображений: " + getResources().getInteger(R.integer.number_of_urls), Toast.LENGTH_SHORT).show();
        showInternetImage(getResources().getStringArray(R.array.urls)[0]);
    }

// асинхронная загрузка файлов из Интернета с отображением progress-бара
private void showInternetImage(final String url) {
    final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
    final View view = (View) findViewById(R.id.divider);
    view.setVisibility(View.INVISIBLE);
    final int URLS_NUMBER = getResources().getInteger(R.integer.number_of_urls);

    new AsyncTask<String, Integer, File>() {
        private Exception m_error = null;

        @Override
        protected void onPreExecute() {
            progressBar.setMax(100);
        }

        @Override
        protected File doInBackground(String... params) {
            URL url;
            HttpURLConnection urlConnection;
            InputStream inputStream;
            int totalSize;
            int downloadedSize;
            byte[] buffer;
            int bufferLength;

            File file = null;
            FileOutputStream fos = null;

            try {
                url = new URL(params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setAllowUserInteraction(true);
                urlConnection.setRequestProperty("Connection", "Keep-Alive");
                urlConnection.connect();

                file = File.createTempFile("img_", ".download");
                fos = new FileOutputStream(file);
                inputStream = urlConnection.getInputStream();

                totalSize = urlConnection.getContentLength();
                downloadedSize = 0;

                buffer = new byte[1024];
                bufferLength = 0;

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fos.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;
                    publishProgress(downloadedSize, totalSize);
                }

                fos.close();
                inputStream.close();
                return file;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                m_error = e;
            } catch (IOException e) {
                e.printStackTrace();
                m_error = e;
            }
            return null;
        }

        protected void onProgressUpdate(Integer... values) {
            if (progressBar.getVisibility() == View.INVISIBLE) progressBar.setVisibility(ProgressBar.VISIBLE);
            progressBar.setProgress((int) ((values[0] / (float) values[1]) * 100));
        };

        @Override
        protected void onPostExecute(File file) {
            if (m_error != null) {
                m_error.printStackTrace();
                return;
            }
            progressBar.setVisibility(ProgressBar.INVISIBLE);
            imageSwitcher.setImageURI(Uri.fromFile(file));
            file.delete();
            //Toast.makeText(getApplicationContext(), file.getName(), Toast.LENGTH_SHORT).show();
            imageSwitcher.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (curIndex == URLS_NUMBER) {
                        finish();
                    }
                    else {
                        showInternetImage(getResources().getStringArray(R.array.urls)[curIndex++]);
                    }
                }
            }, switch_interval);
        }
    }.execute(url);
}

}

