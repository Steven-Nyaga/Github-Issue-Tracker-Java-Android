package com.issuetracker.githubissuetracker.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import com.issuetracker.githubissuetracker.ItemAdapter;
import com.issuetracker.githubissuetracker.R;
import com.issuetracker.githubissuetracker.api.Client;
import com.issuetracker.githubissuetracker.api.Service;
import com.issuetracker.githubissuetracker.model.Item;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, CommentDialog.CommentsDialogListener, DetailsDialog.DetailsDialogListener {

    private RecyclerView recyclerView;
    private TextView disconnected, comments, com, dateTodayTV;
    private EditText searchET;
    private RelativeLayout noVal, noNet;
    private LinearLayout horizontal;
    private Item item;
    ProgressDialog pd;
    private SwipeRefreshLayout swipeRefresh;
    private List<Item> items;
    private List<Item> filterList = new ArrayList<>();
    private List<Item> filterDateList = new ArrayList<>();
    private List<Item> filterWeekList = new ArrayList<>();
    private List<Item> filterMonthList = new ArrayList<>();
    private List<Item> filterCommentsList = new ArrayList<>();
    private ItemAdapter itemAdapter;
    private String userJina = "", repoJina = "";

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.new_blue, this.getTheme()));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.new_blue));
        }

        initViews();

        searchET = findViewById(R.id.searchSV);

        if (isOnline()) {
            noNet.setVisibility(View.GONE);
            if (userJina.isEmpty() && repoJina.isEmpty()){
                noVal.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                searchET.setVisibility(View.GONE);
                horizontal.setVisibility(View.GONE);
            }else {
                showRecycler();
            }
        } else {
            noNet.setVisibility(View.VISIBLE);
            searchET.setVisibility(View.GONE);
            horizontal.setVisibility(View.GONE);
            searchET.setEnabled(false);
        }


        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                filterList.clear();

                if(s.toString().isEmpty()){
                    itemAdapter = new ItemAdapter(getApplicationContext(), items);
                    recyclerView.setAdapter(itemAdapter);
                    itemAdapter.notifyDataSetChanged();
                }else {
                    Filter(s.toString());
                }

            }
        });

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeColors(R.color.blue);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                loadJSON();
                loadDynamicJSON();
                Toast.makeText(MainActivity.this, "Github Issue Refresh", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void Filter(String string){
        for (Item item : items){
            if(item.getTitle().toLowerCase().contains(string.toLowerCase())){
                filterList.add(item);
            }
        }

        itemAdapter = new ItemAdapter(getApplicationContext(), filterList);
        recyclerView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
    }

    private void filterToday(String date){
        String formatter = "";
        for (Item item : items){
            String created_at = item.getCreated_at();
            try {
                Date dat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(created_at);
                formatter = new SimpleDateFormat("dd MMM yyyy").format(dat);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (formatter.contains(date)){
                filterDateList.add(item);
            }
            System.out.println(formatter);
        }
        itemAdapter = new ItemAdapter(getApplicationContext(), filterDateList);
        recyclerView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
    }

    private void filterWeek(){
        Date today = Calendar.getInstance().getTime();
        Date newDate = new Date(today.getTime() - 604800000);
        long newDateNum = newDate.getTime()/86400000;

        Date dat = null;
        for (Item item : items){
            String created_at = item.getCreated_at();
            try {
                dat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(created_at);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long newDat = dat.getTime() / 86400000;

            if (newDat >= newDateNum){
                filterWeekList.add(item);
            }
        }
        itemAdapter = new ItemAdapter(getApplicationContext(), filterWeekList);
        recyclerView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
    }

    private void filterMonth(String date){
        String formatter = "";
        for (Item item : items){
            String created_at = item.getCreated_at();
            try {
                Date dat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(created_at);
                formatter = new SimpleDateFormat("MMM yyyy").format(dat);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (formatter.contains(date)){
                filterDateList.add(item);
            }
        }
        itemAdapter = new ItemAdapter(getApplicationContext(), filterMonthList);
        recyclerView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
    }

    private void filterByComments(String num){

        filterCommentsList.clear();

        for (Item item : items){
            if (item.getComments().contains(num)){
                filterCommentsList.add(item);
            }
        }
        itemAdapter = new ItemAdapter(getApplicationContext(), filterCommentsList);
        recyclerView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
    }

    private void filterLifetime(){
        itemAdapter = new ItemAdapter(getApplicationContext(), items);
        recyclerView.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    private void initViews(){
        dateTodayTV = findViewById(R.id.dateTodayTV);
        String date = String.valueOf(android.text.format.DateFormat.format("EEEE, dd MMMM",
                new java.util.Date()));
        dateTodayTV.setText(date);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.smoothScrollToPosition(0);
//        loadJSON();
        comments = findViewById(R.id.comments);
        com = findViewById(R.id.com);
        if (comments != null && comments.toString().contains("1")){
            com.setText("Comment");
        }
        noVal = findViewById(R.id.noVal);
        noNet = findViewById(R.id.noNet);
        horizontal = findViewById(R.id.horizontal);
    }

    private void showRecycler(){
        searchET.setVisibility(View.VISIBLE);
        horizontal.setVisibility(View.VISIBLE);
        noVal.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        pd = new ProgressDialog(this);
        pd.setMessage("Fetching Github Issues...");
        pd.setCancelable(false);
        pd.show();
//        loadJSON();
        loadDynamicJSON();
    }

    private void loadJSON(){
        disconnected = findViewById(R.id.disconnected);

        try {
            Client client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<List<Item>> call = apiService.getItems();
            call.enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    items = response.body();
                    recyclerView.setAdapter(new ItemAdapter(getApplicationContext(), items));
                    recyclerView.smoothScrollToPosition(0);
                    swipeRefresh.setRefreshing(false);
                    System.out.println("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
                    System.out.println(response);
                    pd.hide();
                }

                @Override
                public void onFailure(Call<List<Item>> call, Throwable t) {

                }
            });
        } catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDynamicJSON(){
        disconnected = findViewById(R.id.disconnected);

        try {
            Client client = new Client();
            Service apiService = Client.getClient().create(Service.class);
            Call<List<Item>> call = apiService.getDynamicItems(userJina, repoJina);
            call.enqueue(new Callback<List<Item>>() {
                @Override
                public void onResponse(Call<List<Item>> call, Response<List<Item>> response) {
                    items = response.body();
                    recyclerView.setAdapter(new ItemAdapter(getApplicationContext(), items));
                    recyclerView.smoothScrollToPosition(0);
                    swipeRefresh.setRefreshing(false);
                    System.out.println("11111111111111111111111111111111111111111111111111111111111111111111111111111111");
                    System.out.println(response);
                    pd.hide();
                }

                @Override
                public void onFailure(Call<List<Item>> call, Throwable t) {

                }
            });
        } catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void showComPopup(View view) {
        openDialog();
    }

    private void openDialog() {
        CommentDialog commentDialog = new CommentDialog();
        commentDialog.show(getSupportFragmentManager(), "Comments Dialog");
    }

    @Override
    public void applyComments(String number) {
        if (!number.isEmpty()) {
            filterByComments(number);
        } else {
            Toast.makeText(MainActivity.this, "Insert Value", Toast.LENGTH_SHORT).show();
        }
    }

    public void showInfoPopup(View view) {
        openDetailsDialog();
    }

    private void openDetailsDialog() {
        DetailsDialog detailsDialog = new DetailsDialog();
        detailsDialog.show(getSupportFragmentManager(), "Details Dialog");
    }

    @Override
    public void applyDetails(String user, String repo) {
        userJina = "";
        repoJina = "";
        if (!user.isEmpty() && !repo.isEmpty()) {
            userJina = user;
            repoJina = repo;
            showRecycler();
        } else {
            Toast.makeText(MainActivity.this, "Insert Details", Toast.LENGTH_SHORT).show();
        }
    }

    public void showDatePopup(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.popup_dates);
        popupMenu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.today:
                String date = String.valueOf(android.text.format.DateFormat.format("dd MMM yyyy",
                        new java.util.Date()));
                filterToday(date);
                System.out.println(date);
                return true;
            case R.id.week:
                filterWeek();
                return true;
            case R.id.month:
                String dat = String.valueOf(android.text.format.DateFormat.format("MMM yyyy",
                        new java.util.Date()));
                filterMonth(dat);
                return true;
            case R.id.all:
                filterLifetime();
                return true;
            default:
                return false;
        }
    }

    //checking connectivity to the internet
    public boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            return false;
        }
        return true;
    }
}