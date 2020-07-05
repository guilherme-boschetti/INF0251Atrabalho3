package com.example.inf0251atrabalho3.ui.average;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.inf0251atrabalho3.AvailableCurrencies;
import com.example.inf0251atrabalho3.R;
import com.example.inf0251atrabalho3.database.DBSQLiteHelper;
import com.example.inf0251atrabalho3.model.Moeda;
import com.example.inf0251atrabalho3.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AverageFragment extends Fragment {

    private DBSQLiteHelper db;
    private SharedPreferencesUtil preferences;

    private Spinner spnAverage;
    private TextView txtCurrentCurrency;
    private TextView txtBid;
    private TextView txtAsk;

    private String currency;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View root = inflater.inflate(R.layout.fragment_average, container, false);

        Context context = getContext();
        db = new DBSQLiteHelper(context);
        preferences = SharedPreferencesUtil.getInstance(context);
        currency = preferences.getValue("currency", "USD-BRL");

        initViews(root);

        initSpinner(context);
        
        return root;
    }

    private void initViews(View root) {

        spnAverage = root.findViewById(R.id.spn_average);
        txtCurrentCurrency = root.findViewById(R.id.txt_currency);
        txtBid = root.findViewById(R.id.txt_bid);
        txtAsk = root.findViewById(R.id.txt_ask);

        txtCurrentCurrency.setText(getString(R.string.current_currency, AvailableCurrencies.getCurrencyName(currency)));
    }

    private void initSpinner(final Context context) {

        List<SpinnerItemAverage> lsItens = new ArrayList<>();

        Calendar cal7 = Calendar.getInstance();
        cal7.add(Calendar.DAY_OF_MONTH, -7);
        lsItens.add(new SpinnerItemAverage(cal7.getTimeInMillis(), "Últimos 7 dias"));
        Calendar cal30 = Calendar.getInstance();
        cal30.add(Calendar.DAY_OF_MONTH, -30);
        lsItens.add(new SpinnerItemAverage(cal30.getTimeInMillis(), "Últimos 30 dias"));
        Calendar cal90 = Calendar.getInstance();
        cal90.add(Calendar.DAY_OF_MONTH, -90);
        lsItens.add(new SpinnerItemAverage(cal90.getTimeInMillis(), "Últimos 90 dias"));
        Calendar cal365 = Calendar.getInstance();
        cal365.add(Calendar.DAY_OF_MONTH, -365);
        lsItens.add(new SpinnerItemAverage(cal365.getTimeInMillis(), "Últimos 365 dias"));
        lsItens.add(new SpinnerItemAverage(0, "Todos os valores salvos"));

        // Create an ArrayAdapter
        ArrayAdapter<SpinnerItemAverage> spnAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, lsItens);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnAverage.setAdapter(spnAdapter);

        spnAverage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SpinnerItemAverage item = (SpinnerItemAverage)spnAverage.getSelectedItem();
                //Toast.makeText(context, item.millis + " - " + item.label, Toast.LENGTH_SHORT).show();
                // Faz a media
                String currency = preferences.getValue("currency", "USD-BRL");
                List<Moeda> lsCurrencies = db.getHistoricByCurrencyAndDate(currency, item.millis);
                if (lsCurrencies != null && !lsCurrencies.isEmpty()) {
                    int count = lsCurrencies.size();
                    float sumBid = 0;
                    float sumAsk = 0;
                    for (Moeda moeda : lsCurrencies) {
                        sumBid += moeda.getBidValue();
                        sumAsk += moeda.getAskValue();
                    }
                    float averageBid = sumBid / count;
                    float averageAsk = sumAsk / count;

                    String bid = String.valueOf(averageBid);
                    String ask = String.valueOf(averageAsk);
                    bid = bid.length() > 6 ? bid.substring(0, 6) : bid; // mostrar no maximo 6 caracteres
                    ask = ask.length() > 6 ? ask.substring(0, 6) : ask; // mostrar no maximo 6 caracteres
                    txtBid.setText(bid.replace(".", ","));
                    txtAsk.setText(ask.replace(".", ","));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Nothing to do
            }
        });
    }
    
    // Inner Class

    private static class SpinnerItemAverage {

        long millis;
        String label;

        SpinnerItemAverage (long millis, String label) {
            this.millis = millis;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}