package com.homeki.android.communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;

public class CommandSender {
	public static String sendCommand(String address) throws IOException {
		URL url = new URL(address);
		
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		Log.d("LOG", address);
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			return processServerResponse(in);
		} catch (NullPointerException e) {
			Log.e("LOG", "Nullpointer :(");
			return "";
		} finally {
			urlConnection.disconnect();
		}
	}
	
	public static String postCommand(String address, String value) throws IOException {
		URL url = new URL(address);
		HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
		urlConnection.setRequestMethod("POST");
		urlConnection.setRequestProperty("Content-Type", "application/x-wwworm-urlencoded");
		urlConnection.setRequestProperty("Content-Length", Integer.toString(value.length()));
		urlConnection.setRequestProperty("Content-Language", "en-US");
		urlConnection.setUseCaches(false);
		urlConnection.setDoInput(true);
		urlConnection.setDoOutput(true);
		
		// Send request
		DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
		wr.writeBytes(value);
		wr.flush();
		wr.close();
		
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			return processServerResponse(in);
		} finally {
			urlConnection.disconnect();
		}
	}
	
	private static String processServerResponse(BufferedReader in) throws IOException {
		StringBuilder sb = new StringBuilder();
		while (in.ready()) {
			sb.append(in.readLine() + "\n");
		}
		return sb.toString();
	}
}
