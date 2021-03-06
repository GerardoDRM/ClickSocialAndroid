package com.craftcode.android.clicksocial;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.craftcode.android.clicksocial.API.SuccessCases;
import com.craftcode.android.clicksocial.models.SuccessCase;
import com.craftcode.android.clicksocial.utils.GeneralConst;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SuccessCaseActivity extends AppCompatActivity {


    private String BASE_URL;
    private SuccessCases apiService;

    private String idpromo;
    private SuccessCase mSuccessCase;

    @Bind(R.id.header_img_poster)
    ImageView mHeaderImg;
    @Bind(R.id.title)
    TextView mTitle;
    @Bind(R.id.description)
    TextView mDescription;
    @Bind(R.id.creation)
    TextView mCreation;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @Bind(R.id.appbar)
    AppBarLayout appBarLayout;
    @Bind(R.id.coordinator)
    CoordinatorLayout coordinatorLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_case);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbarLayout.setTitle(" ");

        this.BASE_URL = GeneralConst.BASE_URL;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(SuccessCases.class);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            idpromo = arguments.getString(GeneralConst.PROMO_KEY);
            setUp();
        }
    }
    private void setUp() {
        if (mSuccessCase != null) {
            createView();
        } else {
            getInfo();
        }
    }

    private void getInfo() {

        Call<SuccessCase> call = apiService.getSuccessCase(idpromo);
        call.enqueue(new Callback<SuccessCase>() {

            @Override
            public void onResponse(Response<SuccessCase> response, Retrofit retrofit) {
                int statusCode = response.code();
                SuccessCase data = response.body();
                if (data == null) return;
                mSuccessCase = data;
                createView();
            }

            @Override
            public void onFailure(Throwable t) {
                if (!GeneralConst.checkNetwork(getApplicationContext()))
                    GeneralConst.showMessageConnection(getApplicationContext());
            }
        });

    }


    private void createView() {
        Picasso.with(this).load(mSuccessCase.getImg()).fit().centerCrop().tag(this).into(mHeaderImg);
        mTitle.setText(mSuccessCase.getTitle());
        mTitle.setTypeface(Typeface.createFromAsset(getResources().getAssets(), "Rosario-Regular.ttf"));

        mDescription.setText(mSuccessCase.getDescription());

        SimpleDateFormat sds = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        sds.setTimeZone(TimeZone.getDefault());
        long event_date = mSuccessCase.getCreation_date();
        final Date date_s = new Date(event_date * 1000L);
        mCreation.setText(sds.format(date_s));
    }



}
