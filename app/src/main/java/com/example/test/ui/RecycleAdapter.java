package com.example.test.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.parsers.Element;

import java.util.ArrayList;
import java.util.List;

public class RecycleAdapter  extends RecyclerView.Adapter<RecycleViewHolder> {
    private List<Element> elementList = new ArrayList<>();

    public void setItemList(List<Element> elementList) {
        this.elementList = elementList;
        notifyDataSetChanged();
    }

    public void clearItemList() {
        this.elementList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new RecycleViewHolder(view, elementList, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (elementList.size() != 0)
            return elementList.get(0).getValueList().size();
        else
            return 0;
    }
}
