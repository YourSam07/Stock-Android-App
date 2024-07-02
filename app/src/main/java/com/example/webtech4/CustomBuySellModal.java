package com.example.webtech4;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Objects;

public class CustomBuySellModal extends Dialog {
    private String name;
    private String amount;
    private String ticker;
    private int actualQuantity;
    private Double cp;
    private Context ctx;
    private VolleySingleton volleySingleton;
    private DialogDismissListener dismissListener;
    private DialogClickListener listener;

    public CustomBuySellModal(Context context, String ticker, String name, String amount, Double cp, int aquant) {
        super(context);
        this.ctx = context;
        this.name = name;
        this.amount = amount;
        this.ticker = ticker;
        this.actualQuantity= aquant;

        this.cp = cp;
        this.volleySingleton = VolleySingleton.getInstance(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set custom dialog layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.buy_sell_modal);

        TextView header = findViewById(R.id.header);
        EditText editNumber = findViewById(R.id.quantity);
        TextView dynamic = findViewById(R.id.dynamic);
        TextView currStatus = findViewById(R.id.currStatus);
        Button Buy = findViewById(R.id.buy);
        Button Sell = findViewById(R.id.sell);

        String temp ="0*$" + String.valueOf(cp) + "/share = 0.00";
        // Set the multiplied number to multipliedEditText
        dynamic.setText(String.valueOf(temp));

//        editNumber.setText(String.valueOf(actualQuantity));

        String headerText = "Trade " + name + " shares";
        String currStat = "$" + amount + " to buy " + ticker;

        header.setText(headerText);
        currStatus.setText(currStat);
        editNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No implementation needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // No implementation needed
                
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Update the multipliedEditText when inputEditText text changes
                try {
                    // Get the number from inputEditText
                    int quant = Integer.parseInt(s.toString());

                    // Multiply the number by 10
                    Double multipliedNumber = quant * cp;

                    String temp = String.valueOf(quant) + "$" + String.valueOf(cp) + "/share = " + String.valueOf(formatDouble(multipliedNumber));
                    // Set the multiplied number to multipliedEditText
                    dynamic.setText(String.valueOf(temp));
                    Buy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (quant == 0){
                                new CustomToast(ctx, "Please enter a valid amount");
//                                Toast.makeText(ctx,
//                                        "Please enter a valid amount",
//                                        Toast.LENGTH_LONG).show();
                                return ;
                            } else if ((quant * cp) <= Double.parseDouble(String.valueOf(amount))) {
                                JSONObject obj = new JSONObject();
                                try {
                                    obj.put("symbol", ticker);
                                    obj.put("quantity", quant);
                                    obj.put("rate", cp);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                volleySingleton.postData2("api/portfolio", obj, new VolleySingleton.VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        Log.d("RESPONSE after adding portfolio", String.valueOf(response));
                                        try {
                                            JSONArray portRes = response.getJSONArray("list");
                                            Log.d("RUNNING in Volley", ""+portRes);
                                            for (int i = 0; i < portRes.length(); i++) {
                                                JSONObject obj = portRes.getJSONObject(i);
                                                if (obj.getString("symbol").equals(ticker)){
                                                    JSONObject portData = new JSONObject(String.valueOf(portRes.getJSONObject(i)));
                                                    if (listener != null) {
                                                        listener.onDialogResponse(true, portData);
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }

                                        String type = "buy";
                                        SuccessModal dialog = new SuccessModal(ctx, ticker, String.valueOf(quant), type);
                                        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialog.show();
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        Log.e("Error in fetching stock data", "" + errorMessage);
                                    }
                                });
                            } else {
                                new CustomToast(ctx, "Not enough money to buy");
//                                Toast.makeText(ctx,
//                                        "Not enough money to buy",
//                                        Toast.LENGTH_LONG).show();
                                return ;
                            }

                            if (dismissListener != null) {
                                dismissListener.onDialogDismissed();
                            }

                            // Dismiss the dialog
                            dismiss();
                        }
                    });

                    Sell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (quant == 0){
                                new CustomToast(ctx, "Cannot sell non-positive shares");
//                                Toast.makeText(ctx,
//                                        "Cannot sell non-positive shares",
//                                        Toast.LENGTH_LONG).show();
                                return ;
                            } else if (quant <= actualQuantity) {
                                Log.d("RESPONSE", "Reaching here");
                                JSONObject obj = new JSONObject();
                                try {
                                    obj.put("symbol", ticker);
                                    obj.put("quantity", quant);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                volleySingleton.sellStock("api/portfolio", obj, new VolleySingleton.VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject response) {
                                        Log.d("RESPONSE after selling portfolio", String.valueOf(response));
                                        try {
                                            JSONArray portRes = response.getJSONArray("list");
                                            Log.d("RUNNING in Volley", ""+portRes);
                                            if (portRes.length() == 0) {
                                                if (listener != null) {
                                                    listener.onDialogResponse(false, new JSONObject());
                                                }
                                            } else {
                                                for (int i = 0; i < portRes.length(); i++) {
                                                    JSONObject obj = portRes.getJSONObject(i);
                                                    if (obj.getString("symbol").equals(ticker)) {
                                                        JSONObject portData = new JSONObject(String.valueOf(portRes.getJSONObject(i)));
                                                        if (listener != null) {
                                                            listener.onDialogResponse(true, portData);
                                                        }
                                                    } else {
                                                        if (listener != null) {
                                                            listener.onDialogResponse(false, new JSONObject());
                                                        }
                                                    }
                                                }
                                            }
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                        String type = "sell";
                                        SuccessModal dialog = new SuccessModal(ctx, ticker, String.valueOf(quant), type);
                                        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialog.show();
                                    }

                                    @Override
                                    public void onError(String errorMessage) {
                                        Log.e("Error in fetching stock data", "" + errorMessage);
                                    }
                                });
                            } else {
                                new CustomToast(ctx, "Not enough shares to sell");
//                                Toast.makeText(ctx,
//                                        "Not enough shares to sell",
//                                        Toast.LENGTH_LONG).show();
                                return ;
                            }

                            if (dismissListener != null) {
                                dismissListener.onDialogDismissed();
                            }

                            // Dismiss the dialog
                            dismiss();
                        }
                    });
                } catch (NumberFormatException e) {
                    // Handle the case when input is not a valid number
                    dynamic.setText("");
                }
            }
        });
    }

    public static double formatDouble(double value) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return Double.parseDouble(decimalFormat.format(value));
    }

    public void setDismissListener(DialogDismissListener dismissListener) {
        this.dismissListener = dismissListener;
    }

    public interface DialogDismissListener {
        void onDialogDismissed();
    }

    public void setDialogClickListener(DialogClickListener listener) {
        this.listener = listener;
    }

    public interface DialogClickListener {
        void onDialogResponse(boolean response, JSONObject responseObj);
    }


}


