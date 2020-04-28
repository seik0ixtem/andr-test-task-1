package com.example.test.ui;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.test.parsers.Element;

import java.util.List;

class RecycleViewHolder extends RecyclerView.ViewHolder {
    private LinearLayout view;
    private List<Element> elementList;
    private Context context;

    RecycleViewHolder(View itemView, List<Element> elementList, Context context) {
        super(itemView);
        this.elementList = elementList;
        this.context = context;
        view = (LinearLayout) itemView;
        
        for (int i = 0; i < elementList.size(); i++)
            createTextView(i);
    }

    private void createTextView(int tag) {
        TextView name = new TextView(context);
        name.setTag(tag);
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
