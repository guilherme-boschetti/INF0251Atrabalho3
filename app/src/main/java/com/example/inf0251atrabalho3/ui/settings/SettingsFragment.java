package com.example.inf0251atrabalho3.ui.settings;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.inf0251atrabalho3.AvailableCurrencies;
import com.example.inf0251atrabalho3.R;
import com.example.inf0251atrabalho3.backgroundservice.CurrencyService;
import com.example.inf0251atrabalho3.util.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SettingsFragment extends Fragment {

    private SharedPreferencesUtil preferences;

    private Spinner spnCurrency;
    private Spinner spnTime;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        View root = inflater.inflate(R.layout.fragment_settings, container, false);

        Context context = getContext();
        preferences = SharedPreferencesUtil.getInstance(context);
        
        findViews(root);
        
        initSpnCurrency(context);
        initSpnTime(context);
        
        return root;
    }
    
    private void findViews(View root) {
        
        spnCurrency = root.findViewById(R.id.spn_currency);
        spnTime = root.findViewById(R.id.spn_time);
    }
    
    private void initSpnCurrency(Context context) {

        Map<String, String> moedasDisponiveis = AvailableCurrencies.getAvailableCurrencies();

        List<SpinnerItemCurrency> lsItens = new ArrayList<>();

        SpinnerItemCurrency spinnerItemSelection = null;

        String currencyCode = preferences.getValue("currency", "USD-BRL");
        for (Map.Entry<String, String> entry : moedasDisponiveis.entrySet()) {
            SpinnerItemCurrency spinnerItemCurrency = new SpinnerItemCurrency(entry.getKey(), entry.getValue());
            if (entry.getKey().equals(currencyCode))
                spinnerItemSelection = spinnerItemCurrency;
            lsItens.add(spinnerItemCurrency);
        }

        // Create an ArrayAdapter
        ArrayAdapter<SpinnerItemCurrency> spnAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, lsItens);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnCurrency.setAdapter(spnAdapter);

        // == Obtem a posição que deve ser selecionada ==
        int position = 0;
        if (spinnerItemSelection != null) {
            position = lsItens.indexOf(spinnerItemSelection);
        }
        spnCurrency.setSelection(position);
        // == ==

        spnCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SpinnerItemCurrency item = (SpinnerItemCurrency)spnCurrency.getSelectedItem();
                // Salva a moeda selecionada nas preferencias
                preferences.setValue("currency", item.code);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Nothing to do
            }
        });
    }
    
    private void initSpnTime(final Context context) {

        List<SpinnerItemTimeBackgroundConsult> lsItens = new ArrayList<>();

        lsItens.add(new SpinnerItemTimeBackgroundConsult(-1, "Não consultar"));
        //lsItens.add(new SpinnerItemTimeBackgroundConsult(10000, "10 segundos")); // 10 segundos para teste
        lsItens.add(new SpinnerItemTimeBackgroundConsult(60000, "1 minuto"));
        lsItens.add(new SpinnerItemTimeBackgroundConsult(600000, "10 minutos"));
        lsItens.add(new SpinnerItemTimeBackgroundConsult(1800000, "30 minutos"));
        lsItens.add(new SpinnerItemTimeBackgroundConsult(3600000, "1 hora"));
        lsItens.add(new SpinnerItemTimeBackgroundConsult(43200000, "12 horas"));
        lsItens.add(new SpinnerItemTimeBackgroundConsult(86400000, "24 horas"));

        // Create an ArrayAdapter
        ArrayAdapter<SpinnerItemTimeBackgroundConsult> spnAdapter = new ArrayAdapter<>(context, R.layout.support_simple_spinner_dropdown_item, lsItens);
        spnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnTime.setAdapter(spnAdapter);

        // == Obtem a posição que deve ser selecionada ==
        int position = 0;
        int timeMillis = preferences.getValue("time_background_consult", -1);
        for (int i=0; i<lsItens.size(); i++) {
            if (lsItens.get(i).time == timeMillis) {
                position = i;
                break;
            }
        }
        spnTime.setSelection(position);
        // == ==

        spnTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Integer time = ((SpinnerItemTimeBackgroundConsult)spnTime.getSelectedItem()).time;
                // Salva o tempo selecionado nas preferencias
                preferences.setValue("time_background_consult", time);
                Intent serviceIntent = new Intent(context, CurrencyService.class);
                //if (isServiceRunning(CurrencyService.class)) {
                context.stopService(serviceIntent);
                //}
                if (time > 0) {
                    context.startService(serviceIntent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Nothing to do
            }
        });
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        try {
            ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (serviceClass.getName().equals(service.service.getClassName())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    // Inner Class
    
    private static class SpinnerItemCurrency {
        
        String code;
        String name;

        SpinnerItemCurrency (String code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private static class SpinnerItemTimeBackgroundConsult {

        Integer time; // millis
        String label;

        SpinnerItemTimeBackgroundConsult (Integer time, String label) {
            this.time = time;
            this.label = label;
        }

        @Override
        public String toString() {
            return label;
        }
    }
}