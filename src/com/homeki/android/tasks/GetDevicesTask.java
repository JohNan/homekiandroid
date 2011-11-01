package com.homeki.android.tasks;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.homeki.android.HomekiApplication;
import com.homeki.android.commands.Commands;
import com.homeki.android.device.Device;
import com.homeki.android.device.Dimmer;
import com.homeki.android.device.JsonDevice;
import com.homeki.android.device.Lamp;
import com.homeki.android.device.Temperature;

public class GetDevicesTask extends AsyncTask<Void, Void, List<JsonDevice>> {
	private final HomekiApplication ha;
	
	public GetDevicesTask(HomekiApplication ha) {
		this.ha = ha;
	}
	
	@Override
	protected List<JsonDevice> doInBackground(Void... params) {
		String s = "";
		try {
			s = Commands.getDevices(ha);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Type listType = new TypeToken<List<JsonDevice>>() {}.getType();
		return new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd hh:mm:ss").create().fromJson(s, listType);
	}

	@Override
	protected void onPostExecute(List<JsonDevice> result) {
		if (result != null) {
			List<Device> list = new ArrayList<Device>();
			for (JsonDevice d : result) {
				if (d.type.contains("Dimmer")) {
					Lamp l = new Dimmer(d);
					new DownloadDeviceStatus(ha, l).execute();
					list.add(l);
				} else if (d.type.contains("Switch")) {
					Lamp l = new Lamp(d);
					new DownloadDeviceStatus(ha, l).execute();
					list.add(l);
				} else if (d.type.contains("Temp")) {
					list.add(new Temperature(d));
				}
			}
			ha.updateList(list);
		}
	}
}