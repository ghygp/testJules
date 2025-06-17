package com.example.demo;/*
 * Universe Platform
 * Copyright (c) 2021-2025, UNIVERSE DATA LLC, All rights reserved.
 *
 * Commercial License
 * This version of Universe Platform (previously Unidata Platform) is licensed
 * commercially and is the appropriate option for the vast majority of use cases.
 *
 * Please see the Universe Data Licensing page at: https://universe-data.ru/license/
 * For clarification or additional options, please contact: info@universe-data.ru
 * -------
 * Disclaimer:
 * -------
 * THIS SOFTWARE IS DISTRIBUTED "AS-IS" WITHOUT ANY WARRANTIES, CONDITIONS AND
 * REPRESENTATIONS WHETHER EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE
 * IMPLIED WARRANTIES AND CONDITIONS OF MERCHANTABILITY, MERCHANTABLE QUALITY,
 * FITNESS FOR A PARTICULAR PURPOSE, DURABILITY, NON-INFRINGEMENT, PERFORMANCE AND
 * THOSE ARISING BY STATUTE OR FROM CUSTOM OR USAGE OF TRADE OR COURSE OF DEALING.
 */

import java.time.format.DateTimeFormatter;

/**
 * Class storing formatters for a specific locale.
 *
 * @author Mikhail.Kataranov
 * @since 24.12.2024
 */
public class LocaleDateFormatters {

    /**
     * Formatter for date.
     */
    private final DateTimeFormatter dateFormatter;

    /**
     * Formatter for time.
     */
    private final DateTimeFormatter timeFormatter;

    /**
     * Formatter for date with time.
     */
    private final DateTimeFormatter dateTimeFormatter;

    private LocaleDateFormatters(LocaleDateFormattersBuilder builder) {
        this.dateFormatter = builder.dateFormatter;
        this.timeFormatter = builder.timeFormatter;
        this.dateTimeFormatter = builder.dateTimeFormatter;
    }

    /**
     * Creates builder.
     *
     * @return builder
     */
    public static LocaleDateFormattersBuilder builder() {
        return new LocaleDateFormattersBuilder();
    }

    /**
     * Returns date formatter.
     *
     * @return date formatter
     */
    public DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    /**
     * Returns time formatter.
     *
     * @return time formatter
     */
    public DateTimeFormatter getTimeFormatter() {
        return timeFormatter;
    }

    /**
     * Returns date/time formatter.
     *
     * @return date/time formatter
     */
    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    /**
     * Builder.
     */
    public static class LocaleDateFormattersBuilder {

        /**
         * Formatter for date.
         */
        private DateTimeFormatter dateFormatter;

        /**
         * Formatter for time.
         */
        private DateTimeFormatter timeFormatter;

        /**
         * Formatter for date with time.
         */
        private DateTimeFormatter dateTimeFormatter;

        private LocaleDateFormattersBuilder self() {
            return this;
        }

        /**
         * Sets date formatter.
         *
         * @param dateFormatter date formatter to set
         * @return self
         */
        public LocaleDateFormattersBuilder dateFormatter(DateTimeFormatter dateFormatter) {
            this.dateFormatter = dateFormatter;
            return self();
        }

        /**
         * Sets time formatter.
         *
         * @param timeFormatter time formatter to set
         * @return self
         */
        public LocaleDateFormattersBuilder timeFormatter(DateTimeFormatter timeFormatter) {
            this.timeFormatter = timeFormatter;
            return self();
        }

        /**
         * Sets date/time formatter.
         *
         * @param dateTimeFormatter date/time formatter to set
         * @return self
         */
        public LocaleDateFormattersBuilder dateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
            this.dateTimeFormatter = dateTimeFormatter;
            return self();
        }

        /**
         * Build the object.
         *
         * @return object
         */
        public LocaleDateFormatters build() {
            return new LocaleDateFormatters(this);
        }
    }
}
