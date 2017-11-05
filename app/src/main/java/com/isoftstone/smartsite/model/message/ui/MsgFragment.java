package com.isoftstone.smartsite.model.message.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.isoftstone.smartsite.R;
import com.isoftstone.smartsite.base.BaseFragment;
import com.isoftstone.smartsite.http.HttpPost;
import com.isoftstone.smartsite.http.MessageBean;
import com.isoftstone.smartsite.model.message.data.MsgData;
import com.isoftstone.smartsite.utils.MsgUtils;

import java.util.ArrayList;

/**
 * Created by yanyongjun on 2017/10/15.
 */

public class MsgFragment extends BaseFragment {
/*    private ViewPager mViewPager = null;
    ArrayList<Fragment> mFragList = new ArrayList<Fragment>();
    private SparseArray<TextView> mSwitchLab = new SparseArray<>();*/

    public static final int FRAGMENT_TYPE_VCR = 0;
    public static final int FRAGMENT_TYPE_ENVIRON = 1;
    public static final int FRAGMENT_TYPE_SYNERGY = 2;


    private RelativeLayout mVcr = null;
    private RelativeLayout mEnviron = null;
    private RelativeLayout mTripartite = null;
    private Activity mActivity = null;

    public static final String FRAGMENT_TYPE = "type";
    public static final String FRAGMENT_DATA = "data";
    public static final int QUERY_TYPE_VCR = 1;
    public static final int QUERY_TYPE_ENVIRON = 2;
    public static final int QUERY_TYPE_THREE_PARTY = 3;

    private HttpPost mHttpPost = null;
    private SparseArray<Object> mVcrmsg = new SparseArray<Object>();

    private int[] unReadMsgCountView = new int[]{R.id.lab_vcr_unread_num, R.id.lab_environ_unread_num, R.id.lab_threeparty_unread_num};
    private int[] dateView = new int[]{R.id.lab_vcr_time, R.id.lab_environ_time, R.id.lab_thirpartite_time};
    private int[] titleView = new int[]{R.id.lab_vcr_msg, R.id.lab_environ_msg, R.id.lab_threeparty_msg};

    private boolean mInForgourd = false;


    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_msg;
    }

    @Override
    protected void afterCreated(Bundle savedInstanceState) {
        init();
    }

    private void init() {
        mActivity = getActivity();
        mVcr = (RelativeLayout) rootView.findViewById(R.id.conlayout_vcr);
        mEnviron = (RelativeLayout) rootView.findViewById(R.id.conlayout_environ);
        mTripartite = (RelativeLayout) rootView.findViewById(R.id.conlayout_thirpartite);

        mVcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, VcrActivity.class);
                mActivity.startActivity(i);
            }
        });
        mEnviron.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, EnvironActivity.class);
                mActivity.startActivity(i);
            }
        });
        mTripartite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, TripartiteMsgActivity.class);
                mActivity.startActivity(i);
            }
        });

        mHttpPost = new HttpPost();
    }

    @Override
    public void onResume() {
        super.onResume();
        mInForgourd = true;
        new QueryMsgTask(1).execute();
        new QueryMsgTask(2).execute();
        new QueryMsgTask(3).execute();
    }

    @Override
    public void onPause(){
        super.onPause();
        mInForgourd = false;
    }

    private class QueryMsgTask extends AsyncTask<String, Integer, SparseArray<Object>> {
        private int mQueryType = 0;

        public QueryMsgTask(int type) {
            mQueryType = type;
        }

        @Override
        protected SparseArray<Object> doInBackground(String... params) {
            ArrayList<MessageBean> msgs = mHttpPost.getMessage("", "", "", mQueryType + "");
            ArrayList<MsgData> datas = new ArrayList<>();
            MsgUtils.toMsgData(datas, msgs);
            int unreadCount = 0;
            MsgData lastMsg = null;
            for (MsgData temp : datas) {
                if (lastMsg == null) {
                    lastMsg = temp;
                } else if (temp.getTime().after(lastMsg.getTime())) {
                    lastMsg = temp;
                }
                if (temp.getStatus() == MsgData.STATUS_UNREAD) {
                    unreadCount++;
                }
            }
            SparseArray<Object> result = new SparseArray<>();
            result.put(1, unreadCount);
            if (lastMsg != null) {
                result.put(2, lastMsg);
            }
            return result;
        }

        @Override
        protected void onPostExecute(SparseArray<Object> s) {
            super.onPostExecute(s);
            if(!mInForgourd){
                return;
            }
            TextView unReadMessageView = (TextView) rootView.findViewById(unReadMsgCountView[mQueryType - 1]);
            TextView title = (TextView) rootView.findViewById(titleView[mQueryType - 1]);
            TextView time = (TextView) rootView.findViewById(dateView[mQueryType - 1]);
            if ((Integer) s.get(1) == 0) {
                unReadMessageView.setVisibility(View.GONE);
            } else {
                unReadMessageView.setVisibility(View.VISIBLE);
                unReadMessageView.setText(s.get(1) + "");
            }

            MsgData msg = (MsgData) s.get(2);
            if (msg != null) {
                title.setText(msg.getTitle());
                time.setText(msg.getDateString());
            } else {
                title.setText("");
                time.setText("");
            }
        }
    }
}
