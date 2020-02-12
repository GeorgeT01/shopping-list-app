package com.gea.shoppinglist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;


public class ProductAdapter extends RecyclerView.Adapter<RecyclerViewHolder> implements Filterable {

    private Database database;
    private Context context;
    private List<ProductModel> data;
    private List<ProductModel> dataFilterd;
    private Intent intent;

    // constructor to initialize context and data sent from MainActivity
    public ProductAdapter(Context context, List<ProductModel> data){
        this.context=context;
        this.data = data;
        this.dataFilterd = new ArrayList<>(data);
        this.database = new Database(context);
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.list_items, parent,false);

        return new RecyclerViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, final int position) {
        // Get current position of item in recycler view to bind data and assign values from list

        final ProductModel productModel = data.get(position);
        final String id = productModel.getId();
        final String name = productModel.getName();
        final String amount = productModel.getAmount();
        final String checkStatus = productModel.getChecked();

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.item_name.setPaintFlags(holder.item_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    database.updateListItem(id, "true");
                } else {
                    holder.item_name.setPaintFlags(holder.item_name.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                    database.updateListItem(id, "false");
                }
            }
        });


        if(checkStatus.equals("true")){
            holder.item_name.setPaintFlags(holder.item_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.checkBox.setChecked(true);
        }else{
            holder.item_name.setPaintFlags(holder.item_name.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            holder.checkBox.setChecked(false);
        }

        holder.item_name.setText(name);
        holder.item_amount.setText(amount);


        holder.setOnClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClcik) {
                if(isLongClcik){

                }
            }
        });

    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public ProductModel getItem(int position){
        return  data.get(position);
    }
    @Override
    public Filter getFilter() {

        return filter;
    }
    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<ProductModel> filterdList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0){
                filterdList.addAll(dataFilterd);
            }else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (ProductModel productModel: dataFilterd){
                    if (productModel.getName().toLowerCase().contains(filterPattern)){
                        filterdList.add(productModel);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filterdList;

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            data.clear();
            data.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };
    public void removeItem(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(int position, ProductModel productModel){
        data.add((position), productModel);
        notifyDataSetChanged();
    }
}
class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    private ItemClickListener itemClickListener;
    TextView item_name, item_amount;
    CheckBox checkBox;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        item_name = itemView.findViewById(R.id.name);
        item_amount= itemView.findViewById(R.id.amount);
        checkBox = itemView.findViewById(R.id.checkbox);


        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }
    public void setOnClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;

    }
    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(),false);
    }

    @Override
    public boolean onLongClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), true);
        return true;
    }
}
