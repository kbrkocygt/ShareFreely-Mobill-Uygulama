package com.kubrakocyigit.sharefreely_proje1;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BildirimlerFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView view=new TextView(getContext());
        view.setText("Burası Bildirimler fragmentidir");
        view.setTextSize(30);
        view.setGravity(Gravity.CENTER);
        return view;
    }
}
