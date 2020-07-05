package com.example.inf0251atrabalho3.ui.historic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inf0251atrabalho3.AvailableCurrencies;
import com.example.inf0251atrabalho3.R;
import com.example.inf0251atrabalho3.model.Moeda;

import java.util.List;

public class HistoricAdapter extends RecyclerView.Adapter<HistoricAdapter.ViewHolder> {

    private Context context;
    private List<Moeda> moedaHistoricList;

    public HistoricAdapter(Context context, List<Moeda> moedaHistoricList) {
        this.context = context;
        this.moedaHistoricList = moedaHistoricList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.moeda_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final Moeda moeda = moedaHistoricList.get(position);
        viewHolder.setData(moeda);
    }

    @Override
    public int getItemCount() {
        return moedaHistoricList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtCurrencyCodeName;
        private TextView txtValueOfBuy;
        private TextView txtValueOfSale;
        private TextView txtCurrencyBid;
        private TextView txtCurrencyAsk;
        private TextView txtDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCurrencyCodeName = itemView.findViewById(R.id.txt_currency_code_name);
            txtValueOfBuy = itemView.findViewById(R.id.txt_value_of_buy);
            txtValueOfSale = itemView.findViewById(R.id.txt_value_of_sale);
            txtCurrencyBid = itemView.findViewById(R.id.txt_currency_bid);
            txtCurrencyAsk = itemView.findViewById(R.id.txt_currency_ask);
            txtDate = itemView.findViewById(R.id.txt_date);
        }

        private void setData(Moeda moeda) {
            String codeName = moeda.getCurrency() + " - " + AvailableCurrencies.getCurrencyName(moeda.getCurrency());
            String valueOfBuy = context.getString(R.string.value_of) + " " + context.getString(R.string.buy) + ":";
            String valueOfSale = context.getString(R.string.value_of) + " " + context.getString(R.string.sale) + ":";
            txtCurrencyCodeName.setText(codeName);
            txtValueOfBuy.setText(valueOfBuy);
            txtValueOfSale.setText(valueOfSale);
            String bid = String.valueOf(moeda.getBidValue());
            String ask = String.valueOf(moeda.getAskValue());
            bid = bid.length() > 6 ? bid.substring(0, 6) : bid; // mostrar no maximo 6 caracteres
            ask = ask.length() > 6 ? ask.substring(0, 6) : ask; // mostrar no maximo 6 caracteres
            txtCurrencyBid.setText(bid.replace(".", ","));
            txtCurrencyAsk.setText(ask.replace(".", ","));
            txtDate.setText(moeda.getDateHourInclusion());
        }
    }
}
