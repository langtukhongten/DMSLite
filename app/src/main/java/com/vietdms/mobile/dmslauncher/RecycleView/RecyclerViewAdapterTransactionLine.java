package com.vietdms.mobile.dmslauncher.RecycleView;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.vietdms.mobile.dmslauncher.CustomClass.LayoutLoadingManager;
import com.vietdms.mobile.dmslauncher.Fragment.RightFragment;
import com.vietdms.mobile.dmslauncher.Home;
import com.vietdms.mobile.dmslauncher.MyMethod;
import com.vietdms.mobile.dmslauncher.R;

import java.util.ArrayList;
import java.util.List;

import CommonLib.EventPool;
import CommonLib.EventType;
import CommonLib.TransactionLine;
import CommonLib.Utils;


/**
 * Created by ${LTG} was born ${10/12/1994}.
 */
public class RecyclerViewAdapterTransactionLine extends RecyclerView.Adapter<RecyclerViewAdapterTransactionLine.ContactViewHolder> implements Filterable {
    private List<TransactionLine> transactionLineList;
    private List<TransactionLine> transactionLineListFilter;
    private Context context;
    private ItemFilter mFilter = new ItemFilter();

    public RecyclerViewAdapterTransactionLine(List<TransactionLine> transactionLineList, Context context) {
        this.transactionLineList = transactionLineList;
        this.transactionLineListFilter = transactionLineList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return transactionLineListFilter.size();
    }

    @Override
    public void onBindViewHolder(ContactViewHolder contactViewHolder, int i) {
        final TransactionLine t = transactionLineListFilter.get(i);
        switch (t.id_transaction_define) {
            case 0:
                contactViewHolder.transactionName.setText("Mới");
                break;
            case 1:
                contactViewHolder.transactionName.setText("Ghé thăm");
                break;
            case 2:
                contactViewHolder.transactionName.setText("Đặt hàng");
                break;
            case 3:
                contactViewHolder.transactionName.setText("Đặt hàng qua điện thoại");
                break;
            case 4:
                contactViewHolder.transactionName.setText("Không đặt hàng \n" + t.note);
                break;
            case 5:
                contactViewHolder.transactionName.setText("Rời cửa hàng");
                break;
            case 6:
                contactViewHolder.transactionName.setText("Cập nhật tồn kho");
                break;
            case 7:
                contactViewHolder.transactionName.setText("Ghi chú \n" + t.note);
                break;
            case 8:
                contactViewHolder.transactionName.setText("Nhập tồn kho nhân viên");
                break;
            case 9:
                contactViewHolder.transactionName.setText("Hàng mẫu");
                break;
            case 10:
                contactViewHolder.transactionName.setText("Hàng trưng bày, poster");
                break;
            case 11:
                contactViewHolder.transactionName.setText("Phát sinh đơn hàng");
                break;
            case 12:
                contactViewHolder.transactionName.setText("Cập nhật vị trí");
                break;
            case 13:
                contactViewHolder.transactionName.setText("Cập nhật hình ảnh");
                break;
            case 14:
                contactViewHolder.transactionName.setText("Giao dịch \n" + t.note);
                break;
            case 15:
                contactViewHolder.transactionName.setText("Ghi nhận \n" + t.note);
                break;
            case 16:
                contactViewHolder.transactionName.setText("Phiếu khảo sát");
                break;
            case 17:
                contactViewHolder.transactionName.setText("Tin nhắn thông báo");
                break;
            case 18:
                contactViewHolder.transactionName.setText("Cập nhật tuyến bán hàng");
                break;
            case 99:
                contactViewHolder.transactionName.setText("Kết thúc giao dịch \n" + t.note);
                break;
            default:
                contactViewHolder.transactionName.setText("Chưa rõ");
                break;
        }

        if (t.id_transaction_define == 2 || t.id_transaction_define == 3 || t.id_transaction_define == 9 || t.id_transaction_define == 10) {
            contactViewHolder.transactionViewOrder.setImageResource(R.drawable.btn_order_list);
            contactViewHolder.transactionViewOrder.setVisibility(View.VISIBLE);
        } else if (t.id_transaction_define == 6) {
            contactViewHolder.transactionViewOrder.setImageResource(R.drawable.btn_inventory);
            contactViewHolder.transactionViewOrder.setVisibility(View.VISIBLE);
        } else if (t.id_transaction_define == 7) {
            contactViewHolder.transactionViewOrder.setImageResource(R.drawable.note_btn);
            contactViewHolder.transactionViewOrder.setVisibility(View.VISIBLE);
        } else if (t.id_transaction_define == 8) {
            contactViewHolder.transactionViewOrder.setImageResource(R.drawable.input_inventory_btn);
            contactViewHolder.transactionViewOrder.setVisibility(View.VISIBLE);
        } else if (t.id_transaction_define == 16) {
            contactViewHolder.transactionViewOrder.setImageResource(R.drawable.survey_btn);
            contactViewHolder.transactionViewOrder.setVisibility(View.VISIBLE);
        } else contactViewHolder.transactionViewOrder.setVisibility(View.GONE);

        if (t.latitude > 0) {

            if (t.urlImage > 0) {
                MyMethod.isHasImage = true;
                contactViewHolder.transactionViewID.setImageResource(R.drawable.btn_map_photo);
            } else {
                MyMethod.isHasImage = false;
                contactViewHolder.transactionViewID.setImageResource(R.drawable.btn_map);
            }
            contactViewHolder.transactionViewID.setVisibility(View.VISIBLE);
        } else contactViewHolder.transactionViewID.setVisibility(View.GONE);
        contactViewHolder.transactionDate.setText(Utils.long2String(t.create_date));
        i++;
        TextDrawable photoDrawable = TextDrawable.builder()
                .buildRound(i + "", Color.parseColor("#FFC000")); // radius in px
        contactViewHolder.transactionStt.setImageDrawable(photoDrawable);
        contactViewHolder.transactionEmployee.setText(t.name_employee);
        contactViewHolder.transactionViewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (t.id_transaction_define == 16) {
                    Toast.makeText(context, "Xem lại khảo sát", Toast.LENGTH_SHORT).show();
                    MyMethod.idCampaign = t.id_ExtNo_;
                    MyMethod.rootCustomer = Integer.parseInt(t.note.split("@")[1]);
                    MyMethod.idCustomer = Integer.parseInt(t.note.split("@")[2]);

                    EventPool.control().enQueue(new EventType.EventLoadSurveyDataByIDRequest(t.id_ExtNo_, MyMethod.idCustomer, MyMethod.rootCustomer));
                } else if (t.id_transaction_define == 7) {
                    new AlertDialog.Builder(context)
                            .setTitle(t.name_employee + "\n" + Utils.long2String(t.create_date))
                            .setMessage(t.note)
                            .setPositiveButton(context.getString(R.string.close), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(R.drawable.note_btn)
                            .show();
                } else {
                    Home.orderDetailArrayList.clear();
                    Home.orderListProductAdapter.notifyDataSetChanged();
                    if (t.id_transaction_define == 6)
                        RightFragment.updateInventoryDetailView(context);
                    else RightFragment.updateOrderDetailView(context);
                    LayoutLoadingManager.Show_OnLoading(Home.loadingOrderDetail, context.getString(R.string.load_order_detail), 30);
                    EventPool.control().enQueue(new EventType.EventLoadOrderDetailsRequest(t.id_ExtNo_));
                    MyMethod.isOrderInTransactionLine = true;
                    if (MyMethod.isVisible(Home.bindingRight.inStore.linearInStore)) {
                        MyMethod.isOpenTransactionLineFromStore = true;
                    } else {
                        MyMethod.isOpenTransactionLineFromStore = false;
                    }
                    Home.LayoutMyManager.ShowLayout(RightFragment.Layouts.OrderDetail);
                }
            }
        });
        contactViewHolder.transactionViewID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RightFragment.googleMap != null) RightFragment.googleMap.clear();
                if (MyMethod.isHasImage) {
                    // Xử lí xem hình ảnh và vị trí
                    MyMethod.isMapViewImageLocation = true;
                    MyMethod.isTransactionMapView = false;
                    Home.nowTransactionLine = t;
                    if (MyMethod.isVisible(Home.bindingRight.inStore.linearInStore)) {
                        MyMethod.isOpenTransactionLineFromStore = true;
                    } else {
                        MyMethod.isOpenTransactionLineFromStore = false;
                    }
                    Home.bindingHome.txtTile.setText(t.note);
                    Home.LayoutMyManager.ShowLayout(RightFragment.Layouts.MapCustomerCheckIn);
                    LayoutLoadingManager.Show_OnLoading(Home.loadingMapCustomerCheckIn, context.getString(R.string.load_map), 30);
                    EventPool.control().enQueue(new EventType.EventLoadLocationVisitedRequest(t.location_ref_id, t.id_employee));
                    Log.d("Pressed ", "transactionViewID isHasImage");
                } else {
                    // Xử lí xem vị trí
                    MyMethod.isMapViewImageLocation = true;
                    Home.nowTransactionLine = t;
                    if (MyMethod.isVisible(Home.bindingRight.inStore.linearInStore)) {
                        MyMethod.isOpenTransactionLineFromStore = true;
                    } else {
                        MyMethod.isOpenTransactionLineFromStore = false;
                    }
                    Home.bindingHome.txtTile.setText(t.note);
                    Home.LayoutMyManager.ShowLayout(RightFragment.Layouts.MapCustomerCheckIn);
                    LayoutLoadingManager.Show_OnLoading(Home.loadingMapCustomerCheckIn, context.getString(R.string.load_map), 30);
                    EventPool.control().enQueue(new EventType.EventLoadLocationVisitedRequest(t.location_ref_id, t.id_employee));
                    Log.d("Pressed ", "transactionViewID isHasn'tImage");
                }
            }
        });
    }

    @Override
    public ContactViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int i) {
        final View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_transaction_line, viewGroup, false);
        return new ContactViewHolder(itemView);
    }


    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        protected TextView transactionName, transactionDate, transactionEmployee;
        protected ImageButton transactionViewID, transactionViewOrder;
        protected ImageView transactionStt;

        public ContactViewHolder(View v) {
            super(v);
            transactionEmployee = (TextView) v.findViewById(R.id.transaction_line_employee);
            transactionStt = (ImageView) v.findViewById(R.id.transaction_line_stt);
            transactionName = (TextView) v.findViewById(R.id.transaction_line_name);
            transactionDate = (TextView) v.findViewById(R.id.transaction_line_time);
            transactionViewID = (ImageButton) v.findViewById(R.id.transaction_line_view_map);
            transactionViewOrder = (ImageButton) v.findViewById(R.id.transaction_line_view_order);
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

            final List<TransactionLine> list = transactionLineList;

            int count = list.size();
            final ArrayList<TransactionLine> nlist = new ArrayList<>(count);

            TransactionLine filterableTransaction;

            for (int i = 0; i < count; i++) {
                filterableTransaction = list.get(i);
                if (Utils.unAccent(filterableTransaction.note).toLowerCase().contains(filterString)) {
                    nlist.add(filterableTransaction);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            transactionLineListFilter = (ArrayList<TransactionLine>) results.values;
            notifyDataSetChanged();
        }

    }

    public void setItems(ArrayList<TransactionLine> list) {
        this.transactionLineList = list;
        this.transactionLineListFilter = list;
    }
}

