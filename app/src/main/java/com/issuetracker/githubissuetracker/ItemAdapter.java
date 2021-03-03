package com.issuetracker.githubissuetracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.issuetracker.githubissuetracker.model.Item;
import com.rey.material.widget.Button;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> items;
    private Context context;

    public ItemAdapter(Context applicationContext, List<Item> itemArrayList) {
        this.context = applicationContext;
        this.items = itemArrayList;
    }

    @Override
    public ItemAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_issues, viewGroup, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(ItemAdapter.ViewHolder viewHolder, int i) {
        viewHolder.title.setText(items.get(i).getTitle());
        viewHolder.comments.setText(items.get(i).getComments());
//        viewHolder.created_at.setText(items.get(i).getCreated_at());
        viewHolder.urlTV.setText(items.get(i).getUrl());
        if (items.get(i).getCreated_at() != null) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(items.get(i).getCreated_at());
                String formater = new SimpleDateFormat(" dd MMM yyyy (h:mm a)").format(date);
                viewHolder.created_at.setText(formater);
            } catch (ParseException e) {
                System.out.println("0000000000000000000000000000000000000000000000000000000000000000000000000");
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        }else {
            return -1;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title, comments, created_at, urlTV;
        private Button btnOpen;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.body);
            comments = view.findViewById(R.id.comments);
            created_at = view.findViewById(R.id.openedTv);
            urlTV = view.findViewById(R.id.urlTV);
            btnOpen = view.findViewById(R.id.btnOpen);

            btnOpen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlTV.getText().toString()));
                    browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                }
            });
        }
    }

}