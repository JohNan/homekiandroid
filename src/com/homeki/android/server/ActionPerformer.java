package com.homeki.android.server;

import java.util.List;

import com.homeki.android.model.devices.AbstractDevice;

public interface ActionPerformer {
	
	public void requestDeviceList();
	public void setOnDeviceListReceivedListener(OnDeviceListReceivedListener listener);
	
	public void setChannelValueForDevice(int deviceId, int channelId, String value);
	
	public interface OnDeviceListReceivedListener {
		void onDeviceListReceived(List<AbstractDevice> devices);
	}
}