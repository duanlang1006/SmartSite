package com.isoftstone.smartsite.model.message.ui;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.isoftstone.smartsite.R;
import com.isoftstone.smartsite.base.BaseFragment;
import com.isoftstone.smartsite.model.message.data.SynergyData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by yanyongjun on 2017/10/15.
 */

public class SynergyFragment extends BaseFragment {
    //listView中各项的名称
    public static final String FRAG_MSG_ITEM_DETAILS = "frag_synergy_msg";
    public static final String FRAG_MSG_ITEM_DATE = "frag_synergy_date";

    private Activity mActivity = null;
    private ListView mListView = null;
    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_msg_synergy;
    }

    @Override
    protected void afterCreated(Bundle savedInstanceState) {
        mActivity = getActivity();
        init();
    }

    private void init() {
        mListView = (ListView) mActivity.findViewById(R.id.listview_frag_synergy);
        SimpleAdapter adapter = new SimpleAdapter(mActivity, getData(), R.layout.frag_synergy_item, new String[]{FRAG_MSG_ITEM_DETAILS, FRAG_MSG_ITEM_DATE},
                new int[]{R.id.frag_synergy_msg, R.id.frag_synergy_date});
        mListView.setAdapter(adapter);
    }

    /**
     * 加载listview中的内容
     * @return
     */
    private List<Map<String, Object>> getData() {
        //TODO
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Resources res = mActivity.getResources();

        List<SynergyData> datas = readDataFromSDK();
        String sendReport = res.getString(R.string.send_report);
        String receiveReport = res.getString(R.string.receive_report);
        for (SynergyData data : datas) {
            Map<String, Object> map = new HashMap<String, Object>();
            if (data.getType() == SynergyData.TYPE_SEND_REPORT) {
                map.put(FRAG_MSG_ITEM_DETAILS, sendReport);
                map.put(FRAG_MSG_ITEM_DATE, data.getStringDate());
                list.add(map);
            } else if(data.getType() == SynergyData.TYPE_RECEIVE_REPORT) {
                map.put(FRAG_MSG_ITEM_DETAILS, String.format(receiveReport, data.getName()));
                map.put(FRAG_MSG_ITEM_DATE, data.getStringDate());
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 测试专用，后续需要从SDK数据源中读取
     * @return
     */
    private List<SynergyData> readDataFromSDK() {
        //TODO
        List<SynergyData> list = new ArrayList<>();

        SynergyData data = new SynergyData();
        data.setType(SynergyData.TYPE_RECEIVE_REPORT);
        data.setName("张珊");
        list.add(data);

        data = new SynergyData();
        data.setType(SynergyData.TYPE_SEND_REPORT);
        data.setName("王五");
        list.add(data);

        data = new SynergyData();
        data.setType(SynergyData.TYPE_RECEIVE_REPORT);
        data.setName("赵六");
        list.add(data);

        data = new SynergyData();
        data.setType(SynergyData.TYPE_SEND_REPORT);
        data.setName("李四");
        list.add(data);

        data = new SynergyData();
        data.setType(SynergyData.TYPE_RECEIVE_REPORT);
        data.setName("Steve");
        list.add(data);

        return list;
    }
}
