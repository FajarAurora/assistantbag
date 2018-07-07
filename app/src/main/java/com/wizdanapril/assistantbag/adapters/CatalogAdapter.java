package com.wizdanapril.assistantbag.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wizdanapril.assistantbag.R;
import com.wizdanapril.assistantbag.activities.CatalogActivity;
import com.wizdanapril.assistantbag.models.Catalog;

import java.util.List;

public class CatalogAdapter extends RecyclerView.Adapter<CatalogAdapter.ViewHolder> {

    private List<Catalog> catalogList;
    private Context context;
    private CatalogActivity catalogActivity;

    public CatalogAdapter(List<Catalog> catalogList, Context context) {
        this.catalogList = catalogList;
        this.context = context;
        catalogActivity = (CatalogActivity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_catalog, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Catalog catalog = catalogList.get(position);
        holder.tagName.setText(catalog.name);
        holder.tagId.setText(catalog.id);
        if (catalog.imageUri != null) {
            Uri imageUri = Uri.parse(catalog.imageUri);
            Picasso.with(catalogActivity).load(imageUri).into(holder.tagImage);
        }

        holder.itemOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            // Display option menu
            PopupMenu popupMenu = new PopupMenu(context, holder.itemOption);
            popupMenu.inflate(R.menu.option_menu_catalog);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.menu_item_edit:
                        catalogActivity.changeTag(holder.getAdapterPosition(), holder.tagImage.getDrawable());
                        break;
                    case R.id.menu_item_delete:
                        catalogActivity.removeTag(holder.getAdapterPosition());
                        break;
                    default:
                        break;
                }
                return false;
                }
            });
            popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return catalogList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tagName;
        TextView tagId;
        TextView itemOption;
        ImageView tagImage;

        private ViewHolder(View itemView) {
            super(itemView);

            tagName = (TextView) itemView.findViewById(R.id.tv_name);
            tagId = (TextView) itemView.findViewById(R.id.tv_id);
            itemOption = (TextView) itemView.findViewById(R.id.tv_digit_option);
            tagImage = (ImageView) itemView.findViewById(R.id.iv_item);
        }
    }
}