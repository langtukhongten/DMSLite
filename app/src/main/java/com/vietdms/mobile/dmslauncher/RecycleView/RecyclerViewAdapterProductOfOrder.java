package com.vietdms.mobile.dmslauncher.RecycleView;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import CommonLib.OrderDetail;
import CommonLib.Product;
import CommonLib.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Admin on 5/24/2016.
 */
public class RecyclerViewAdapterProductOfOrder extends RecyclerView.Adapter<RecyclerViewAdapterProductOfOrder.ContactViewHolder> implements Filterable {
    private List<Product> ProductList;
    private List<Product> ProductListFilter;
    private static Context context;
    private ItemFilter mFilter = new ItemFilter();
    private int i;

    public RecyclerViewAdapterProductOfOrder(List<Product> ProductList, Context context) {
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
        contactViewHolder.productUnit.setText(c.unit);
        Glide.with(context).load(MyMethod.getUrlProductImage(c.imageUrl)).error(R.drawable.product_sample_btn).override(150, 150).centerCrop().into(contactViewHolder.productStt);
        contactViewHolder.productInventory.setText(c.inventory + "");
        if (Home.hashListQuantity.get(c.no_) != null) {
            int quantity = Home.hashListQuantity.get(c.no_) + getQuantity(Home.hashViewPromotion.get(c.no_));
            contactViewHolder.productQuantity.setText(quantity + "");
        } else {
            int quantity = getQuantity(Home.hashViewPromotion.get(c.no_));
            contactViewHolder.productQuantity.setText(quantity + "");
        }

        if (Home.hashListPrice.get(c.no_) != null)
            contactViewHolder.productPrice.setText(Home.hashListPrice.get(c.no_) + context.getString(R.string.money));
        else
            contactViewHolder.productPrice.setText(Utils.formatFloat(c.price) + context.getString(R.string.money));
        int quantity = getQuantityFromHash(c.no_);
        if (quantity > 0) {
            contactViewHolder.productQuantity.setText(quantity + "");
        }
    }

    private int getQuantityFromHash(String no_) {
        int result = 0;
        for (Integer i : Home.hashOrderLine.keySet()) {
            OrderDetail detail = Home.hashOrderLine.get(i);
            if (detail.itemNo_.equals(no_)) {
                result += detail.quantity;
            }
        }
        return result;
    }

    private int getQuantity(ArrayList<EditText> arrView) {
        //Lay so luong san phan nhap khuyen mai
        if (arrView == null) return 0;
        else {
            int result = 0;
            for (EditText editText : arrView) {
                if (!editText.getText().toString().isEmpty()) {
                    result += Integer.parseInt(editText.getText().toString());
                }
            }
            return result;
        }
    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_product_of_order, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView productName, productNo, productPrice, productUnit, productQuantity, productInventory;
        protected CircleImageView productStt;
        protected LinearLayout linearInventory;

        public ContactViewHolder(View v) {
            super(v);
            productName = (TextView) v.findViewById(R.id.custom_product_name);
            productNo = (TextView) v.findViewById(R.id.custom_product_no);
            productPrice = (TextView) v.findViewById(R.id.custom_product_price);
            productStt = (CircleImageView) v.findViewById(R.id.custom_product_photo);
            productUnit = (TextView) v.findViewById(R.id.custom_product_unit);
            productQuantity = (TextView) v.findViewById(R.id.custom_product_quantity);
            productInventory = (TextView) v.findViewById(R.id.txt_product_of_order_inventory);
            linearInventory = (LinearLayout) v.findViewById(R.id.linear_product_inventory);

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

