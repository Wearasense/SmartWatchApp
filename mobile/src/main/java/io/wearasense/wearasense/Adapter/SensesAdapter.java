package io.wearasense.wearasense.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import io.wearasense.wearasense.R;
import io.wearasense.wearasense.Models.Category;

/**
 * Created by goofyahead on 8/07/15.
 */
public class SensesAdapter extends BaseAdapter {
    private ArrayList<Category> categories;
    private LayoutInflater mInflater;

    public SensesAdapter(Context context, ArrayList<Category> categories) {
        this.categories = categories;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.menu_item, null);
            holder.name = (TextView) convertView.findViewById(R.id.categories_item_name);
            holder.image = (ImageView) convertView.findViewById(R.id.category_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(categories.get(position).getCategoryName());
        holder.image.setImageResource(categories.get(position).getResourceImg());
        return convertView;
    }


    private class ViewHolder {
        private TextView name;
        private ImageView image;
    }
}
