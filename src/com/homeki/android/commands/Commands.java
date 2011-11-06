package com.homeki.android.commands;

import java.io.IOException;

import android.content.Context;

import com.homeki.android.SharedPreferenceHelper;
import com.homeki.android.communication.CommandSender;

public class Commands {
	
	private static String sendCommand(Context c, String command) throws IOException {
		String address = String.format("http://%s", SharedPreferenceHelper.getStringValue(c, "server"));		
		return CommandSender.sendCommand(String.format("%s/%s", address, command));
	}
	
	private static String postCommmand(Context c, String command, String data) throws IOException {
		String address = String.format("http://%s", SharedPreferenceHelper.getStringValue(c, "server"));		
		return CommandSender.postCommand(String.format("%s/%s", address, command), data);
	}
	
	public static String getDevices(Context c) throws IOException {
		return sendCommand(c, "get/devices");
	}
	
	public static String switchOn(Context c, int id) throws IOException {
		String command = String.format("set/on?id=%d", id);
		return sendCommand(c, command);
	}
	
	public static String switchOff(Context c, int id) throws IOException {
		String command = String.format("set/off?id=%d", id);
		return sendCommand(c, command);
	}
	
	public static String dim(Context c, int id, int level) throws IOException {
		String command = String.format("set/dim?id=%d&level=%d", id, level);
		return sendCommand(c, command);
	}

	public static String getDeviceStatus(Context c, int id) throws IOException {
		String command = String.format("get/status?id=%d", id);
		return sendCommand(c, command);
	}
	
	public static String setName(Context c, int id, String name) throws IOException {
		String command = String.format("set/device?id=%d", id);
		return postCommmand(c, command, String.format("{name=%s}", name));
	}
	
}
