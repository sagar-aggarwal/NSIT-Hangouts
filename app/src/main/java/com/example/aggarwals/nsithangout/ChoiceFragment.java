package com.example.aggarwals.nsithangout;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.Toast;

/**
 * Created by AGGARWAL'S on 8/29/2015.
 */
public class ChoiceFragment extends Activity implements View.OnClickListener{

    private LinearLayout restaurant,
            cafes,
            clubs,
            malls,
            bowling,
            movie,
            waterpark,
            food,
            dhaba, // now as park linera layout
            search;
    private ScrollView scrollView;
    private int CHOICE_SEARCH = -1;
    public static final String CHOICE = "choice";
    public static final int CHOICE_INTENT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choice_layout);
        scrollView = (ScrollView)findViewById(R.id.choice_scroll);
        restaurant = (LinearLayout)findViewById(R.id.choice_resturant);
        cafes = (LinearLayout)findViewById(R.id.choice_cafes);
        clubs = (LinearLayout)findViewById(R.id.choice_clubs);
        malls = (LinearLayout)findViewById(R.id.choice_malls);
        bowling = (LinearLayout)findViewById(R.id.choice_bowling);
        movie = (LinearLayout)findViewById(R.id.choice_movie);
        waterpark = (LinearLayout)findViewById(R.id.choice_water_parks);
        food = (LinearLayout)findViewById(R.id.choice_food);
        dhaba = (LinearLayout)findViewById(R.id.choice_dhaba);
        search = (LinearLayout)findViewById(R.id.choice_search);

        restaurant.setOnClickListener(this);
        cafes.setOnClickListener(this);
        clubs.setOnClickListener(this);
        malls.setOnClickListener(this);
        bowling.setOnClickListener(this);
        movie.setOnClickListener(this);
        waterpark.setOnClickListener(this);
        food.setOnClickListener(this);
        dhaba.setOnClickListener(this);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChoiceFragment.this,AboutActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Animation animation = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        restaurant.setAnimation(animation);
        cafes.setAnimation(animation);
        clubs.setAnimation(animation);
        malls.setAnimation(animation);
        bowling.setAnimation(animation);
        movie.setAnimation(animation);
        waterpark.setAnimation(animation);
        food.setAnimation(animation);
        dhaba.setAnimation(animation);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this,ChoiceDisplay.class);
        switch (view.getId()){
            case R.id.choice_resturant:CHOICE_SEARCH = 0;
                        break;
            case R.id.choice_cafes :CHOICE_SEARCH = 1;
                    break;
            case R.id.choice_clubs:CHOICE_SEARCH = 2;
                break;
            case R.id.choice_malls:CHOICE_SEARCH = 3;
                break;
            case  R.id.choice_bowling:CHOICE_SEARCH = 4;
                break;
            case  R.id.choice_movie:CHOICE_SEARCH = 5;
                break;
            case  R.id.choice_water_parks:CHOICE_SEARCH = 7;
                break;
            case  R.id.choice_food:CHOICE_SEARCH = 6;
                break;
            case  R.id.choice_dhaba:CHOICE_SEARCH = 8;  // park linear layout
                break;
        }
        intent.putExtra(CHOICE, CHOICE_SEARCH);
        startActivityForResult(intent, CHOICE_INTENT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            setResult(RESULT_CANCELED);
            finish();
        }
        return true;
    }

}
