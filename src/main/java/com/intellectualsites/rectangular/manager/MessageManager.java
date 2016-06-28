package com.intellectualsites.rectangular.manager;

import com.google.common.collect.ImmutableMap;
import com.intellectualsites.rectangular.config.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

public class MessageManager {

    private final ImmutableMap<String, String> messages;

    private MessageManager(Map<String, String> translations) {
        ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
        builder.putAll(translations);
        for (Message m : Message.values()) {
            if (!translations.containsKey(m.toString())) {
                builder.put(m.toString(), m.getDefaultString());
            }
        }
        this.messages = builder.build();
    }

    public String getMessage(String key) {
        if (!messages.containsKey(key)) {
            return "{UnknownMessage:" + key + "}";
        }
        return messages.get(key);
    }

    public static MessageManager create(File path, Consumer<String> logger, String name) throws Exception {
        File file = new File(path, name + ".properties");
        logger.accept("Loading messages from: " + file.getPath());
        if (!file.exists()) {
            if (!file.createNewFile()) {
                throw new Exception("Couldn't create file :(");
            }
        }
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            properties.load(fileInputStream);
        }
        Map<String, String> map = new HashMap<>();
        for (Map.Entry e : properties.entrySet()) {
            map.put(e.getKey().toString(), e.getValue().toString());
        }
        MessageManager manager = new MessageManager(map);
        manager.messages.entrySet().stream().filter(entry ->
                !properties.containsKey(entry.getKey())).forEach(entry -> properties.put(entry.getKey(), entry.getValue()));
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            properties.store(fileOutputStream, "Updated on: " + new Date());
        }
        return manager;
    }
}
