package com.intellectualsites.rectangular.data;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RegionData {

    /*
    Meta Data Types:
    @f = Flag
    @m = Member
    @b = Blocked User
    @d = Data (general meta)
     */

    @RequiredArgsConstructor
    public enum DataType {
        FLAG            ("@f"),
        MEMBER          ("@m"),
        BLOCKED         ("@b"),
        DATA            ("@d")
        ;
        @Getter
        private final String key;
    }

    @RequiredArgsConstructor
    public static class DataEntry {
        @Getter
        private final DataType dataType;
        @Getter
        private final String key;
        @Getter
        private final String value;
    }

    @Getter
    private final int regionID;

    @Getter
    private String owner;

    private Map<DataType, Map<String, DataEntry>> dataEntries = new ConcurrentHashMap<>();
    {
        for (DataType type : DataType.values()) {
            dataEntries.put(type, new HashMap<>());
        }
    }

    public RegionData(int regionID, String owner, Map<String, String> entries) {
        this.regionID = regionID;
        this.owner = owner;

        for (Map.Entry<String, String> entry : entries.entrySet()) {
            if (!entry.getKey().startsWith("@")) {
                continue; // Skip owner, etc
            }
            DataType type = getDataType(entry.getKey());
            dataEntries.get(type).put(entry.getKey(), new DataEntry(type, entry.getKey(), entry.getValue()));
        }
    }

    public static DataType getDataType(String s) {
        for (DataType t : DataType.values()) {
            if (s.startsWith(t.getKey())) {
                return t;
            }
        }
        return DataType.DATA;
    }

    public DataEntry getDataEntry(DataType type, String key) {
        return dataEntries.get(type).get(key);
    }

    public DataEntry getDataEntry(String key) {
        return getDataEntry(getDataType(key), key);
    }

    public DataEntry getDataEntryFromStripped(DataType type, String key) {
        return dataEntries.get(type).get(type.getKey() + key);
    }

    public String getPackedData() {
        StringBuilder builder = new StringBuilder();
        for (Map<String, DataEntry> entries : dataEntries.values()) {
            for (DataEntry entry : entries.values()) {
                builder.append(entry.getKey()).append(":")
                        .append(entry.getValue()).append(";");
            }
        }
        return builder.toString();
    }

}
