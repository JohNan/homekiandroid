package com.homeki.android.view.devicelist;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import com.homeki.android.R;
import com.homeki.android.model.devices.SwitchDevice;
import com.homeki.android.server.ActionPerformer;

public class DeviceListItemSwitchView extends AbstractDeviceListView<SwitchDevice> {
	private Switch onOffSwitch;
	private OnOffChangedListener onOffChangedListener;

	public DeviceListItemSwitchView(Context context, ActionPerformer actionPerformer) {
		super(context, actionPerformer);
		onOffChangedListener = new OnOffChangedListener();
	}

	@Override
	protected void inflate(LayoutInflater layoutInflater) {
		layoutInflater.inflate(R.layout.device_list_switch, this);
		nameView = (TextView) findViewById(R.id.device_list_switch_name);
		descriptionView = (TextView) findViewById(R.id.device_list_switch_description);
		onOffSwitch = (Switch) findViewById(R.id.device_list_switch_onoff);

		openDetailsView = findViewById(R.id.device_list_switch_details_button);

		onOffSwitch.setOnCheckedChangeListener(onOffChangedListener);
	}

	@Override
	protected void onDeviceSet(SwitchDevice device) {
		onOffSwitch.setOnCheckedChangeListener(null);
		onOffSwitch.setChecked(device.getValue());
		onOffSwitch.setOnCheckedChangeListener(onOffChangedListener);
	}

	private class OnOffChangedListener implements OnCheckedChangeListener {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
			SwitchDevice device = (SwitchDevice) DeviceListItemSwitchView.this.device;
			device.setValue(isChecked);

			setChannelValue(SwitchDevice.CHANNEL_ID_VALUE, isChecked ? "1" : "0");
		}
	}
}
