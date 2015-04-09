package org.fourthline.cling.demo.android.wifidirect;

import android.app.Activity;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.fourthline.cling.android.FixedAndroidLogHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DESCRIPTION
 *
 * @author Sebastian Roth <sebastian.roth@gmail.com>
 */
public class WifiDirectBrowserActivity extends Activity {
    private WifiP2pManager.Channel p2pChannel;
    private WifiP2pManager wifiP2pManager;

    private static final Logger log = Logger.getLogger(LightActivity.class.getName());
    private DeviceListAdapter deviceListAdapter = new DeviceListAdapter();
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);

        setContentView(R.layout.browser);

        RecyclerView deviceList = (RecyclerView) findViewById(R.id.deviceList);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDeviceList();
            }
        });

        deviceList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        deviceList.setAdapter(deviceListAdapter);

        // Setup logging.
        org.seamless.util.logging.LoggingUtil.resetRootHandler(new FixedAndroidLogHandler());
        Logger.getLogger("org.fourthline.cling.demo.android").setLevel(Level.FINEST);

        p2pChannel = wifiP2pManager.initialize(this, getMainLooper(), new WifiP2pManager.ChannelListener() {
            @Override
            public void onChannelDisconnected() {
                publishInfo("Channel disconnected");
            }
        });
        publishInfo("Channel connected");

        wifiP2pManager.setDnsSdResponseListeners(p2pChannel, new WifiP2pManager.DnsSdServiceResponseListener() {
            @Override
            public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
                log.log(Level.FINEST, "instance: " + instanceName);
                deviceListAdapter.addDevice(new Pair<>(instanceName, srcDevice));
            }
        }, new WifiP2pManager.DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> txtRecordMap, WifiP2pDevice srcDevice) {
                log.log(Level.FINEST, "domain  : " + fullDomainName);
            }
        });
        wifiP2pManager.setServiceResponseListener(p2pChannel, new WifiP2pManager.ServiceResponseListener() {
            @Override
            public void onServiceAvailable(int protocolType, byte[] responseData, WifiP2pDevice srcDevice) {
                log.log(Level.FINEST, "Service available: " + protocolType + ", device: " + srcDevice);
            }
        });

        refreshDeviceList();
    }

    private void refreshDeviceList() {
        deviceListAdapter.removeAllDevices();
        wifiP2pManager.addServiceRequest(p2pChannel, WifiP2pDnsSdServiceRequest.newInstance(P2pConstants.WIFI_P2P_SERVICE_TYPE),
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        log.finest("Adding Service request");
                        wifiP2pManager.discoverServices(p2pChannel, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                log.finest("Searching");
                            }

                            @Override
                            public void onFailure(final int i) {
                                log.warning("Error: " + i);
                            }
                        });
                    }

                    @Override
                    public void onFailure(final int i) {
                        log.log(Level.SEVERE, "Can not submit service request: " + i);
                    }
                });

        getWindow().getDecorView().postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void publishInfo(final String text) {
        if (getActionBar() != null)
            getActionBar().setSubtitle(text);
    }

    static class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.DeviceItemViewHolder> {
        List<Pair<String, WifiP2pDevice>> devices = new ArrayList<>();

        @Override
        public DeviceItemViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
            return new DeviceItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false));
        }

        @Override
        public void onBindViewHolder(final DeviceItemViewHolder holder, final int position) {
            final Pair<String, WifiP2pDevice> info = devices.get(position);
            final WifiP2pDevice device = info.second;

            holder.title.setText(info.first + ": " + device.deviceName);
            holder.subTitle.setText(device.deviceAddress + " / " + device.primaryDeviceType + " / " + device.secondaryDeviceType);
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        public void addDevice(Pair<String, WifiP2pDevice> device) {
            if (!devices.contains(device)) {
                devices.add(device);
                notifyItemInserted(devices.size() - 1);
            }
        }

        public void removeAllDevices() {
            final int size = devices.size();
            if (size > 0) {
                devices.clear();
                notifyItemRangeRemoved(0, size);
            }
        }

        static class DeviceItemViewHolder extends RecyclerView.ViewHolder {

            private final TextView title;
            private final TextView subTitle;

            public DeviceItemViewHolder(final View itemView) {
                super(itemView);

                title = (TextView) itemView.findViewById(R.id.title);
                subTitle = (TextView) itemView.findViewById(R.id.subTitle);
            }
        }
    }
}
