package com.homeki.android.server;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.homeki.android.model.devices.Device;
import com.homeki.android.model.devices.DeviceBuilder;
import com.homeki.android.misc.Settings;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ApiClient {
	private static String TAG = ApiClient.class.getSimpleName();

	private Context context;

	public ApiClient(Context context) {
		this.context = context;
	}

	private String getServerURL() {
		return "http://" + Settings.getServerUrl(context) + ":" + Settings.getServerPort(context) + "/api";
	}

	public List<Device> getAllDevices() {
		Log.d(TAG, "getAllDevices()");

		ArrayList<Device> devices = new ArrayList<>();
		HttpURLConnection connection = null;

		try {
			connection = (HttpURLConnection) new URL(getServerURL() + "/devices").openConnection();
			connection.setConnectTimeout(2000);

			InputStream stream = connection.getInputStream();

			String response = readStreamToEnd(stream);
			Gson gson = new Gson();
			DeviceBuilder.Device[] deviceArray = gson.fromJson(response, DeviceBuilder.Device[].class);

			for (int i = 0; i < deviceArray.length; i++) {
				Device device = DeviceBuilder.build(deviceArray[i].type, deviceArray[i].deviceId, deviceArray[i].name, deviceArray[i].description, deviceArray[i].active, deviceArray[i].channels);
				devices.add(device);
			}
		} catch (Exception e) {
			Log.e(TAG, "getAllDevices() " + e.getMessage());
			e.printStackTrace();
		} finally {
			Log.d(TAG, "getAllDevices() disconnect");
			if (connection != null) {
				connection.disconnect();
			}
		}

		return devices;
	}

	public LatLng getServerLocation() {
		Log.d(TAG, "getServerLocation()");

		HttpURLConnection connection = null;

		try {
			connection = (HttpURLConnection) new URL(getServerURL() + "/server").openConnection();
			connection.setConnectTimeout(2000);

			InputStream stream = connection.getInputStream();

			String response = readStreamToEnd(stream);
			Gson gson = new Gson();
			JSONServer server = gson.fromJson(response, JSONServer.class);

			return new LatLng(server.locationLatitude, server.locationLongitude);
		} catch (Exception e) {
			Log.e(TAG, "getServerLocation() " + e.getMessage());
			e.printStackTrace();
		} finally {
			Log.d(TAG, "getServerLocation() disconnect");
			if (connection != null) {
				connection.disconnect();
			}
		}

		return new LatLng(0, 0);
	}

	public void registerClient(String id) throws Exception {
		HttpURLConnection connection = null;

		try {
			Gson gson = new Gson();
			connection = (HttpURLConnection) new URL(getServerURL() + "/clients").openConnection();
			connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(1000);

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			String json = gson.toJson(new JSONClient(id));
			writer.write(json);
			writer.flush();

			connection.getInputStream();
		} finally {
			Log.d(TAG, "registerClient() disconnect");
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public void unregisterClient(String id) throws Exception {
		HttpURLConnection connection = null;

		try {
			connection = (HttpURLConnection) new URL(getServerURL() + "/clients/" + id).openConnection();
			connection.setRequestMethod("DELETE");
			connection.setConnectTimeout(1000);

			connection.getInputStream();
		} finally {
			Log.d(TAG, "unregisterClient() disconnect");
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	public boolean setChannelValueForDevice(int deviceId, int channel, int value) {
		HttpURLConnection connection = null;

		try {
			Gson gson = new Gson();
			connection = (HttpURLConnection) new URL(getServerURL() + "/devices/" + deviceId + "/channels/" + channel).openConnection();
			connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
			connection.setRequestMethod("POST");
			connection.setConnectTimeout(1000);

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			String json = gson.toJson(new JSONChannelValue(value));
			writer.write(json);
			writer.flush();

			connection.getInputStream();
		} catch (Exception e) {
			Log.e(TAG, "setChannelValueForDevice() " + e.getMessage());
			e.printStackTrace();

			return false;
		} finally {
			Log.d(TAG, "setChannelValueForDevice() disconnect");
			if (connection != null) {
				connection.disconnect();
			}
		}

		return true;
	}

	private String readStreamToEnd(InputStream inputStream) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			total.append(line);
		}
		return total.toString();
	}

	private class JSONServer {
		public double locationLatitude;
		public double locationLongitude;
		public String name;
	}

	private class JSONClient {
		public String id;

		public JSONClient(String id) {
			this.id = id;
		}
	}

	private class JSONChannelValue {
		public int value;

		public JSONChannelValue(int value) {
			this.value = value;
		}
	}
}