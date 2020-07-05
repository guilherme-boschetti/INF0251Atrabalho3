package com.example.inf0251atrabalho3.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.inf0251atrabalho3.R;
import com.example.inf0251atrabalho3.apiservice.IApiService;
import com.example.inf0251atrabalho3.apiservice.RetrofitClient;
import com.example.inf0251atrabalho3.database.DBSQLiteHelper;
import com.example.inf0251atrabalho3.model.Moeda;
import com.example.inf0251atrabalho3.repository.Currency;
import com.example.inf0251atrabalho3.util.AndroidUtil;
import com.example.inf0251atrabalho3.AvailableCurrencies;
import com.example.inf0251atrabalho3.util.SharedPreferencesUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.internal.EverythingIsNonNull;

public class HomeFragment extends Fragment {

    private DBSQLiteHelper db;
    private SharedPreferencesUtil preferences;

    private ProgressDialog progressDialog;
    private TextView txtApiFailed;
    private TextView txtCurrentCurrency;
    private TextView txtBid;
    private TextView txtAsk;

    private String currency;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        Context context = getContext();
        db = new DBSQLiteHelper(context);
        preferences = SharedPreferencesUtil.getInstance(context);
        currency = preferences.getValue("currency", "USD-BRL");
        initViews(root);
        if (AndroidUtil.isNetworkAvailable(context)) {
            callApi();
        } else {
            Toast.makeText(getContext(), getString(R.string.no_network_available), Toast.LENGTH_SHORT).show();
            loadLastValue();
        }

        return root;
    }

    private void initViews(View root) {

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);

        txtApiFailed = root.findViewById(R.id.txt_api_failed);
        txtCurrentCurrency = root.findViewById(R.id.txt_currency);
        txtBid = root.findViewById(R.id.txt_bid);
        txtAsk = root.findViewById(R.id.txt_ask);

        txtCurrentCurrency.setText(getString(R.string.current_currency, AvailableCurrencies.getCurrencyName(currency)));
    }

    private void callApi() {

        try {
            progressDialog.show();

            IApiService apiService = RetrofitClient.createService(IApiService.class);

            Call<List<Currency>> call = apiService.getCurrency(currency);
            call.enqueue(new Callback<List<Currency>>() {
                @Override
                @EverythingIsNonNull
                public void onResponse(Call<List<Currency>> call, Response<List<Currency>> response) {
                    if (response.code() >= 200 && response.code() <= 299) {
                        List<Currency> currencyResponse = response.body();
                        if (currencyResponse != null && !currencyResponse.isEmpty()) {
                            float bidResponse = currencyResponse.get(0).getBid();
                            float askResponse = currencyResponse.get(0).getAsk();
                            String bid = String.valueOf(bidResponse);
                            String ask = String.valueOf(askResponse);
                            bid = bid.length() > 6 ? bid.substring(0, 6) : bid; // mostrar no maximo 6 caracteres
                            ask = ask.length() > 6 ? ask.substring(0, 6) : ask; // mostrar no maximo 6 caracteres
                            txtBid.setText(bid.replace(".", ","));
                            txtAsk.setText(ask.replace(".", ","));

                            // Guardar valor no banco caso valor da api esteja atualizado
                            // ou caso não exista no banco
                            Moeda lastMoeda = db.getLastIncludedByCurrency(currency);
                            if (lastMoeda == null ||
                                    lastMoeda.getBidValue() != bidResponse || lastMoeda.getAskValue() != askResponse) {

                                String pattern = "dd/MM/yyyy HH:mm:ss";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                                String dateHour = simpleDateFormat.format(new Date());

                                Moeda moeda = new Moeda();
                                moeda.setBidValue(bidResponse);
                                moeda.setAskValue(askResponse);
                                moeda.setCurrency(currency);
                                moeda.setDateHourInclusion(dateHour);
                                moeda.setDateMillis(new Date().getTime());
                                db.add(moeda);
                            }
                        }
                    } else {
                        loadLastValue();
                    }
                    progressDialog.hide();
                }

                @Override
                @EverythingIsNonNull
                public void onFailure(Call<List<Currency>> call, Throwable t) {
                    progressDialog.hide();
                    loadLastValue();
                }
            });
        } catch (Exception e) {
            progressDialog.hide();
            loadLastValue();
        }
    }

    private void loadLastValue() {
        txtApiFailed.setVisibility(View.VISIBLE);

        // Buscar valor do banco e mostrar caso exista
        String currency = preferences.getValue("currency", "USD-BRL");
        Moeda lastMoeda = db.getLastIncludedByCurrency(currency);
        if (lastMoeda != null) {
            String bid = String.valueOf(lastMoeda.getBidValue());
            String ask = String.valueOf(lastMoeda.getAskValue());
            bid = bid.length() > 6 ? bid.substring(0, 6) : bid; // mostrar no maximo 6 caracteres
            ask = ask.length() > 6 ? ask.substring(0, 6) : ask; // mostrar no maximo 6 caracteres
            txtBid.setText(bid.replace(".", ","));
            txtAsk.setText(ask.replace(".", ","));
        } else {
            txtBid.setText("0");
            txtAsk.setText("0");
        }
    }
}