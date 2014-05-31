package com.homeki.android.view.devicegrid;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.homeki.android.model.devices.Device;
import com.homeki.android.server.ActionPerformer;
import com.homeki.android.server.ActionPerformer.OnChannelValueSetListener;
import com.homeki.android.view.DeviceItemView;

public abstract class AbstractDeviceGridView<T extends Device> extends LinearLayout implements DeviceItemView {
	protected Device device;
	protected Context context;
	protected ActionPerformer actionPerformer;

	public AbstractDeviceGridView(Context context, ActionPerformer actionPerformer) {
		super(context);
		this.actionPerformer = actionPerformer;
		this.context = context;
		inflate((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setDevice(Device device) {
		this.device = device;
		onDeviceSet((T) device);
	}

	protected void setChannelValue(String channelName, int value) {
		actionPerformer.setChannelValueForDevice(device.getId(), device.getChannelId(channelName), value, new OnChannelValueSetListener() {
			@Override
			public void result(boolean success) {
				if (!success && device != null) {
					Toast.makeText(context, "Failed to set value for " + device.getName(), Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	protected abstract void inflate(LayoutInflater inflater);

	protected abstract void onDeviceSet(T device);
}
