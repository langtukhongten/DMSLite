package com.vietdms.mobile.dmslauncher.RecycleView;

/**
 * Created by Admin on 6/29/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;
import java.util.List;

import CommonLib.Product;
import CommonLib.Utils;


public class RecyclerViewAdapterProductEmployee extends RecyclerView.Adapter<RecyclerViewAdapterProductEmployee.ContactViewHolder> implements Filterable {
    private List<Product> ProductList;
    private List<Product> ProductListFilter;
    private Context context;
    private ItemFilter mFilter = new ItemFilter();

    public RecyclerViewAdapterProductEmployee(List<Product> ProductList, Context context) {
        this.ProductList = ProductList;
        this.ProductListFilter = ProductList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return ProductListFilter.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        Product c = ProductListFilter.get(i);
        contactViewHolder.productName.setText(c.name);
        contactViewHolder.productNo.setText(c.no_);
        contactViewHolder.productPrice.setText(Utils.formatFloat(c.price) + context.getString(R.string.money));
        contactViewHolder.productUnit.setText(c.unit);
        Glide.with(context).load(MyMethod.getUrlProductImage(c.imageUrl)).error(R.drawable.product_sample_btn).override(150,150).centerCrop().into(contactViewHolder.productStt);

        if (c.inventory == 0) {
            contactViewHolder.productInventory.setTextColor(Color.RED);
            contactViewHolder.productInventory.setText(context.getString(R.string.out_of_stock));
        } else {
            contactViewHolder.productInventory.setTextColor(Color.BLUE);
            contactViewHolder.productInventory.setText(context.getString(R.string.inventory_title) + " " + c.inventory);
        }

    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_product, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView productName, productNo, productPrice, productUnit, productInventory;
        protected ImageView productStt;

        public ContactViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.product_name);
            productNo = (TextView) v.findViewById(R.id.product_no);
            productPrice = (TextView) v.findViewById(R.id.product_price);
            productStt = (ImageView) v.findViewById(R.id.product_stt);
            productUnit = (TextView) v.findViewById(R.id.product_unit);
            productInventory = (TextView) v.findViewById(R.id.txtInventoryEmployee);
            //Set clicked in cardview
        }
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = Utils.unAccent(constraint.toString()).toLowerCase();

            FilterResults results = new FilterResults();

            final List<Product> list = ProductList;

            int count = list.size();
            final ArrayList<Product> nlist = new ArrayList<>(count);

            Product filterableProduct;

            for (int i = 0; i < count; i++) {
                filterableProduct = list.get(i);
                if (Utils.unAccent(filterableProduct.name).toLowerCase().contains(filterString)) {
                    nlist.add(filterableProduct);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ProductListFilter = (ArrayList<Product>) results.values;
            notifyDataSetChanged();
        }

    }

    public void setItems(ArrayList<Product> list) {
        this.ProductList = list;
        this.ProductListFilter = list;
    }
}

