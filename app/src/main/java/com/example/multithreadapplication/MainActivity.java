package com.example.multithreadapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Helper method to fetch Bitmap from URL (synchronously)
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

    public void loadImageUIThread(View view) {
        // Not recommended: Network on main thread (for demonstration only)
//        try {
//            Bitmap bitmap = getBitmapFromUrl(UI_THREAD_IMAGE_URL);
//            if (bitmap != null) {
//                imageView.setImageBitmap(bitmap);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        imageView.setImageResource(R.drawable.anh_con_meo_cute_2); // Placeholder for UI thread

    }

    public void loadImageWorkerThread(View view) {
        new Thread(() -> {
            try {
                Bitmap bitmap = getBitmapFromUrl(WORKER_THREAD_IMAGE_URL);
                if (bitmap != null) {
                    runOnUiThread(() -> imageView.setImageBitmap(bitmap));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void loadImageAsyncTask(View view) {
        new AsyncTask<Void, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {
                try {
                    return getBitmapFromUrl(ASYNC_TASK_IMAGE_URL);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }.execute();
    }

    public void loadImageServiceExecutor(View view) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(getMainLooper());
        executor.execute( new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bitmap = getBitmapFromUrl(EXECUTOR_SERVICE_IMAGE_URL);
                    if (bitmap != null) {
                        handler.post(() -> imageView.setImageBitmap(bitmap));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}