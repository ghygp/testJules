package com.example.demo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.lang.NonNull;

/**
 * Xlsx metadata storage.
 *
 * @author zakhar.kalachev
 * @since 23.04.2025
 */
public class XlsxMetadata {

    /**
     * Immutable empty instance of metadata.
     * Does not store any data and ignores all modification attempts.
     */
    public static final XlsxMetadata EMPTY = new XlsxMetadata() {

        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public Map<String, MetadataField> getStorage() {
            return Collections.emptyMap();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void put(@NonNull String key, MetadataField value) {
            // No-op
        }
    };

    /**
     * Metadata storage.
     */
    private final Map<String, MetadataField> storage;

    /**
     * Constructor.
     */
    public XlsxMetadata() {
        this.storage = new HashMap<>();
    }

    /**
     * Gets metadata storage.
     *
     * @return metadata storage
     */
    @NonNull
    public Map<String, MetadataField> getStorage() {
        return storage;
    }

    /**
     * Gets specific metadata value by key.
     *
     * @param key the key for the metadata
     * @return the metadata value, or null if not exists
     */
    public MetadataField get(@NonNull String key) {
        Objects.requireNonNull(key, "Key cannot be null");
        return storage.get(key);
    }

    /**
     * Puts metadata field.
     *
     * @param key the key for metadata
     * @param value the value to store
     */
    public void put(@NonNull String key, MetadataField value) {
        Objects.requireNonNull(key, "Key cannot be null");
        storage.put(key, value);
    }

    /**
     * Checks if metadata contains a mapping for the specified key.
     *
     * @param key the key to check
     * @return {@code true} if metadata contains a mapping, {@code false} otherwise
     */
    public boolean contains(@NonNull String key) {
        Objects.requireNonNull(key, "Key cannot be null");
        return storage.containsKey(key);
    }

    /**
     * Metadata value.
     */
    public static class MetadataField {

        private String value;

        private String description;

        /**
         * Gets metadata field value.
         *
         * @return metadata field value
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets metadata field value.
         *
         * @param value metadata field value
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Sets metadata field description.
         *
         * @return metadata field description
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets metadata field description.
         *
         * @param description metadata field description
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * Sets metadata field value.
         *
         * @param value metadata field value
         * @return self
         */
        public MetadataField withValue(String value) {
            setValue(value);
            return this;
        }

        /**
         * Sets metadata field description.
         *
         * @param description metadata field description
         * @return self
         */
        public MetadataField withDescription(String description) {
            setDescription(description);
            return this;
        }
    }
}
