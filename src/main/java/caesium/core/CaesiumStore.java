package caesium.core;

import java.util.concurrent.ConcurrentHashMap;

import caesium.model.CaesiumJobStatus;

public class CaesiumStore {
	public final static ConcurrentHashMap<String, CaesiumJobStatus> jobStatusMap = new ConcurrentHashMap<>();
}
