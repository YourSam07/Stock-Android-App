package com.example.webtech4;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StockDetails extends AppCompatActivity {
    private Menu menu;
    private MenuItem star;
    private NestedScrollView stockScrollView;
    private ProgressBar progressBar;
    private VolleySingleton volleySingleton;
    private JSONObject stockData;
    private JSONObject quoteData;
    private JSONObject portData;
    private JSONObject favData;
    private List<NewsModel> newsList = new ArrayList<>();
    private RecyclerView newsRecyclerView;
    private RecyclerView peersRecyclerView;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter viewPagerAdapter;
    private WebView mainChart;
    private WebView recomChart;
    private WebView epsChart;
    private NewsAdapter newsadapter;
    private StringAdapter peerAdapter;
    private int completedRequests = 0;
    private static final int TOTAL_REQUESTS = 8;
    private List<String> peerList = new ArrayList<>();
    private boolean inPortfolio = false;
    private boolean inFavorite = false;
    private String amount;
    private NumberFormat cf;
    private NumberFormat nf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stock_details);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        cf = NumberFormat.getCurrencyInstance();
        nf = NumberFormat.getInstance();
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        inFavorite = false;
        volleySingleton = VolleySingleton.getInstance(this);
        progressBar = findViewById(R.id.progressBar);
        stockScrollView = findViewById(R.id.stockScrollView);
        newsRecyclerView = findViewById(R.id.newsRecView);
        peersRecyclerView = findViewById(R.id.peers);
        tabLayout = findViewById(R.id.tabChartSec);
//        viewPager = findViewById(R.id.viewPager);
        mainChart = findViewById(R.id.mainChart);
        recomChart = findViewById(R.id.recomChart);
        epsChart = findViewById(R.id.epsChart);

        peersRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        newsadapter = new NewsAdapter(newsList, this);
        peerAdapter = new StringAdapter(this, peerList);
//        viewPagerAdapter = new ViewPagerAdapter(this);

//        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    tab.setIcon(R.drawable.chart_hour);
                    displayMainChart("file:///android_asset/hourchart.html");
                } else {
                    tab.setIcon(R.drawable.chart_historical);
                    displayMainChart("file:///android_asset/histchart.html");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0){
                    Log.d("RUNNNING TABS", "this is hour inactive");
                    tab.setIcon(R.drawable.chart_hour_inactive);
                } else {
                    Log.d("RUNNNING TABS", "this is hist inactive");
                    tab.setIcon(R.drawable.chart_historical_inactive);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                tabLayout.getTabAt(position).select();
//            }
//        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("ticker"));

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView()).setAppearanceLightStatusBars(false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fetchAllData();
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu2, menu);
        this.menu = menu;
        star = menu.findItem(R.id.action_favorite);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_favorite){
            Log.d("Checking inFav", ""+inFavorite);
            if (inFavorite){
                volleySingleton.postData("api/watchlist/delete", getIntent().getStringExtra("ticker"), new VolleySingleton.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        inFavorite = false;
                        star.setIcon(R.drawable.star_border);
                        new CustomToast(getApplicationContext(), getIntent().getStringExtra("ticker") + " removed from Favorites");
//                        Toast.makeText(getApplicationContext(),
//                                getIntent().getStringExtra("ticker") + " removed from Favorites",
//                                Toast.LENGTH_LONG).show();
                        Log.d("RESPONSE in fav delete", ""+response);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("Error in fetching stock data", ""+errorMessage);
                    }
                });
            }
            else {
                volleySingleton.postData("api/watchlist", getIntent().getStringExtra("ticker"), new VolleySingleton.VolleyCallback() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        inFavorite = true;
                        star.setIcon(R.drawable.full_star);
                        new CustomToast(getApplicationContext(), getIntent().getStringExtra("ticker") + " is added to Favorite");
//                        Toast.makeText(getApplicationContext(),
//                                getIntent().getStringExtra("ticker") + " is added to Favorite",
//                                Toast.LENGTH_LONG).show();
                        Log.d("RESPONSE in fav add", ""+response);
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Log.e("Error in fetching stock data", ""+errorMessage);
                    }
                });
            }
        }
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchAllData() {
        completedRequests = 0;
        showLoadingSpinner();
        String symbol = getIntent().getStringExtra("ticker");
        volleySingleton.getData("api/portfolio/doc", new VolleySingleton.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONObject data = new JSONObject(response.toString());
                    amount = data.getString("amount");
                } catch (JSONException error){
                    error.printStackTrace();
                }
                Log.d("Response", response.toString());
                checkIfAllRequestsCompleted();
            }
            @Override
            public void onError(String errorMessage) {
                Log.e("Error", errorMessage);
                checkIfAllRequestsCompleted();
            }
        });
        volleySingleton.getData("stock/" + symbol, new VolleySingleton.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    stockData = new JSONObject(String.valueOf(response));
                    checkIfAllRequestsCompleted();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Error in fetching stock data", errorMessage);
            }
        });

        volleySingleton.getData("quote/" + symbol, new VolleySingleton.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    quoteData = new JSONObject(String.valueOf(response));
                    checkIfAllRequestsCompleted();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Error in fetching stock data", errorMessage);
            }
        });

        volleySingleton.getData2("news/" + symbol, new VolleySingleton.VolleyCallback2() {
            @Override
            public void onSuccess(JSONArray response) {
                try {
                    Log.d("response in news", String.valueOf(response));
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        String title = obj.getString("headline");
                        String desc = obj.getString("summary");
                        String src = obj.getString("source");
                        Long timestamp = obj.getLong("datetime");
                        String image = obj.getString("image");
                        String url = obj.getString("url");
                        newsList.add(new NewsModel(src, timestamp, title, desc, image, url));
                    }
                    newsRecyclerView.setAdapter(newsadapter);
                    newsRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    checkIfAllRequestsCompleted();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Error in fetching stock data", errorMessage);
            }
        });

        volleySingleton.getData2("peer/" + symbol, new VolleySingleton.VolleyCallback2() {
            @Override
            public void onSuccess(JSONArray response) {
                try {
                    Log.d("Response", String.valueOf(response));
                    for (int i = 0; i < response.length(); i++){
                        peerList.add((String) response.get(i));
                    }
                    peersRecyclerView.setAdapter(peerAdapter);
                    checkIfAllRequestsCompleted();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Error in fetching peer data", ""+errorMessage);
            }
        });

        volleySingleton.getData("insent/" + symbol, new VolleySingleton.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("Response", String.valueOf(response));
                TextView symbInTable = findViewById(R.id.inr0c0);
                TextView totMspr = findViewById(R.id.inr1c1);
                TextView totChange = findViewById(R.id.inr1c2);
                TextView posMspr = findViewById(R.id.inr2c1);
                TextView posChange = findViewById(R.id.inr2c2);
                TextView negMspr = findViewById(R.id.inr3c1);
                TextView negChange = findViewById(R.id.inr3c2);
                symbInTable.setText(symbol);
                try {
                    totMspr.setText(nf.format(response.getDouble("totalmspr")));
                    totChange.setText(nf.format(response.getDouble("totalchange")));
                    posMspr.setText(nf.format(response.getDouble("posmspr")));
                    posChange.setText(nf.format(response.getDouble("poschange")));
                    negMspr.setText(nf.format(response.getDouble("negmspr")));
                    negChange.setText(nf.format(response.getDouble("negchange")));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                checkIfAllRequestsCompleted();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Error in fetching stock data", errorMessage);
            }
        });

        volleySingleton.getData("api/portfolio", new VolleySingleton.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray portRes = response.getJSONArray("list");
                    for (int i = 0; i < portRes.length(); i++) {
                        JSONObject obj = portRes.getJSONObject(i);
                        if (obj.getString("symbol").equals(getIntent().getStringExtra("ticker"))){
                            portData = new JSONObject(String.valueOf(portRes.getJSONObject(i)));
                            inPortfolio = true;
                        }
                    }
                    checkIfAllRequestsCompleted();
                } catch (JSONException error){
                    Log.e("error", String.valueOf(error));
                    error.printStackTrace();
                }
            }
            @Override
            public void onError(String errorMessage) {
                Log.e("Error", errorMessage);
            }
        });

        volleySingleton.getData("api/watchlist", new VolleySingleton.VolleyCallback() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray watchRes = response.getJSONArray("list");
                    for (int i = 0; i < watchRes.length(); i++) {
                        JSONObject obj = watchRes.getJSONObject(i);
                        if (obj.getString("ticker").equals(getIntent().getStringExtra("ticker"))) {
                            inFavorite = true;
                        }
                    }
                    checkIfAllRequestsCompleted();
                } catch (JSONException error) {
                    Log.e("error", String.valueOf(error));
                    error.printStackTrace();
                }
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("Error", errorMessage);
            }
        });
    }

    private void displayFirstDiv() {
        Log.d("CHECKING IF DATA RECEIVED", String.valueOf(stockData));
        Log.d("CHECKING IF DATA RECEIVED", String.valueOf(quoteData));
        TextView ticker = findViewById(R.id.r0c0SD);
        TextView name = findViewById(R.id.r1c0SD);
        TextView price = findViewById(R.id.r0c1SD);
        TextView change =  findViewById(R.id.r1c1SD);
        ImageView logo = findViewById(R.id.logo);
        try {
            String tempTicker = stockData.getString("ticker");
            ticker.setText(tempTicker);
            name.setText(stockData.getString("name"));
            price.setText(cf.format(quoteData.getDouble("c")));
            Picasso.get().load(stockData.getString("logo")).into(logo);
            String temp = cf.format(quoteData.getDouble("d")) + " (" + nf.format(quoteData.getDouble("dp")) + "%)";
            change.setText(temp);
            int success = ContextCompat.getColor(getApplicationContext(), R.color.success);
            int error = ContextCompat.getColor(getApplicationContext(), R.color.error);
            if ((Double) quoteData.getDouble("dp") < 0) {
                change.setCompoundDrawablesWithIntrinsicBounds(R.drawable.trending_down, 0, 0, 0);
                change.setTextColor(error);
            } else {
                change.setCompoundDrawablesWithIntrinsicBounds(R.drawable.trending_up, 0, 0, 0);
                change.setTextColor(success);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void displayMainChart(String filename) {
        WebSettings webSettings = mainChart.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mainChart.clearCache(true);
        try {
            Log.d("TODT VALUE", "" + quoteData.getString("t"));
            ChartData data = new ChartData(getIntent().getStringExtra("ticker"), quoteData.getString("t"), quoteData.getDouble("dp"));
            Log.d("TODT VALUE FROM CLASS", "" + data.getTodt());
            mainChart.addJavascriptInterface(data, "ChartData");
            mainChart.loadUrl(filename);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void displayChart(String filename, int type) {
        if (type == 0) {
            WebSettings webSettings = recomChart.getSettings();
            webSettings.setJavaScriptEnabled(true);
            recomChart.clearCache(true);
            try {
                ChartData data = new ChartData(getIntent().getStringExtra("ticker"), quoteData.getString("t"), quoteData.getDouble("dp"));
                recomChart.addJavascriptInterface(data, "ChartData");
                recomChart.loadUrl(filename);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            WebSettings webSettings = epsChart.getSettings();
            webSettings.setJavaScriptEnabled(true);
            epsChart.clearCache(true);
            try {
                ChartData data = new ChartData(getIntent().getStringExtra("ticker"), quoteData.getString("t"), quoteData.getDouble("dp"));
                epsChart.addJavascriptInterface(data, "ChartData");
                epsChart.loadUrl(filename);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private void setDataToViews(){
        TextView ipo = findViewById(R.id.ipo);
        TextView industry = findViewById(R.id.industry);
        TextView webpage = findViewById(R.id.webpage);
        TextView openPrice = findViewById(R.id.statr0c0);
        TextView highPrice = findViewById(R.id.statr0c1);
        TextView lowPrice = findViewById(R.id.statr1c0);
        TextView prevClose = findViewById(R.id.statr1c1);

        try {
            ipo.setText(stockData.getString("ipo"));
            industry.setText(stockData.getString("finnhubIndustry"));
            webpage.setText(stockData.getString("weburl"));
            String op = nf.format(quoteData.getDouble("o"));
            String hp = nf.format(quoteData.getDouble("h"));
            String lp = nf.format(quoteData.getDouble("l"));
            String pc = nf.format(quoteData.getDouble("pc"));
            openPrice.setText("$"+op);
            highPrice.setText("$"+hp);
            lowPrice.setText("$"+lp);
            prevClose.setText("$"+pc);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setPortSecData() {
        Log.d("RUNNING", "FUNCION IS RUNNING");
        Log.d("RUNNING", ""+portData);
        TextView sharesOwned = findViewById(R.id.portr1c1);
        TextView avgCost = findViewById(R.id.portr2c1);
        TextView totCost = findViewById(R.id.portr3c1);
        TextView change = findViewById(R.id.portr4c1);
        TextView marketValue = findViewById(R.id.portr5c1);
        Button trade = findViewById(R.id.trade);
        trade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tempquant;
                try {
                    if (inPortfolio) {
                        tempquant = portData.getInt("quantity");
                    } else {
                        tempquant = 0;
                    }
                    CustomBuySellModal dialog = new CustomBuySellModal(StockDetails.this, getIntent().getStringExtra("ticker"), stockData.getString("name"), amount, quoteData.getDouble("c"), tempquant);
                    Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    dialog.setDismissListener(new CustomBuySellModal.DialogDismissListener() {
                        @Override
                        public void onDialogDismissed() {
                            dialog.setDialogClickListener(new CustomBuySellModal.DialogClickListener() {
                                @Override
                                public void onDialogResponse(boolean response, JSONObject responseObj) {
                                    Log.d("ONDIALOG", ""+response);
                                    if (response) {
                                        portData = responseObj;
                                        inPortfolio = response;
                                    } else{
                                        inPortfolio = response;
                                    }
                                    setPortSecData();
                                }
                            });
                        }
                    });
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        try {
            if (inPortfolio) {
                String so = nf.format(portData.getDouble("quantity"));
                String tc = nf.format(portData.getDouble("totCost"));
                String ac = nf.format(portData.getDouble("avgCost"));
                String c = nf.format(portData.getDouble("change"));
                String mv = nf.format(portData.getDouble("marketVal"));
                sharesOwned.setText(so);
                avgCost.setText("$"+ac);
                totCost.setText("$"+tc);
                change.setText("$"+c);
                marketValue.setText("$"+mv);
            } else {
                sharesOwned.setText("0");
                avgCost.setText(cf.format(0));
                totCost.setText(cf.format(0));
                change.setText(cf.format(0));
                marketValue.setText(cf.format(0));
            }

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }

    private void checkIfAllRequestsCompleted() {
        completedRequests++;
        Log.d("Completed Response", ""+String.valueOf(completedRequests));
        if (completedRequests == TOTAL_REQUESTS) {
            // All requests completed, hide loading spinner
            hideLoadingSpinner();
            stockScrollView.setVisibility(View.VISIBLE);
            MenuItem star = menu.findItem(R.id.action_favorite);
            star.setVisible(true);
            if (inFavorite){
                Log.d("CHECKING STAR", "this should be full star, This is in Favs");
                star.setIcon(R.drawable.full_star);
            } else {
                star.setIcon(R.drawable.star_border);
            }
            displayFirstDiv();
            displayMainChart("file:///android_asset/hourchart.html");
            setDataToViews();
            setPortSecData();
            displayChart("file:///android_asset/recomChart.html", 0);
            displayChart("file:///android_asset/epsChart.html", 1);
            return ;
        }
        stockScrollView.setVisibility(View.GONE);
    }

    private void showLoadingSpinner() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingSpinner() {
        progressBar.setVisibility(View.GONE);
    }
}