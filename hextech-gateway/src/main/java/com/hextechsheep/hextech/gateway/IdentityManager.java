package com.hextechsheep.hextech.gateway;

import java.util.HashMap;
import java.util.Map;

import com.hextechsheep.hextech.persistence.HextechDataStore;
import com.hextechsheep.hextech.persistence.HextechDataStore.Transaction;

public class IdentityManager {
    public static class Configuration {
        private HextechDataStore.Configuration dataStore = new HextechDataStore.Configuration();
        private String tableName = DEFAULT_TABLE_NAME;

        public HextechDataStore.Configuration getDataStore() {
            return dataStore;
        }

        public String getTableName() {
            return tableName;
        }

        public void setDataStore(final HextechDataStore.Configuration dataStore) {
            this.dataStore = dataStore;
        }

        public void setTableName(final String tableName) {
            this.tableName = tableName;
        }
    }

    public class Identity {
        private final Map<String, String> identities;

        public Identity(final Map<String, String> identities) {
            this.identities = identities;
        }

        public String getMinecraftId() {
            return identities.get(MINECRAFT_ID);
        }

        public String getMinecraftName() {
            return identities.get(MINECRAFT_NAME);
        }

        public void setMinecraftId(final String minecraftId) {
            if(minecraftId == null) {
                throw new IllegalArgumentException("minecraft id must not be null!");
            }

            final String old = identities.get(MINECRAFT_ID);
            identities.put(MINECRAFT_ID, minecraftId);
            try(Transaction transaction = dataStore.open(tableName)) {
                if(old != null) {
                    transaction.delete(old);
                }
                transaction.put(minecraftId, identities);
            }
        }

        public void setMinecraftName(final String minecraftName) {
            identities.put(MINECRAFT_NAME, minecraftName);
            try(Transaction transaction = dataStore.open(tableName)) {
                transaction.put(identities.get(MINECRAFT_ID), identities);
            }
        }
    }

    private static final String DEFAULT_TABLE_NAME = "hextech-gateway.identity-manager";
    private static final String MINECRAFT_ID = "minecraft-id";
    private static final String MINECRAFT_NAME = "minecraft-name";
    private final HextechDataStore dataStore;
    private final String tableName;

    public IdentityManager() {
        this(new Configuration());
    }

    public IdentityManager(final Configuration configuration) {
        tableName = configuration.getTableName();
        dataStore = HextechDataStore.getInstance(configuration.getDataStore());
    }

    @SuppressWarnings("unchecked")
    public Identity get(final String minecraftId) {
        Map<String, String> identities;
        try(Transaction transaction = dataStore.open(tableName)) {
            identities = transaction.get(HashMap.class, minecraftId);
            if(identities == null) {
                identities = new HashMap<>();
                identities.put(MINECRAFT_ID, minecraftId);
                transaction.put(minecraftId, identities);
            }
        }
        return new Identity(identities);
    }
}
