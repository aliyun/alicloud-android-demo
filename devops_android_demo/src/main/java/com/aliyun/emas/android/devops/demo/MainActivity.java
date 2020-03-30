package com.aliyun.emas.android.devops.demo;

import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.taobao.update.common.framework.UpdateRuntime;
import com.taobao.update.datasource.UpdateDataSource;
import com.taobao.update.datasource.UpdateUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRv;
    private List<Params> mData = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_params);
//        setCustomActionBar();
//        setActionBarTitle(R.string.app_name);
        mRv = findViewById(R.id.rv);
        initData();
        initView();
    }

    /********************UPDATE SDK START **************************/

    private void initData() {
        mData.add(new Params("appVersion",BuildConfig.VERSION_NAME,"应用版本"));
        mData.add(new Params("ip", UpdateUtils.getIpAddress(this),"客户端ip地址"));
        mData.add(new Params("proxyIp","unknown","代理服务器地址"));
        mData.add(new Params("ttid", Utils.sChannelID,"渠道号"));
        mData.add(new Params("identifier",Utils.sAppkey + "@android","appkey@android"));
        mData.add(new Params("utdid", UpdateUtils.getUtDId(this),"UTDID"));
        mData.add(new Params("brand",Build.BRAND,"设备品牌"));
        mData.add(new Params("model",Build.MODEL,"设备机型"));
        mData.add(new Params("os","android","系统名"));
        mData.add(new Params("osVersion", Build.VERSION.RELEASE+"","OS版本"));
        mData.add(new Params("apiVersion",Build.VERSION.SDK_INT+"","OS API级别"));
        mData.add(new Params("arch", UpdateUtils.getArch(),"客户端架构参数"));
        String netStatus = "null";
        switch (UpdateUtils.getNetworkState(this)) {
            case 0:
                netStatus = "NONE";
                break;
            case 1:
                netStatus = "2G";
                break;
            case 2:
                netStatus = "3G";
                break;
            case 3:
                netStatus = "4G";
                break;
            case 10:
                netStatus = "WIFI";
                break;
        }
        mData.add(new Params("netStatus", netStatus,"网络状态"));
        mData.add(new Params("locale", UpdateUtils.getLanguage(),"语言选项"));
        mData.add(new Params("md5Sum", PreferenceManager.getDefaultSharedPreferences(UpdateRuntime.getContext()).getString("update_history_c_md5", ""),"当前包的MD5值"));
        mData.add(new Params("packageMd5", UpdateUtils.getLastUpdateMd5(), "上次收到更新提醒的md5值"));
        mData.add(new Params("lastMd5Sum", PreferenceManager.getDefaultSharedPreferences(UpdateRuntime.getContext()).getString("update_history_l_md5", ""), "上一个安装包的md5Sum"));
    }

    private void initView() {
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new RVAdapter());
        findViewById(R.id.update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateDataSource.getInstance().startManualUpdate(false);
            }
        });
    }

    class RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(MainActivity.this).inflate(R.layout.item_prams, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VH vh = (VH) holder;
            vh.k.setText(mData.get(position).key);
            vh.v.setText(mData.get(position).value);
            vh.r.setText(mData.get(position).remarks);

        }

        @Override
        public int getItemCount() {
            return mData.size();
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }

        class VH extends RecyclerView.ViewHolder {
            TextView k, r;
            TextView v;
            public VH(View itemView) {
                super(itemView);
                k = itemView.findViewById(R.id.tv_k);
                v = itemView.findViewById(R.id.tv_v);
                r = itemView.findViewById(R.id.tv_remarks);
//                disableShowInput(v);
            }
        }
    }

    static class Params {
        String key;
        String value;
        String remarks;
        public Params(String key,String value,String remarks){
            this.key = key;
            this.value = value;
            this.remarks = remarks;
        }
    }

    private void disableShowInput(EditText et) {
        if (Build.VERSION.SDK_INT < 21) {
            Class<EditText> cls = EditText.class;
            Method method;
            try {
                method = cls.getMethod("setShowSoftInputOnFocus", boolean.class);
                method.setAccessible(true);
                method.invoke(et, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else{
            et.setShowSoftInputOnFocus(false);
        }

    }


}