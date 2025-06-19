package com.example.multithreadapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ImageView imageView;
    private ProgressBar progressBar;
    private static final String UI_THREAD_IMAGE_URL = "https://hoanghamobile.com/tin-tuc/wp-content/webp-express/webp-images/uploads/2024/08/anh-con-meo-cute-2.jpg.webp";
    private static final String WORKER_THREAD_IMAGE_URL = "https://hoanghamobile.com/tin-tuc/wp-content/webp-express/webp-images/uploads/2024/08/anh-con-meo-cute-3.jpg.webp";
    private static final String ASYNC_TASK_IMAGE_URL = "https://hoanghamobile.com/tin-tuc/wp-content/webp-express/webp-images/uploads/2024/08/anh-con-meo-cute-4.jpg.webp";
    private static final String EXECUTOR_SERVICE_IMAGE_URL = "https://hoanghamobile.com/tin-tuc/wp-content/webp-express/webp-images/uploads/2024/08/anh-con-meo-cute-5.jpg.webp";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.button_GoToProduct).setOnClickListener(v -> goToProductActivity());
    }

    private void goToProductActivity() {
        startActivity(new android.content.Intent(this, ProductActivity.class));
    }

    private Bitmap getBitmapFromUrl(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful() && response.body() != null) {
            InputStream inputStream = response.body().byteStream();
            return BitmapFactory.decodeStream(inputStream);
        }
        return null;
    }

    private void showProgressBar() {
        runOnUiThread(() -> {
            progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.VISIBLE);
        });
    }

    private void hideProgressBar() {
        runOnUiThread(() -> progressBar.setVisibility(View.GONE));
    }

    private void showProgressBarWithProgress() {
        runOnUiThread(() -> {
            progressBar.setIndeterminate(false);
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
        });
    }

    private void updateProgressBar(int progress) {
        runOnUiThread(() -> progressBar.setProgress(progress));
    }

    public void loadImageUIThread(View view) {
        showProgressBar();
        try {
            Bitmap bitmap = getBitmapFromUrl(UI_THREAD_IMAGE_URL);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        } catch (IOException e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
        hideProgressBar();
    }

    public void loadImageWorkerThread(View view) {
        showProgressBarWithProgress();
        new Thread(() -> {
            int progress = 0;
            var ref = new Object() {
                boolean downloading = true;
            };
            // Start a progress simulation thread
            Thread progressThread = new Thread(() -> {
                int localProgress = 0;
                while (ref.downloading && localProgress < 95) {
                    localProgress += 5;
                    updateProgressBar(localProgress);
                    try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                }
            });
            progressThread.start();
            try {
                Bitmap bitmap = getBitmapFromUrl(WORKER_THREAD_IMAGE_URL);
                ref.downloading = false;
                updateProgressBar(100);
                if (bitmap != null) {
                    runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                }
            } catch (IOException e) {
                ref.downloading = false;
                e.printStackTrace();
            }
            hideProgressBar();
        }).start();
    }

    public void loadImageAsyncTask(View view) {
        showProgressBarWithProgress();
        new AsyncTask<Void, Integer, Bitmap>() {
            private volatile boolean downloading = true;
            @Override
            protected Bitmap doInBackground(Void... voids) {
                // Simulate progress
                new Thread(() -> {
                    int localProgress = 0;
                    while (downloading && localProgress < 95) {
                        localProgress += 5;
                        publishProgress(localProgress);
                        try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                    }
                }).start();
                try {
                    Bitmap bmp = getBitmapFromUrl(ASYNC_TASK_IMAGE_URL);
                    downloading = false;
                    publishProgress(100);
                    return bmp;
                } catch (IOException e) {
                    downloading = false;
                    e.printStackTrace();
                    return null;
                }
            }
            @Override
            protected void onProgressUpdate(Integer... values) {
                updateProgressBar(values[0]);
            }
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
                hideProgressBar();
            }
        }.execute();
    }

    public void loadImageServiceExecutor(View view) {
        showProgressBar();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(getMainLooper());
        executor.execute(() -> {
            try {
                Bitmap bitmap = getBitmapFromUrl(EXECUTOR_SERVICE_IMAGE_URL);
                if (bitmap != null) {
                    handler.post(() -> imageView.setImageBitmap(bitmap));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            hideProgressBar();
        });
    }
}