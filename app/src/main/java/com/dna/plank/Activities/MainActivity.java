package com.dna.plank.Activities;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;

import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dna.plank.extras.BottomNavigationBehavior;
import com.dna.plank.DB.DBscore;
import com.dna.plank.Fragments.HistoriqueFrag;
import com.dna.plank.Fragments.MenuFrag;
import com.dna.plank.Modeles.Historique;
import com.dna.plank.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private CardView b;
    private ImageView image;
    Animation translate;
    SQLiteDatabase db;
    private List<Historique> mhistoricList;
    DBscore dbHelper;
    TextView cate;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                case R.id.histo:


                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right, R.anim.exist_left, R.anim.slide_left, R.anim.exist_right);


                                    ft.replace(R.id.contenuFrag, new HistoriqueFrag());
                    //cate.setText("Historique");

                    ft.commit();
                    return true;

                case R.id.excer:
                    FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_right, R.anim.exist_left, R.anim.slide_left, R.anim.exist_right);
                    ft1.replace(R.id.contenuFrag, new MenuFrag());
                   // cate.setText("Exercises");

                    ft1.commit();

                    return true;
            }
            return false;
        }
    };


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cate = (TextView)findViewById(R.id.categori);


        dbHelper = new DBscore(MainActivity.this);
        db = dbHelper.getReadableDatabase();

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        image = (ImageView) findViewById(R.id.rungirl);

        translate = AnimationUtils.loadAnimation(this, R.anim.translate);
        image.startAnimation(translate);

// Begin the transaction

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
// Replace the contents of the container with the new fragment
        ft.replace(R.id.contenuFrag, new MenuFrag());
// or ft.add(R.id.your_placeholder, new FooFragment());
// Complete the changes added above
        ft.commit();


        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomNavigationView.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());


        //handling floating action menu



    }
}



