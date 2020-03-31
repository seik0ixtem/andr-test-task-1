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
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

public class RecycleAdapter  extends RecyclerView.Adapter<RecycleAdapter.RecycleViewHolder> {
    private List<String> keyList = new ArrayList<>();
    private ArrayList<String> valueList = new ArrayList<>();
    private Context context;

    class RecycleViewHolder extends RecyclerView.ViewHolder {
        LinearLayout view;

        RecycleViewHolder(View itemView) {
            super(itemView);
            view = (LinearLayout) itemView;
            boolean padding = true;
            for (int i = 0; i < keyList.size(); i++) {
                if (padding) {
                    createTextView(i, true);
                    padding = false;
                } else
                    createTextView(i, false);
            }
        }

        void bind(String values) {
            try {
                JSONArray jsonArray = new JSONArray(values);
                for (int i = 0; i < keyList.size(); i++) {
                    TextView name = view.findViewWithTag(i);
                    name.setText(Html.fromHtml("<b>" + keyList.get(i) + ":</b> " + jsonArray.getString(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void createTextView(int tag, boolean padding) {
            TextView name = new TextView(context);
            name.setTag(tag);
            name.setPadding(10, padding ? 50 : 0, 10, 0);
            view.addView(name);
        }
    }

    public void setItemList(ArrayList<String> keys, ArrayList<String> value) {
        keyList.addAll(keys);
        valueList.addAll(value);
        notifyDataSetChanged();
    }

    public void clearItemList() {
        keyList.clear();
        valueList.clear();
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
        holder.bind(valueList.get(position));
    }

    @Override
    public int getItemCount() {
        return valueList.size();
    }
}
