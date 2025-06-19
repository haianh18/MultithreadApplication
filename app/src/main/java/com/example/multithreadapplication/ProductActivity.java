package com.example.multithreadapplication;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ProductActivity extends AppCompatActivity implements ListProductFragment.OnProductClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        findViewById(R.id.button_BackToMain).setOnClickListener(v -> handleBackPressed());
        if (savedInstanceState == null) {
            ListProductFragment listFragment = new ListProductFragment();
            listFragment.setOnProductClickListener(this);
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, listFragment)
                .commit();
        }
    }

    private void handleBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            finish();
        }
    }


    @Override
    public void onProductClick(Product product) {
        DetailedProductFragment detailFragment = DetailedProductFragment.newInstance(product.name, product.description);
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.fragment_container, detailFragment)
            .addToBackStack(null)
            .commit();
    }
}