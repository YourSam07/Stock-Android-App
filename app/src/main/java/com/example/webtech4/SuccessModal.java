package com.example.webtech4;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SuccessModal extends Dialog{
    private String quant;
    private String ticker;
    private String type;
    private Context ctx;

    public SuccessModal(Context context, String ticker, String quantity, String type) {
        super(context);
        this.ticker = ticker;
        this.quant = quantity;
        this.type = type;
        this.ctx = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set custom dialog layout
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.success_modal);

        TextView msg =  findViewById(R.id.message);
        if (type.equals("sell")) {
            msg.setText("You have successfully sold " + quant + " shares of " + ticker);
        } else {
            msg.setText("You have successfully bought " + quant + " shares of " + ticker);
        }

        Button closeBtn = findViewById(R.id.done);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
