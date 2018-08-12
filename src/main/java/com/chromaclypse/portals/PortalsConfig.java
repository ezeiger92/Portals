package com.chromaclypse.portals;

import java.util.HashMap;

import com.chromaclypse.api.config.ConfigObject;

public class PortalsConfig extends ConfigObject {
	public HashMap<String, WorldEntry> worlds = new HashMap<>();
	public static class WorldEntry {
		public double size = 1.0;
		public String end = "";
		public String nether = "";
	}
}
