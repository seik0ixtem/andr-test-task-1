package com.example.test.ui;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.test.R;
import com.example.test.parsers.Element;
import java.util.ArrayList;
import java.util.List;

public class RecycleAdapter  extends RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder> {
    private List<Element> elementList = new ArrayList<>();
    private Context context;

    class RecycleViewHolder extends RecyclerView.ViewHolder {
        LinearLayout view;

        RecycleViewHolder(View itemView) {
            super(itemView);
            view = (LinearLayout) itemView;
            boolean padding = true;

            for (int i = 0; i < elementList.size(); i++) {
                if (padding) {
                    createTextView(i, true);
                    padding = false;
                } else
                    createTextView(i, false);
            }
        }

        private void createTextView(int tag, boolean padding) {
            TextView name = new TextView(context);
            name.setTag(tag);
            name.setPadding(10, padding ? 50 : 0, 10, 0);

            view.addView(name);
        }

        void bind(int position) {
            Element element;
            for (int i = 0; i < elementList.size(); i++) {
                element = elementList.get(i);
                TextView name = view.findViewWithTag(i);
                name.setText(Html.fromHtml("<b>" + element.getKeyName() + ":</b> " + element.getValueList().get(position)));
            }
        }


    }

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
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new RecycleViewHolder(view);
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
