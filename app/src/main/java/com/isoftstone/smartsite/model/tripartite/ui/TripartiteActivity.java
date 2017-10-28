package com.isoftstone.smartsite.model.tripartite.ui;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.isoftstone.smartsite.R;
import com.isoftstone.smartsite.base.BaseActivity;

import java.util.ArrayList;

/**
 * Created by yanyongjun on 2017/10/16.
 * 三方协同的主页面
 */

public class TripartiteActivity extends BaseActivity {
    private ViewPager mViewPager = null;
    ArrayList<Fragment> mFragList = new ArrayList<Fragment>();
    private SparseArray<TextView> mSwitchLab = new SparseArray<>();
    private SparseArray<ImageView> mSwitchImg = new SparseArray<>();

    public static final int FRAGMENT_TYPE_INSPECT_REPORT = 0;
    public static final int FRAGMENT_TYPE_CHECK_REPORT = 1;

    public static final String FRAGMENT_TYPE = "type";
    public static final String FRAGMENT_DATA = "data";


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_tripartite;
    }

    @Override
    protected void afterCreated(Bundle savedInstanceState) {
        init();
    }

    private void init() {
        mViewPager = (ViewPager) findViewById(R.id.report_view_pager);
        Fragment inspectFrag = new InspectReportFragment();
        Fragment checkFrag = new CheckReportFragment();

        mFragList.add(inspectFrag);
        mFragList.add(checkFrag);

        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragList.get(position);
            }

            @Override
            public int getCount() {
                return mFragList.size();
            }
        };
        mViewPager.setAdapter(pagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //TODO
            }

            @Override
            public void onPageSelected(int position) {
                chooseFrag(position);
                initTitleOnClickListener(position);
            }


            @Override
            public void onPageScrollStateChanged(int state) {
                //TODO
            }
        });

        TextView v1 = (TextView) findViewById(R.id.lab_inspect);
        TextView v2 = (TextView) findViewById(R.id.lab_check);


        v1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFrag(0);
                mViewPager.setCurrentItem(0);
            }
        });
        v2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFrag(1);
                mViewPager.setCurrentItem(1);
            }
        });

        mSwitchLab.put(0, v1);
        mSwitchLab.put(1, v2);
        ImageView img1 = (ImageView)findViewById(R.id.img_inspect);
        mSwitchImg.put(0,img1);
        ImageView img2 = (ImageView)findViewById(R.id.img_check);
        mSwitchImg.put(1,img2);

        chooseFrag(0);
        initTitleOnClickListener(0);
        mViewPager.setCurrentItem(0);
    }

    private void chooseFrag(int position) {
        Resources res = getResources();
        Drawable drawable = res.getDrawable(R.drawable.shape_threeparty_lab);
        drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
        for (int i = 0; i < mSwitchLab.size(); i++) {
            TextView v = mSwitchLab.get(i);
            if (i == position) {
                v.setTextColor(res.getColor(R.color.mainColor));
                mSwitchImg.get(i).setVisibility(View.VISIBLE);
            } else {
                v.setTextColor(res.getColor(R.color.main_text_color));
                mSwitchImg.get(i).setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initTitleOnClickListener(int position){
/*        Button btnAddPeport = (Button)findViewById(R.id.btn_title_add);
        btnAddPeport.setVisibility(View.VISIBLE);*/
        switch (position){
            case FRAGMENT_TYPE_INSPECT_REPORT:
/*                btnAddPeport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(TripartiteMsgActivity.this,AddInspectReportActivity.class);
                        startActivity(intent);
                    }
                });*/
                break;
            case FRAGMENT_TYPE_CHECK_REPORT:
                break;
            default:
                break;
        }
    }

    /**
     * 点击返回键之后的操作
     * @param v
     */
    public void onBackBtnClick(View v){
        finish();
    }
}
