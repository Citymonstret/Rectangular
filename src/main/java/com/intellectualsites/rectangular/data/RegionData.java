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

    public RegionData(int regionID, String owner, String packedData) {
        this.regionID = regionID;
        this.owner = owner;
        String[] parts = packedData.split(";");
        for (String s : parts) {
            String[] split = s.split(":");
            String key = split[0];
            DataType type = getDataType(key);
            dataEntries.get(type).put(key, new DataEntry(type, key, split[1]));
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
