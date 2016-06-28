package com.intellectualsites.rectangular.player;

import com.intellectualsites.rectangular.Rectangular;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("ALL")
public class PlayerMeta {

    @Getter(AccessLevel.PRIVATE)
    private final Map<String, byte[]> map = new ConcurrentHashMap<>();

    @Getter
    private String uuid;

    public PlayerMeta( Map<String, byte[]> map) {
        this.map.putAll(map);
    }

    public PlayerMeta associate(String uuid) {
        this.uuid = uuid;
        return this;
    }

    public boolean hasMeta(String key) {
        return this.map.containsKey(key);
    }

    public byte[] getMeta(String key) {
        if (hasMeta(key)) {
            return map.get(key);
        }
        return null;
    }

    public <T> T getMeta(String key, MetaParser<T> parser) {
        byte[] bytes = getMeta(key);
        if (bytes == null) {
            return null;
        }
        return parser.fromBytes(bytes);
    }

    public int getInt(String key) {
        return getMeta(key, Parsers.integerParser);
    }

    public boolean getBoolean(String key) {
        return getMeta(key, Parsers.booleanParser);
    }

    public String getString(String key) {
        return getMeta(key, Parsers.stringParser);
    }

    public void setMeta(String key, byte[] value) {
        final boolean delete = hasMeta(key);
        map.put(key, value);
        Rectangular.getServiceManager().runAsync(() -> {
            if (delete) {
                Rectangular.getDatabase().updatePlayerMeta(uuid, key, value);
            } else {
                Rectangular.getDatabase().addPlayerMeta(uuid, key, value);
            }
        });
    }

    public void setMeta(String key, boolean value) {
        this.setMeta(key, value, Parsers.booleanParser);
    }

    public void setMeta(String key, String value) {
        this.setMeta(key, value, Parsers.stringParser);
    }

    public void setMeta(String key, int value) {
        this.setMeta(key, value, Parsers.integerParser);
    }

    public <T> void setMeta(String key, T value, MetaParser<T> parser) {
        this.setMeta(key, parser.toBytes(value));
    }

    public void removeMeta(String key) {
        if (hasMeta(key)) {
            map.remove(key);
            Rectangular.getServiceManager().runAsync(()
                    -> Rectangular.getDatabase().removePlayerMeta(uuid, key));
        }
    }

    public interface MetaParser<T> {
        byte[] toBytes(T in);
        T fromBytes(byte[] in);
    }

    public static final class Parsers {
        public static final StringParser stringParser = new StringParser();
        public static final BooleanParser booleanParser = new BooleanParser();
        public static final IntegerParser integerParser = new IntegerParser();
    }

    private static class StringParser implements MetaParser<String> {
        @Override public byte[] toBytes(String in) {
            return in.getBytes();
        }
        @Override public String fromBytes(byte[] in) {
            if (in.length == 0) {
                return "";
            }
            return new String(in);
        }
    }

    private static class IntegerParser implements MetaParser<Integer> {
        @Override public byte[] toBytes(Integer in) {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) (in >> 24);
            bytes[1] = (byte) (in >> 16);
            bytes[2] = (byte) (in >> 8);
            bytes[3] = in.byteValue();
            return bytes;
        }
        @Override public Integer fromBytes(byte[] in) {
            if (in.length > 3) {
                return (in[0]<<24)&0xff000000
                        |(in[1]<<16)&0x00ff0000
                        |(in[2]<<8)&0x0000ff00
                        |(in[3])&0x000000ff;
            }
            return null;
        }
    }

    private static class BooleanParser implements MetaParser<Boolean> {
        @Override public byte[] toBytes(Boolean in) {
            return new byte[] {
                    (byte) (in ? 1 : 0)
            };
        }
        @Override public Boolean fromBytes(byte[] in) {
            if (in.length > 0) {
                return in[0] == 1;
            }
            return null;
        }
    }
}
