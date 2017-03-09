package com.docwei.xmppdemo;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ashokvarma.bottomnavigation.BadgeItem;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;

import fragment.MyFragmentAdapter;

public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener {
    public ViewPager getVp() {
        return mVp;
    }

    private ViewPager           mVp;
    private BottomNavigationBar mNavigationBar;
    private Toolbar             mToolbar;

    public TextView getTvtitle() {
        return mTv_title;
    }

    private TextView mTv_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
    }

    private void initData() {
        mTv_title = (TextView) findViewById(R.id.tv_title);

        mVp = (ViewPager) findViewById(R.id.vp);
        mNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        FragmentManager   fragmentManager = getSupportFragmentManager();
        MyFragmentAdapter adapter         =new MyFragmentAdapter(fragmentManager);
        mVp.setAdapter(adapter);

        BadgeItem badgeItem =new BadgeItem().setBorderWidth(1).setBackgroundColorResource(R.color.colorAccent).setText("2").setHideOnSelect(true);


        mNavigationBar.setMode(mNavigationBar.MODE_SHIFTING);
       /* mNavigationBar.setBackgroundStyle(mNavigationBar.BACKGROUND_STYLE_RIPPLE);*/
        mNavigationBar
                      .addItem(new BottomNavigationItem(R.mipmap.ic_favorite_white_24dp,"会话").setActiveColorResource(R.color.orange))
                      .addItem(new BottomNavigationItem(R.mipmap.ic_book_white_24dp, "联系人").setActiveColorResource(R.color.teal))
                      .setFirstSelectedPosition(0)
                      .initialise();

        mNavigationBar.setTabSelectedListener(this);




        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mNavigationBar.selectTab(position);
                System.out.println("postion--"+position);
                if(position==0) {
                    mTv_title.setText("会话");
                }else if(position==1){
                    mTv_title.setText("联系人");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onTabSelected(int position) {
        //点击底部导航，Viewpager跟着变化
        mVp.setCurrentItem(position);

    }

    @Override
    public void onTabUnselected(int position) {

    }

    @Override
    public void onTabReselected(int position) {

    }

}
