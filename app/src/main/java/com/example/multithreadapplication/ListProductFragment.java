package com.example.multithreadapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ListProductFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public void setOnProductClickListener(OnProductClickListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_product, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = getSampleProducts();
        adapter = new ProductAdapter(productList, product -> {
            if (listener != null) listener.onProductClick(product);
        });
        recyclerView.setAdapter(adapter);
        return view;
    }

    private List<Product> getSampleProducts() {
        List<Product> list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            list.add(new Product("Product " + i, "Description for Product " + i + ". This is a detailed description for product number " + i + "."));
        }
        return list;
    }
}
