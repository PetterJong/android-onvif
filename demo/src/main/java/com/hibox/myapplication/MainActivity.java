package com.hibox.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wp.android_onvif.onvif.FindDevicesThread;
import com.wp.android_onvif.onvif.GetDeviceInfoThread;
import com.wp.android_onvif.onvif.GetSnapshotInfoThread;
import com.wp.android_onvif.onvifBean.Device;
import com.wp.android_onvif.util.NetUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements FindDevicesThread.FindDevicesListener,
        GetDeviceInfoThread.GetDeviceInfoCallBack {

    private DevicesAdapter adapter;
    private ArrayList<Device> devices;
    //当前选中的标识
    private int selectedIndex = -1;

    private LoadingFragment loadingFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.listView1);
       Log.v("MainActivity", "主机ip地址：" + NetUtil.getHostIP()) ;
        //加载动画
        loadingFragment = new LoadingFragment();
        //设备列表
        devices = new ArrayList<>();
        adapter = new DevicesAdapter(this, devices);
        listView.setAdapter(adapter);
        //点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadingFragment.show(getSupportFragmentManager(), "loading");
                selectedIndex = position;
                new GetDeviceInfoThread(devices.get(position), MainActivity.this, MainActivity.this).start();
            }
        });
        /*
        如果能够确定端口号的话，可以跳过设备配置请求，可直接获取截图
         */
        Device device = new Device();
        device.setIpAddress("192.168.1.10:8899");
        device.setMediaUrl("http://192.168.1.10:8899/onvif/Media");
        new GetSnapshotInfoThread(device, MainActivity.this, new GetSnapshotInfoThread.GetSnapshotInfoCallBack() {
            @Override
            public void getSnapshotInfoResult(boolean isSuccess, String errorMsg) {
//                    loadingFragment.dismiss();
                Log.v("MainActivity", "截图路径 " + errorMsg);
            }
        }).start();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
                loadingFragment.show(getSupportFragmentManager(), "loading");
                new FindDevicesThread(this, this).start();
                break;
        }
    }

    @Override
    public void searchResult(ArrayList<Device> devices) {
        this.devices.clear();
        this.devices.addAll(devices);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingFragment.dismiss();
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void getDeviceInfoResult(boolean isSuccess, String errorMsg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadingFragment.dismiss();
            }
        });
        if (isSuccess) {
            //搜索成功打印数据
            Log.e("MainActivity", devices.get(selectedIndex).toString());
            Device device =  devices.get(selectedIndex);
            new GetSnapshotInfoThread(device, MainActivity.this, new GetSnapshotInfoThread.GetSnapshotInfoCallBack() {
                @Override
                public void getSnapshotInfoResult(boolean isSuccess, String errorMsg) {
//                    loadingFragment.dismiss();
                }
            }).start();
        }
    }
}
