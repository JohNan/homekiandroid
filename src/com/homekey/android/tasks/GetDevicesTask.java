package com.homekey.android.tasks;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.homekey.android.HomekiApplication;
import com.homekey.android.commands.Commands;

import device.Device;
import device.Dimmer;
import device.JsonDevice;
import device.Lamp;
import device.Temperature;

public class GetDevicesTask extends AsyncTask<Void, Void, List<JsonDevice>> {
	private final HomekiApplication ha;
	
	public GetDevicesTask(HomekiApplication ha){
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
		Type listType = new TypeToken<List<JsonDevice>>(){}.getType();
		return new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd hh:mm:ss").create().fromJson(s, listType);
	}
	
	@Override
	protected void onPostExecute(List<JsonDevice> result) {
		super.onPostExecute(result);
		List<Device> list = new ArrayList<Device>();
		for (JsonDevice d : result) {
			if (d.type.contains("Dimmer")) {
				list.add(new Dimmer(d));
			} else if (d.type.contains("Switch")) {
				list.add(new Lamp(d));
			} else if (d.type.contains("Temp")) {
				list.add(new Temperature(d));
			}
		}
		ha.updateList(list);
	}
}