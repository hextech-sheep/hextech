package com.hextechsheep.hextech.persistence;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.msgpack.jackson.dataformat.MessagePackFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jetbrains.exodus.ArrayByteIterable;
import jetbrains.exodus.ByteIterable;
import jetbrains.exodus.bindings.StringBinding;
import jetbrains.exodus.env.Environment;
import jetbrains.exodus.env.Environments;
import jetbrains.exodus.env.Store;
import jetbrains.exodus.env.StoreConfig;

public class HextechDataStore implements AutoCloseable {
    public static class Configuration {
        private String dataDirectory = Paths.get(System.getProperty("user.home"), ".hextech", "data").toString();

        @Override
        public boolean equals(final Object obj) {
            if(this == obj) {
                return true;
            }
            if(obj == null) {
                return false;
            }
            if(getClass() != obj.getClass()) {
                return false;
            }
            final Configuration other = (Configuration)obj;
            if(dataDirectory == null) {
                if(other.dataDirectory != null) {
                    return false;
                }
            } else if(!dataDirectory.equals(other.dataDirectory)) {
                return false;
            }
            return true;
        }

        public String getDataDirectory() {
            return dataDirectory;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (dataDirectory == null ? 0 : dataDirectory.hashCode());
            return result;
        }

        public void setDataDirectory(final String dataDirectory) {
            this.dataDirectory = dataDirectory;
        }
    }

    public class Transaction implements AutoCloseable {
        private final Store store;
        private final jetbrains.exodus.env.Transaction transaction;

        private Transaction(final String table) {
            transaction = xodus.beginTransaction();
            store = xodus.openStore(table, STORE_CONFIG, transaction);
        }

        @Override
        public void close() {
            transaction.commit();
        }

        public void delete(final String key) {
            store.delete(transaction, StringBinding.stringToEntry(key));
        }

        public <T> T get(final Class<T> type, final String key) {
            try {
                return fromByteIterable(type, store.get(transaction, StringBinding.stringToEntry(key)));
            } catch(final IOException e) {
                throw new RuntimeException(e);
            }
        }

        public <T> void put(final String key, final T value) {
            try {
                store.put(transaction, StringBinding.stringToEntry(key), toByteIterable(value));
            } catch(final JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static final Map<Configuration, HextechDataStore> instances = new HashMap<>();

    private static final ObjectMapper MAPPER =
        new ObjectMapper(new MessagePackFactory()).disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).setSerializationInclusion(Include.NON_DEFAULT);

    private static final StoreConfig STORE_CONFIG = StoreConfig.WITHOUT_DUPLICATES_WITH_PREFIXING;

    private static <T> T fromByteIterable(final Class<T> type, final ByteIterable value) throws JsonParseException, JsonMappingException, IOException {
        if(value != null) {
            return MAPPER.readValue(value.getBytesUnsafe(), type);
        }
        return null;
    }

    public static HextechDataStore getInstance() {
        return getInstance(new Configuration());
    }

    public static HextechDataStore getInstance(final Configuration configuration) {
        HextechDataStore instance = instances.get(configuration);
        if(instance == null) {
            synchronized(instances) {
                instance = instances.get(configuration);
                if(instance == null) {
                    final HextechDataStore newInstance = new HextechDataStore(configuration);
                    instances.put(configuration, newInstance);

                    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                        if(newInstance.isOpen()) {
                            newInstance.close();
                        }
                    }));
                    instance = newInstance;
                }
            }
        }
        return instance;
    }

    private static <T> ByteIterable toByteIterable(final T value) throws JsonProcessingException {
        return new ArrayByteIterable(MAPPER.writeValueAsBytes(value));
    }

    private final Environment xodus;

    public HextechDataStore() {
        this(new Configuration());
    }

    public HextechDataStore(final Configuration config) {
        xodus = Environments.newInstance(config.getDataDirectory());
    }

    @Override
    public void close() {
        xodus.close();
    }

    public boolean isOpen() {
        return xodus.isOpen();
    }

    public Transaction open(final String table) {
        return new Transaction(table);
    }
}
