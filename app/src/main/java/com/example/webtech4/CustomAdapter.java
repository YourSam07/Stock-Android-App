package com.example.webtech4;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.text.NumberFormat;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.StockViewHolder>  {

    private List<Model> stockList;
    private OnItemClickListener listener;
    private Context context;
    private NumberFormat cf;
    private NumberFormat nf;

    public interface OnItemClickListener {
        void onItemClick(String itemName);
    }

    public CustomAdapter(List<Model> stockList, Context context, OnItemClickListener listener) {
        this.stockList = stockList;
        this.context = context;
        this.listener = listener;
        this.cf = NumberFormat.getCurrencyInstance();
        this.nf = NumberFormat.getInstance();
    }

    @NonNull
    @Override
    public StockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        return new StockViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull StockViewHolder holder, int position) {
        Model currentItem = stockList.get(position);
        holder.txtName.setText(currentItem.getName());
        holder.txtTicker.setText(currentItem.getTicker());
        holder.curPrice.setText(cf.format(currentItem.getC()));
        int success = ContextCompat.getColor(holder.itemView.getContext(), R.color.success);
        int error = ContextCompat.getColor(holder.itemView.getContext(), R.color.error);
        if (currentItem.getUpDown()) {
            holder.change.setCompoundDrawablesWithIntrinsicBounds(R.drawable.trending_up, 0, 0, 0);
            holder.change.setTextColor(success);
        } else {
            holder.change.setCompoundDrawablesWithIntrinsicBounds(R.drawable.trending_down, 0, 0, 0);
            holder.change.setTextColor(error);
        }
        String r1c1 = cf.format(currentItem.getD()) + " (" + nf.format(currentItem.getDp())+ "%)";
        holder.change.setText(r1c1);

        holder.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the name of the clicked item
                String itemName = currentItem.getTicker();

                // Pass the name to the onItemClick method of the listener
                listener.onItemClick(itemName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

    public static class StockViewHolder extends RecyclerView.ViewHolder {
        public TextView txtName;
        public TextView txtTicker;
        public TextView curPrice;
        public TextView change;
        public ImageView nextBtn;
        public StockViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.r1c0);
            txtTicker = itemView.findViewById(R.id.r0c0);
            curPrice = itemView.findViewById(R.id.r0c1);
            change = itemView.findViewById(R.id.r1c1);
            nextBtn = itemView.findViewById(R.id.rightArrowIcon);
        }
    }
}
