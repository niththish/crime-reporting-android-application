package com.example.crimereporting;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Fragment> list = new ArrayList<>();
        list.add(new report());
        list.add(new locate());

        pager = findViewById(R.id.pager);
        pagerAdapter = new SliderAdapter(getSupportFragmentManager(),list);

        //pager.setPageTransformer(true,new SliderAnimation());
        pager.setAdapter(pagerAdapter);

    }


}