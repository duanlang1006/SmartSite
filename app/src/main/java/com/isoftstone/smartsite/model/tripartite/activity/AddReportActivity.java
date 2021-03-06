package com.isoftstone.smartsite.model.tripartite.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.isoftstone.smartsite.R;
import com.isoftstone.smartsite.base.BaseActivity;
import com.isoftstone.smartsite.http.HttpPost;
import com.isoftstone.smartsite.model.tripartite.adapter.DialogListViewAdapter;
import com.isoftstone.smartsite.model.tripartite.fragment.RevisitFragment;
import com.isoftstone.smartsite.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanyongjun on 2017/10/18.
 * 添加巡查报告界面
 */

public class AddReportActivity extends BaseActivity {
    public final static int REQUEST_ACTIVITY_ATTACH = 0;//请求图片的request code

    private List<Uri> attach = new ArrayList<>();
    private Resources mRes = null;
    private Drawable mWaittingAdd = null;
    private Drawable mWattingChanged = null;
    private HttpPost mHttpPost = null;
    private ArrayList<String> mQueryTypes = new ArrayList<>();

    //the view in this activity
    public EditText mEditAddress = null;
    public TextView mTypesEditor = null;
    public TextView mAddress = null;
    public TextView mCompany = null;
    public TextView mTypes = null;
    public TextView mBuildCompany = null;
    public TextView mConsCompany = null;
    public TextView mSuperCompany = null;

    public EditText mEditCompany = null;
    public EditText mEditBuildCompany = null;
    public EditText mEditConsCompany = null;
    public EditText mEditSuperCompany = null;
    public boolean isSettedType = false;
    public RevisitFragment mRevisitFrag = null;


    @Override
    protected int getLayoutRes() {

        //TODO 这个界面还需要不少的润色
        return R.layout.activity_add_inspect_report;
    }

    @Override
    protected void afterCreated(Bundle savedInstanceState) {
        init();
    }

    public void init() {
        mHttpPost = new HttpPost();
        mRes = getResources();
        mWaittingAdd = mRes.getDrawable(R.drawable.addcolumn);
        mWaittingAdd.setBounds(0, 0, mWaittingAdd.getIntrinsicWidth(), mWaittingAdd.getIntrinsicHeight());
        mWattingChanged = mRes.getDrawable(R.drawable.editcolumn);
        mWattingChanged.setBounds(0, 0, mWattingChanged.getIntrinsicWidth(), mWattingChanged.getIntrinsicHeight());

        initView();
        initListener();
        restoreData();
        new QueryReportTypeTask().execute();
    }

    public void initView() {
        mEditAddress = (EditText) findViewById(R.id.edit_report_address);
        mTypesEditor = (TextView) findViewById(R.id.lab_report_types);
        mAddress = (TextView) findViewById(R.id.lab_address);
        mCompany = (TextView) findViewById(R.id.lab_company);
        mTypes = (TextView) findViewById(R.id.lab_types);
        mBuildCompany = (TextView) findViewById(R.id.lab_build_company);
        mConsCompany = (TextView) findViewById(R.id.lab_cons_company);
        mSuperCompany = (TextView) findViewById(R.id.lab_super_company);

        mEditCompany = (EditText) findViewById(R.id.edit_company);
        mEditBuildCompany = (EditText) findViewById(R.id.edit_build_company);
        mEditConsCompany = (EditText) findViewById(R.id.edit_cons_company);
        mEditSuperCompany = (EditText) findViewById(R.id.edit_super_company);
        mTypesEditor.setTextColor(getResources().getColor(R.color.des_text_color));
        mRevisitFrag = (RevisitFragment)getSupportFragmentManager().findFragmentById(R.id.frag_reply_inspect_report);
    }

    public void saveData() {
        mRevisitFrag.saveData();
        SPUtils.saveString("add_report_address", mEditAddress.getText().toString());
        SPUtils.saveString("add_report_company", mEditCompany.getText().toString());
        SPUtils.saveString("add_report_build_company", mEditBuildCompany.getText().toString());
        SPUtils.saveString("add_report_cons_company", mEditConsCompany.getText().toString());
        SPUtils.saveString("add_report_super_company", mEditSuperCompany.getText().toString());

        if (isSettedType) {
            SPUtils.saveString("add_report_type", mTypesEditor.getText().toString());
        }
    }

    public void restoreData() {
        mEditCompany.setText(SPUtils.getString("add_report_company", ""));
        mEditAddress.setText(SPUtils.getString("add_report_address", ""));
        mEditBuildCompany.setText(SPUtils.getString("add_report_build_company", ""));
        mEditConsCompany.setText(SPUtils.getString("add_report_cons_company", ""));
        mEditSuperCompany.setText(SPUtils.getString("add_report_super_company", ""));

        String type = SPUtils.getString("add_report_type","");
        if (!TextUtils.isEmpty(type)) {
            mTypesEditor.setText(type);
            isSettedType = true;
            mTypes.setCompoundDrawables(mWattingChanged, null, null, null);
            mTypesEditor.setTextColor(getResources().getColor(R.color.main_text_color));
        }
    }

    public void initListener() {
        mTypesEditor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddReportActivity.this);
                View dialogLayout = LayoutInflater.from(AddReportActivity.this).inflate(R.layout.dialog_add_report, null);
                ListView listView = (ListView) dialogLayout.findViewById(R.id.listview_dialog_add_report);

                //TODO
                final ArrayList<String> list = new ArrayList<>();
                list.add("工地报告");
                list.add("巡查报告");
                DialogListViewAdapter adapter = new DialogListViewAdapter(AddReportActivity.this, list);
                listView.setAdapter(adapter);

                builder.setView(dialogLayout);
                final AlertDialog dialog = builder.create();
                dialog.show();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mTypesEditor.setText(list.get(position));
                        isSettedType = true;
                        mTypes.setCompoundDrawables(mWattingChanged, null, null, null);
                        mTypesEditor.setTextColor(getResources().getColor(R.color.main_text_color));
                        dialog.dismiss();
                    }
                });

            }
        });

        mEditAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() != 0) {
                    mAddress.setCompoundDrawables(mWattingChanged, null, null, null);
                } else {
                    mAddress.setCompoundDrawables(mWaittingAdd, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() != 0) {
                    mCompany.setCompoundDrawables(mWattingChanged, null, null, null);
                } else {
                    mCompany.setCompoundDrawables(mWaittingAdd, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditBuildCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() != 0) {
                    mBuildCompany.setCompoundDrawables(mWattingChanged, null, null, null);
                } else {
                    mBuildCompany.setCompoundDrawables(mWaittingAdd, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditConsCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() != 0) {
                    mConsCompany.setCompoundDrawables(mWattingChanged, null, null, null);
                } else {
                    mConsCompany.setCompoundDrawables(mWaittingAdd, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditSuperCompany.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null && s.length() != 0) {
                    mSuperCompany.setCompoundDrawables(mWattingChanged, null, null, null);
                } else {
                    mSuperCompany.setCompoundDrawables(mWaittingAdd, null, null, null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    /**
     * 点击保存按钮
     *
     * @param v
     */
    public void onBtnSaveClick(View v) {
        saveData();
        finish();
        //TODO
    }

    /**
     * 点击返回按钮
     *
     * @param v
     */
    public void onBtnBackClick(View v) {
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ACTIVITY_ATTACH: {
                Log.e(TAG, "onactivityresult:" + data.getData());
                mRevisitFrag.addAttachUri(data.getDataString());
                break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private class QueryReportTypeTask extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
//            ArrayList<MessageBean> msgs = mHttpPost.getPatrolReportList("", "", "", "1");
            return null;
        }
    }

}
