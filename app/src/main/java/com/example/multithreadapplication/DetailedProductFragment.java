package com.example.multithreadapplication;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailedProductFragment extends Fragment {
    private static final String ARG_NAME = "name";
    private static final String ARG_DESC = "desc";

    public static DetailedProductFragment newInstance(String name, String desc) {
        DetailedProductFragment fragment = new DetailedProductFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, name);
        args.putString(ARG_DESC, desc);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailed_product, container, false);
        TextView name = view.findViewById(R.id.productName);
        TextView desc = view.findViewById(R.id.productDesc);
        if (getArguments() != null) {
            name.setText(getArguments().getString(ARG_NAME));
            desc.setText(getArguments().getString(ARG_DESC));
        }
        return view;
    }
}

