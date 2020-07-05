package com.example.inf0251atrabalho3.ui.historic;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.inf0251atrabalho3.R;
import com.example.inf0251atrabalho3.database.DBSQLiteHelper;
import com.example.inf0251atrabalho3.ui.historic.adapter.HistoricAdapter;
import com.example.inf0251atrabalho3.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HistoricFragment extends Fragment {

    private DBSQLiteHelper db;
    private SharedPreferencesUtil preferences;

    private Spinner spnHistoric;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_historic, container, false);

        Context context = getContext();
        db = new DBSQLiteHelper(context);
        preferences = SharedPreferencesUtil.getInstance(context);

        findViews(root);

        initSpinner(context);

        initRecyclerView(context);

        return root;
    }

    private void findViews(View root) {

        spnHistoric = root.findViewById(R.id.spn_historic);
        recyclerView = root.findViewById(R.id.recycler_view);
    }

    private void initSpinner(final Context context) {

        List<SpinnerItemHistoric> lsItens = new ArrayList<>();

        Calendar cal7 = Calendar.getInstance();
        cal7.add(Calendar.DAY_OF_MONTH, -7);
        lsItens.add(new SpinnerItemHistoric(cal7.getTimeInMillis(), "Últimos 7 dias"));
        Calendar cal30 = Calendar.getInstance();
        cal30.add(Calendar.DAY_OF_MONTH, -30);
        lsItens.add(new SpinnerItemHistoric(cal30.getTimeInMillis(), "Últimos 30 dias"));
        Calendar cal90 = Calendar.getInstance();
        cal90.add(Calendar.DAY_OF_MONTH, -90);
        lsItens.add(new SpinnerItemHistoric(cal90.getTimeInMillis(), "Últimos 90 dias"));
        Calendar cal365 = Calendar.getInstance();
        cal365.add(Calendar.DAY_OF_MONTH, -365);
        lsItens.add(new SpinnerItemHistoric(cal365.getTimeInMillis(), "Últimos 365 dias"));
        lsItens.add(new SpinnerItemHistoric(0, "Todo histórico"));

        // Create an ArrayAdapter
        ArrayAdapter<SpinnerItemHistoric> spnAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, lsItens);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnHistoric.setAdapter(spnAdapter);

        spnHistoric.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SpinnerItemHistoric item = (SpinnerItemHistoric)spnHistoric.getSelectedItem();
                //Toast.makeText(context, item.millis + " - " + item.label, Toast.LENGTH_SHORT).show();
                // Recarrega a lista de acordo com a selecao
                String currency = preferences.getValue("currency", "USD-BRL");
                HistoricAdapter photoAdapter = new HistoricAdapter(context, db.getHistoricByCurrencyAndDate(currency, item.millis));
                recyclerView.setAdapter(photoAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Nothing to do
            }
        });
    }

    private void initRecyclerView (Context context) {

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        //String currency = preferences.getValue("currency", "USD-BRL");
        //HistoricAdapter historicAdapter = new HistoricAdapter(context, db.getAllByCurrency(currency)); // pega o historico completo
        //recyclerView.setAdapter(historicAdapter);
    }

    // Inner Class

    private static class SpinnerItemHistoric {

        long millis;
        String label;

        SpinnerItemHistoric (long millis, String label) {
            this.millis = millis;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}