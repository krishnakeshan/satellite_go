package com.piedpipar.satellitego;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class PassesActivity extends AppCompatActivity {
    //Properties
    String satName;
    JSONArray passesJSONArray;
    RecyclerView passesRecyclerView;

    PassesListAdapter adapter;

    //Methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passes);

        //find views
        passesRecyclerView = findViewById(R.id.activity_passes_recycler_view);

        //get passes information
        try {
            satName = getIntent().getStringExtra("satName");
            passesJSONArray = new JSONArray(getIntent().getStringExtra("passesJSON"));

            //setup recycler view
            passesRecyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            passesRecyclerView.setLayoutManager(layoutManager);

            adapter = new PassesListAdapter();
            passesRecyclerView.setAdapter(adapter);
        } catch (Exception e) {
            return;
        }
    }

    class PassesListAdapter extends RecyclerView.Adapter<PassListViewItem> {
        //Properties

        //Methods
        @NonNull
        @Override
        public PassListViewItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(PassesActivity.this).inflate(R.layout.view_holder_pass, parent, false);
            return new PassListViewItem(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PassListViewItem holder, int position) {
            try {
                JSONObject passObject = passesJSONArray.getJSONObject(position);

                //bind views
                holder.satName.setText("Satellite Name: " + satName);
                holder.startAz.setText("Start Azimuth: " + passObject.getInt("startAz"));
                holder.startTime.setText("Start UTC: " + passObject.getInt("startUTC"));
                holder.startAz.setText("Start Elevation: " + passObject.getInt("startEl"));
                holder.endAz.setText("End Azimuth: " + passObject.getInt("endAz"));
                holder.endTime.setText("End UTC: " + passObject.getInt("endUTC"));
                holder.endEl.setText("End Elevation: " + passObject.getInt("endEl"));
            } catch (Exception e) {
                //do something
            }
        }

        @Override
        public int getItemCount() {
            return passesJSONArray.length();
        }
    }

    class PassListViewItem extends RecyclerView.ViewHolder {
        //Properties
        TextView satName;
        TextView startTime;
        TextView startAz;
        TextView startEl;
        TextView endTime;
        TextView endAz;
        TextView endEl;

        //Methods
        public PassListViewItem(@NonNull View itemView) {
            super(itemView);

            //find views
            satName = itemView.findViewById(R.id.view_holder_pass_satellite_name_text_view);
            startTime = itemView.findViewById(R.id.view_holder_pass_satellite_start_time);
            startAz = itemView.findViewById(R.id.view_holder_pass_satellite_start_azimuth);
            startEl = itemView.findViewById(R.id.view_holder_pass_satellite_start_elevation);
            endTime = itemView.findViewById(R.id.view_holder_pass_satellite_end_time);
            endAz = itemView.findViewById(R.id.view_holder_pass_satellite_end_azimuth);
            endEl = itemView.findViewById(R.id.view_holder_pass_satellite_end_elevation);
        }
    }
}
