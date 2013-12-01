package com.homeki.android.view.devicegrid;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.TextView;
import com.homeki.android.DeviceDetailsActivity;
import com.homeki.android.R;
import com.homeki.android.model.devices.ThermometerDevice;
import com.homeki.android.server.ActionPerformer;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class DeviceGridItemThermometerView extends AbstractDeviceGridView<ThermometerDevice> {
	private TextView temperatureView;
	private TextView nameView;

	public DeviceGridItemThermometerView(Context context, ActionPerformer actionPerformer) {
		super(context, actionPerformer);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Intent intent = new Intent(context, DeviceDetailsActivity.class);
		intent.putExtra(DeviceDetailsActivity.EXTRA_DEVICE_ID, device.getId());
		context.startActivity(intent);
		return super.onTouchEvent(event);
	}

	@Override
	protected void inflate(LayoutInflater layoutInflater) {
		layoutInflater.inflate(R.layout.device_grid_thermometer, this);

		nameView = (TextView) findViewById(R.id.device_grid_thermometer_name);
		temperatureView = (TextView) findViewById(R.id.device_grid_thermometer_temperature);
	}

	@Override
	protected void onDeviceSet(ThermometerDevice device) {
		double temp = device.getTemperature();
		DecimalFormat df = new DecimalFormat("0.0");
		df.setDecimalSeparatorAlwaysShown(true);
		df.setRoundingMode(RoundingMode.HALF_UP);
		temperatureView.setText(df.format(temp) + " °C");
		
		nameView.setText(device.getName());
	}
}
