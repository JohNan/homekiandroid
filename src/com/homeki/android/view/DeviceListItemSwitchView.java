package com.homeki.android.view;

import com.homeki.android.R;
import com.homeki.android.model.devices.DimmerDevice;
import com.homeki.android.model.devices.SwitchDevice;
import com.homeki.android.server.ActionPerformer;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class DeviceListItemSwitchView extends AbstractDeviceListItemView<SwitchDevice> {

	private Switch mOnOffSwitch;

	public DeviceListItemSwitchView(Context context, ActionPerformer actionPerformer) {
		super(context, actionPerformer);

	}

	@Override
	protected void inflate(LayoutInflater layoutInflater) {
		layoutInflater.inflate(R.layout.device_list_switch, this);
		mNameView = (TextView) findViewById(R.id.device_list_switch_name);
		mDescriptionView = (TextView) findViewById(R.id.device_list_switch_description);
		mOnOffSwitch = (Switch) findViewById(R.id.device_list_switch_onoff);
		
		mOpenDetailsView = (ImageView)findViewById(R.id.device_list_switch_details_button);
		
		mOnOffSwitch.setOnCheckedChangeListener(new OnOffChangedListener());
	}

	@Override
	protected void onDeviceSet(SwitchDevice device) {

	}

	private class OnOffChangedListener implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			mActionPerformer.setChannelValueForDevice(mDevice.getId(), SwitchDevice.CHANNEL_ID_VALUE, isChecked ? "1" : "0");
		}
	}
}