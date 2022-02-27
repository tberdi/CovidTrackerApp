package com.example.android_final;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomePage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    CountryCodePicker countryCodePicker;
    TextView mtodaytotal,mtotal,mactive,mtodayactive,mrecovered,mtodayrecovered,mdeaths,mtodaydeaths;

    String country;
    TextView mfilter;
    Spinner spinner;
    String[] types={"cases","deaths","recovered","active"};
    private List<ModelClass> modelClassList;
    private List<ModelClass> modelClassList2;
    PieChart mpieChart;
    private RecyclerView recyclerView;

    private Button logout;
    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

   com.example.android_final.Adapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        getSupportActionBar().hide();
        countryCodePicker=findViewById(R.id.ccp);
        mtodayactive=findViewById(R.id.todayactive);
        mactive=findViewById(R.id.activecase);
        mdeaths=findViewById(R.id.totaldeath);
        mtodaydeaths=findViewById(R.id.todaydeath);
        mrecovered=findViewById(R.id.recoveredcase);
        mtodayrecovered=findViewById(R.id.todayrecovered);
        mtotal=findViewById(R.id.totalcase);
        mtodaytotal=findViewById(R.id.todaytotal);
        mpieChart=findViewById(R.id.piechart);
        spinner=findViewById(R.id.spinner);
        mfilter=findViewById(R.id.filter);
        recyclerView=findViewById(R.id.recyclerview);
        modelClassList=new ArrayList<>();
        modelClassList2=new ArrayList<>();

        spinner.setOnItemSelectedListener(this);
        ArrayAdapter arrayAdapter=new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(arrayAdapter);

        logout=(Button) findViewById(R.id.logoutBtn);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomePage.this, MainActivity.class));
            }
        });

        user=FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("Users");
        userID=user.getUid();

        final TextView nameTextView=(TextView) findViewById(R.id.username);
        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile= snapshot.getValue(User.class);

                if (userProfile!=null){
                    String name=userProfile.name;

                     nameTextView.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomePage.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

        ApiUtilities.getAPIInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList2.addAll(response.body());
                //adapter.notify
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });

        adapter=new Adapter(getApplicationContext(),modelClassList2);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        countryCodePicker.setAutoDetectedCountry(true);
        country=countryCodePicker.getSelectedCountryName();
        countryCodePicker.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                country=countryCodePicker.getSelectedCountryName();
                fetchdata();
            }
        });

        fetchdata();


    }

    private void fetchdata() {

        ApiUtilities.getAPIInterface().getcountrydata().enqueue(new Callback<List<ModelClass>>() {
            @Override
            public void onResponse(Call<List<ModelClass>> call, Response<List<ModelClass>> response) {
                modelClassList.addAll(response.body());
                for(int i=0; i<modelClassList.size(); i++)
                {
                    if(modelClassList.get(i).getCountry().equals(country))
                    {
                      mactive.setText((modelClassList.get(i).getActive()));
                      mtodaydeaths.setText((modelClassList.get(i).getTodayDeaths()));
                      mtodayrecovered.setText((modelClassList.get(i).getTodayRecovered()));
                      mtodaytotal.setText((modelClassList.get(i).getTodayCases()));
                      mtotal.setText((modelClassList.get(i).getCases()));
                      mdeaths.setText((modelClassList.get(i).getDeaths()));
                      mrecovered.setText((modelClassList.get(i).getRecovered()));


                      int active,total,recovered,deaths;

                      active=Integer.parseInt(modelClassList.get(i).getActive());
                      total=Integer.parseInt(modelClassList.get(i).getCases());
                      recovered=Integer.parseInt(modelClassList.get(i).getRecovered());
                      deaths=Integer.parseInt(modelClassList.get(i).getDeaths());
                      
                      updateGraph(active,total,recovered,deaths);



                    }
                }
            }

            @Override
            public void onFailure(Call<List<ModelClass>> call, Throwable t) {

            }
        });


    }

    private void updateGraph(int active, int total, int recovered, int deaths) {

        mpieChart.clearChart();
        mpieChart.addPieSlice(new PieModel("Confirm",total,Color.parseColor("#FFB701")));
        mpieChart.addPieSlice(new PieModel("Active",active,Color.parseColor("#FF4CAF50")));
        mpieChart.addPieSlice(new PieModel("Recovered",recovered,Color.parseColor("#38ACCD")));
        mpieChart.addPieSlice(new PieModel("Deaths",deaths,Color.parseColor("#F55c47")));
        mpieChart.startAnimation();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        String item=types[position];
        mfilter.setText(item);
        adapter.filter(item);

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}