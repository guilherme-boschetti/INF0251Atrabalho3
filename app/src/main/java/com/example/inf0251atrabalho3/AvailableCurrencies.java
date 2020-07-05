package com.example.inf0251atrabalho3;

import java.util.HashMap;
import java.util.Map;

public class AvailableCurrencies {

    private static Map<String, String> moedasDisponiveis;

    public static void initMoedasDisponiveis() {
        moedasDisponiveis = new HashMap<>();

        moedasDisponiveis.put("USD-BRL", "Dólar Comercial");
        moedasDisponiveis.put("USDT-BRL", "Dólar Turismo");
        moedasDisponiveis.put("CAD-BRL", "Dólar Canadense");
        moedasDisponiveis.put("AUD-BRL", "Dólar Australiano");
        moedasDisponiveis.put("EUR-BRL", "Euro");
        moedasDisponiveis.put("GBP-BRL", "Libra Esterlina");
        moedasDisponiveis.put("ARS-BRL", "Peso Argentino");
        moedasDisponiveis.put("JPY-BRL", "Iene Japonês");
        moedasDisponiveis.put("CHF-BRL", "Franco Suíço");
        moedasDisponiveis.put("CNY-BRL", "Yuan Chinês");
        moedasDisponiveis.put("YLS-BRL", "Novo Shekel Israelense");
        moedasDisponiveis.put("BTC-BRL", "Bitcoin");
        moedasDisponiveis.put("LTC-BRL", "Litecoin");
        moedasDisponiveis.put("ETH-BRL", "Ethereum");
        moedasDisponiveis.put("XRP-BRL", "Ripple");
    }

    public static Map<String, String> getAvailableCurrencies() {
        return moedasDisponiveis;
    }

    public static String getCurrencyName(String currencyCode) {
        return moedasDisponiveis.get(currencyCode);
    }
}
