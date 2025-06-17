package com.example.demo;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.threeten.extra.chrono.JulianChronology;
import org.threeten.extra.chrono.JulianDate;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;
import static org.apache.commons.lang3.StringUtils.isNumeric;

/**
 * @author Mikhail Mikhailov
 * Various conversions.
 */
public class ConvertUtils {
    /**
     * This logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConvertUtils.class);
    /**
     * No TZ offset timestamp formatter.
     */
    public static final DateTimeFormatter DEFAULT_LOCAL_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    /**
     * No TZ offset timestamp formatter allowing omitted milliseconds.
     */
    public static final DateTimeFormatter LOCAL_TIMESTAMP_FORMATTER_OPTIONAL_MILLISECONDS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]");
    /**
     * Zero offset formatter.
     */
    public static final DateTimeFormatter ZULU_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    /**
     * User timestamp formater.
     */
    public static final DateTimeFormatter USER_TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    /**
     * Fix UTC ZID here.
     */
    public static final ZoneId UTC_ZONE_ID = ZoneId.of("UTC");
    /**
     * The point at which the Gregorian calendar rules are used, measured in
     * milliseconds from the standard epoch.  Default is October 15, 1582
     * (Gregorian) 00:00:00 UTC or -12219292800000L.  For this value, October 4,
     * 1582 (Julian) is followed by October 15, 1582 (Gregorian).  This
     * corresponds to Julian day number 2299161.
     *
     * @serial
     */
    private static final long DEFAULT_GREGORIAN_CUTOVER = -12219292800000L;
    /**
     * Start of epoch as local date.
     */
    private static final LocalDate START_OF_EPOCH = LocalDate.of(1970, 1, 1);
    /**
     * Local date time formatter (dd.MM.yyyy format).
     */
    public static final DateTimeFormatter DD_MM_YYYY_LOCAL_DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
            .appendValue(DAY_OF_MONTH, 2)
            .appendLiteral('.')
            .appendValue(MONTH_OF_YEAR, 2)
            .appendLiteral('.')
            .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
            .toFormatter();


    private static final String[] DATE_PATTERNS = {
            "dd.M.yyyy",
            "dd.MM.yyyy",
            "dd.MMM.yyyy",
            "M/dd/yyyy",
            "dd-MM-yyyy",
            "dd-MMM-yyyy",
            "dd.MM.+yyyy"
    };

    private static final DateTimeFormatter[] DATE_TIME_FORMATTERS = new DateTimeFormatter[DATE_PATTERNS.length];

    static {
        for (int i = 0; i < DATE_PATTERNS.length; i++) {
            DATE_TIME_FORMATTERS[i] = createFormatter(DATE_PATTERNS[i]);
        }
    }

    private static DateTimeFormatter createFormatter(final String datePattern) {
        return new DateTimeFormatterBuilder()
                .parseLenient()
                .optionalStart()
                .appendPattern(datePattern)
                .optionalEnd()

                .optionalStart()
                .appendLiteral(' ')
                .optionalEnd()

                .optionalStart()
                .appendPattern("HH")
                .optionalStart()
                .appendLiteral(':')
                .appendPattern("mm")
                .optionalStart()
                .appendLiteral(':')
                .appendPattern("ss")
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
                .optionalEnd()
                .optionalEnd()

                .optionalStart()
                .appendLiteral(' ')
                .appendText(ChronoField.AMPM_OF_DAY)
                .optionalEnd()

                .optionalEnd()
                .toFormatter();
    }

    private static final Map<Locale, LocaleDateFormatters> localeFormatters = new ConcurrentHashMap<>();

    /**
     * Expected length of record id.
     */
    private static final int UUID_LENGTH = 36;
    /**
     * Separator between period id and record id.
     */
    private static final char DASH = '-';

    /**
     * Constructor.
     */
    private ConvertUtils() {
        super();
    }

    /**
     * Date 2 LocalDate.
     *
     * @param d the date
     * @return local date
     */
    public static LocalDate date2LocalDate(Date d) {

        if (d != null && d.getTime() < DEFAULT_GREGORIAN_CUTOVER) {
            JulianDate julianDate = JulianChronology.INSTANCE.date(d.toInstant().atZone(ZoneId.systemDefault()));
            return LocalDate.of(
                    julianDate.get(ChronoField.YEAR_OF_ERA),
                    julianDate.get(ChronoField.MONTH_OF_YEAR),
                    julianDate.get(ChronoField.DAY_OF_MONTH));
        }

        return d == null ? null : new Date(d.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Date 2 LocalTime.
     *
     * @param d the date
     * @return local time
     */
    public static LocalTime date2LocalTime(Date d) {
        return d == null ? null : new Date(d.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
    }

    /**
     * Date 2 LocalDateTime.
     *
     * @param d the date
     * @return local date time
     */
    public static LocalDateTime date2LocalDateTime(Date d) {

        LocalDateTime ldt = d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (d != null && d.getTime() < DEFAULT_GREGORIAN_CUTOVER) {
            JulianDate julianDate = JulianChronology.INSTANCE.date(d.toInstant().atZone(ZoneId.systemDefault()));
            return LocalDateTime.of(
                    julianDate.get(ChronoField.YEAR_OF_ERA),
                    julianDate.get(ChronoField.MONTH_OF_YEAR),
                    julianDate.get(ChronoField.DAY_OF_MONTH),
                    ldt.getHour(),
                    ldt.getMinute(),
                    ldt.getSecond(),
                    ldt.getNano());
        }

        return ldt;
    }

    public static OffsetDateTime date2OffsetDateTime(Date d) {
        return d == null ? null : OffsetDateTime.ofInstant(d.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalDate 2 Date.
     *
     * @param d the local date
     * @return date
     */
    public static Date localDate2Date(LocalDate d) {
        return d == null ? null : Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalTime 2 Date.
     *
     * @param d the local time
     * @return date
     */
    public static Date localTime2Date(LocalTime d) {
        return d == null ? null : Date.from(d.atDate(START_OF_EPOCH).atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime 2 Date.
     *
     * @param d the local date time
     * @return date
     */
    public static Date localDateTime2Date(LocalDateTime d) {
        return d == null ? null : Date.from(d.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime 2 Date.
     *
     * @param d the local date time
     * @return date
     */
    public static Date localDateTime2ZonedDateTime(LocalDateTime d) {

        return d == null ? null : Date.from(d.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * ZonedDateTime 2 LocalDateTime.
     *
     * @param d zoned date time
     * @return local date time
     */
    public static LocalDateTime zonedDateTime2LocalDate(ZonedDateTime d) {
        return d == null
                ? null
                : d.toLocalDateTime();
    }

    /**
     * LocalDateTime 2 Date.
     *
     * @param ldt the local date time
     * @return date
     */
    public static OffsetDateTime localDateTime2OffsetDateTime(LocalDateTime ldt, ZoneId zoneId) {
        return ldt == null ? null : OffsetDateTime.ofInstant(ldt.atZone(ZoneId.systemDefault()).toInstant(), zoneId);
    }

    /**
     * Converts {@link OffsetDateTime} to {@link Date}.
     *
     * @param z the offset date time
     * @return date
     */
    public static Date offsetDateTime2Date(OffsetDateTime z) {
        return z == null ? null : Date.from(z.toInstant());
    }

    /**
     * @param z the zoned date time
     * @return date
     */
    public static Date zonedDateTime2Date(ZonedDateTime z) {
        return z == null ? null : Date.from(z.toInstant());
    }

    /**
     * Converts to zoned DT from {@linkplain Date}.
     *
     * @param date the {@link Date}
     * @return ZDT
     */
    public static ZonedDateTime zonedDateTimeFromDate(final Date date) {
        return date == null ? null : ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * LocalTime 2 LocalDateTime.
     *
     * @param d the local time
     * @return date
     */
    public static LocalDateTime localTime2LocalDateTime(LocalTime d) {
        return d == null ? null : LocalDateTime.of(START_OF_EPOCH, d);
    }

    /**
     * LocalDate 2 LocalDateTime.
     *
     * @param d the local date
     * @return date
     */
    public static LocalDateTime localDate2LocalDateTime(LocalDate d) {
        return d == null ? null : LocalDateTime.of(d, LocalTime.MIN);
    }

    public static LocalDate date2LocalDateWithoutOffset(final Date d) {
        if (d != null && d.getTime() < DEFAULT_GREGORIAN_CUTOVER) {
            return new Timestamp(d.getTime()).toLocalDateTime().toLocalDate();
        }

        return d == null ? null : new Date(d.getTime()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime date2LocalDateTimeWithoutOffset(Date d) {
        LocalDateTime ldt = d == null ? null : d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        if (d != null && d.getTime() < DEFAULT_GREGORIAN_CUTOVER) {
            return new Timestamp(d.getTime()).toLocalDateTime();
        }

        return ldt;
    }

    public static String localDateTime2UTCAndFormat(LocalDateTime ldt) {
        return ldt == null ? null : ZULU_TIMESTAMP_FORMATTER.format(localDateTime2OffsetDateTime(ldt, ConvertUtils.UTC_ZONE_ID));
    }

    /**
     * Parses string representation of date according to date format from
     * {@see DEFAULT_LOCAL_TIMESTAMP_FORMATTER}.
     *
     * @param param string representation of date.
     * @return parsed date.
     */
    public static Date string2Date(String param) {

        try {
            if (StringUtils.isNotBlank(param)) {
                return localDateTime2Date(LocalDateTime.parse(param, DEFAULT_LOCAL_TIMESTAMP_FORMATTER));
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot parse validity period boundary {}.", param, e);
        }

        return null;
    }

    /**
     * Reads boundary date.
     *
     * @param param the timestamp as {@linkplain Date} object
     * @return date string or null
     */
    public static String date2String(Date param) {

        try {
            return Objects.isNull(param)
                    ? null
                    : DEFAULT_LOCAL_TIMESTAMP_FORMATTER.format(ConvertUtils.date2LocalDateTime(param));
        } catch (Exception e) {
            LOGGER.warn("Cannot unparse date [{}] to string.", param, e);
        }

        return null;
    }

    /**
     * Parses string representation of instant in ISO 8601 format to {@link Date}, using {@link DateTimeFormatter#ISO_INSTANT}.
     *
     * @param dateAsStringUTC {@link Date} instance as string in ISO 8601 format
     * @return parsed {@link Date} instance or {@code null}
     */
    public static Date stringUTC2Date(String dateAsStringUTC) {

        try {
            if (StringUtils.isNotBlank(dateAsStringUTC)) {
                return Date.from(Instant.parse(dateAsStringUTC));
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot parse string [{}] as Date.", dateAsStringUTC, e);
        }

        return null;
    }
    /**
     * Parses string representation of instant in ISO 8601 format to {@link Date} with or without timezone, using {@link ConvertUtils#DEFAULT_LOCAL_TIMESTAMP_FORMATTER}.
     *
     * @param dateAsStringUTC {@link Date} instance as string in ISO 8601 format
     * @return parsed {@link Date} instance or {@code null}
     */
    public static Date stringLocalOrUTC2Date(String dateAsStringUTC) {

        try {
            if (StringUtils.isNotBlank(dateAsStringUTC)) {
                DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                        .append(LOCAL_TIMESTAMP_FORMATTER_OPTIONAL_MILLISECONDS)
                        .optionalStart().appendOffsetId()
                        .toFormatter()
                        .withZone(UTC_ZONE_ID);
                return Date.from(Instant.from(fmt.parse(dateAsStringUTC)));
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot parse string [{}] as Date.", dateAsStringUTC, e);
        }

        return null;
    }

    /**
     * Checks whether {@link ConvertUtils#stringLocalOrUTC2Date} can parse the provided string by calling it
     */
    public static boolean isStringLocalOrUTCConvertible(String dateAsStringUTC) {
        return StringUtils.isBlank(dateAsStringUTC) || ConvertUtils.stringLocalOrUTC2Date(dateAsStringUTC) != null;
    }

    /**
     * Parses string representation of instant in ISO 8601 format to {@link Date}, using {@link DateTimeFormatter#ISO_INSTANT}
     * with nanoseconds precision. {@link Timestamp} that extends {@link Date} is used.
     *
     * @param dateAsStringUTC {@link Timestamp} instance as string in ISO 8601 format
     * @return parsed {@link Timestamp} instance or {@code null}
     */
    public static Date stringUTC2DateWithNanos(String dateAsStringUTC) {

        Instant ludAsInstant = ConvertUtils.string2Instant(dateAsStringUTC);
        Timestamp ludAsDate;
        if (ludAsInstant == null) {
            ludAsDate = null;
        } else {
            ludAsDate = new Timestamp(ludAsInstant.toEpochMilli());
            ludAsDate.setNanos(ludAsInstant.getNano());
        }

        return ludAsDate;
    }

    /**
     * Converts {@link Date} instance to string in ISO 8601 format, using {@link DateTimeFormatter#ISO_INSTANT}.
     *
     * @param date {@link Date} instance
     * @return resulting string or {@code null}
     */
    public static String date2StringUTC(Date date) {

        try {
            if (Objects.nonNull(date)) {
                return DateTimeFormatter.ISO_INSTANT.format(date.toInstant());
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot format Date [{}] to string.", date, e);
        }

        return null;
    }

    /**
     * Converts {@link LocalDate} instance to string in ISO format, using {@link DateTimeFormatter#ISO_LOCAL_DATE}.
     *
     * @param v`{@link LocalDate} instance
     * @return resulting string or {@code null}
     */
    @Nullable
    public static String localDate2String(LocalDate v) {

        try {
            if (Objects.nonNull(v)) {
                return DateTimeFormatter.ISO_LOCAL_DATE.format(v);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot format local date [{}] to string.", v, e);
        }

        return null;
    }

    /**
     * Converts {@link LocalDate} instance to string in custom format using provided {@link DateTimeFormatter}.
     *
     * @param formatter {@link DateTimeFormatter} instance
     * @param date      {@link LocalDate} instance
     * @return resulting string or {@code null}
     */
    @Nullable
    public static String localDate2String(DateTimeFormatter formatter, LocalDate date) {

        try {
            if (formatter != null && date != null) {
                return formatter.format(date);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot format local date [{}] to string using custom date time formatter {}.", date, formatter, e);
        }

        return null;
    }

    public static LocalDate string2LocalDate(String v) {

        try {
            if (StringUtils.isNotBlank(v)) {
                return LocalDate.parse(v, DateTimeFormatter.ISO_LOCAL_DATE);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot parse local date [{}] from string.", v, e);
        }

        return null;
    }

    public static String localTime2String(LocalTime v) {

        try {
            if (Objects.nonNull(v)) {
                return DateTimeFormatter.ISO_LOCAL_TIME.format(v);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot format local time [{}] to string.", v, e);
        }

        return null;
    }

    public static LocalTime string2LocalTime(String v) {

        try {
            if (StringUtils.isNotBlank(v)) {
                return LocalTime.parse(v, DateTimeFormatter.ISO_LOCAL_TIME);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot parse local time [{}] from string.", v, e);
        }

        return null;
    }

    public static String localDateTime2String(LocalDateTime v) {

        try {
            if (Objects.nonNull(v)) {
                return DEFAULT_LOCAL_TIMESTAMP_FORMATTER.format(v);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot format local date time [{}] to string.", v, e);
        }

        return null;
    }

    public static LocalDateTime string2LocalDateTime(String v) {

        try {
            if (StringUtils.isNotBlank(v)) {
                return LocalDateTime.parse(v, DEFAULT_LOCAL_TIMESTAMP_FORMATTER);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot parse local date time [{}] from string.", v, e);
        }

        return null;
    }

    public static LocalDateTime string2LocalDateTimeWithNano(String v) {

        try {
            if (StringUtils.isNotBlank(v)) {
                return LocalDateTime.parse(v, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot parse local date time with nano [{}] from string.", v, e);
        }

        return null;
    }

    public static String instant2String(Instant v) {

        try {
            if (Objects.nonNull(v)) {
                return DateTimeFormatter.ISO_INSTANT.format(v);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot format instant [{}] to string.", v, e);
        }

        return null;
    }

    public static Instant string2Instant(String v) {

        try {
            if (StringUtils.isNotBlank(v)) {
                return Instant.parse(v);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot parse instant [{}] from string.", v, e);
        }

        return null;
    }

    public static String time2String(LocalTime v) {

        try {
            if (Objects.nonNull(v)) {
                return DateTimeFormatter.ISO_TIME.format(v);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot format time [{}] to string.", v, e);
        }

        return null;
    }

    public static LocalTime string2Time(String v) {

        try {
            if (StringUtils.isNotBlank(v)) {
                return LocalTime.parse(v);
            }
        } catch (Exception e) {
            LOGGER.warn("Cannot parse time [{}] from string.", v, e);
        }

        return null;
    }

    public static Date instant2Date(Instant instant) {
        return instant == null ? null : Date.from(instant);
    }

    public static LocalDateTime instant2LocalDateTimeUTC(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, UTC_ZONE_ID);
    }

    public static LocalDateTime instant2LocalDateTime(Instant instant) {
        return instant == null ? null : LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static Instant date2Instant(Date date) {
        return date == null ? null : date.toInstant();
    }

    public static Instant localDateTime2InstantUTC(LocalDateTime date) {
        return date == null ? null : date.toInstant(ZoneOffset.UTC);
    }

    public static Instant localDateTime2Instant(LocalDateTime date) {
        return date == null ? null : date.atZone(ZoneId.systemDefault()).toInstant();
    }

    public static Pair<Instant, Instant> pairDate2Instant(Pair<Date, Date> pair) {
        return pair == null ? null : Pair.of(date2Instant(pair.getLeft()), date2Instant(pair.getRight()));
    }

    public static Pair<Date, Date> pairInstant2Date(Pair<Instant, Instant> pair) {
        return pair == null ? null : Pair.of(instant2Date(pair.getLeft()), instant2Date(pair.getRight()));
    }

    /**
     * parse format 1m/1h/1d/1M/1y to ChronoUnit
     *
     * @param interval 1m/1h/1d/1M/1y
     * @return ChronoUnit
     * @throws IllegalArgumentException if interval cannot be parsed
     */
    @NonNull
    public static ChronoUnit parseChronoUnit(@NonNull String interval) {

        if (interval.length() < 2) {
            throw new IllegalArgumentException("Invalid time interval format: " + interval);
        }

        char unit = interval.charAt(interval.length() - 1);

        ChronoUnit chronoUnit;
        switch (unit) {
            case 'd':
                chronoUnit = ChronoUnit.DAYS;
                break;
            case 'h':
                chronoUnit = ChronoUnit.HOURS;
                break;
            case 'm':
                chronoUnit = ChronoUnit.MINUTES;
                break;
            case 'M':
                chronoUnit = ChronoUnit.MONTHS;
                break;
            case 'y':
                chronoUnit = ChronoUnit.YEARS;
                break;
            default:
                throw new IllegalArgumentException("Invalid time interval format: " + interval);
        }
        return chronoUnit;
    }

    /**
     * Parses file name from {@link StandardCharsets#ISO_8859_1} to {@link StandardCharsets#UTF_8} format.
     *
     * @param filename in {@link StandardCharsets#ISO_8859_1} format
     * @return filename in {@link StandardCharsets#UTF_8} format
     */
    public static String parseUTF8Filename(String filename) {
        return new String(filename.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
    }

    /**
     * Convert time in some locale as string to LocalTime using one of the default patterns.
     *
     * @param timeAsString time as sting
     * @return LocalTime
     */
    @Nullable
    public static LocalTime localeStringToLocalTime(String timeAsString) {

        final TemporalAccessor accessor = stringToTemporalAccessor(timeAsString);

        if (accessor == null) {
            return null;
        }

        final boolean hasTime = accessor.isSupported(ChronoField.HOUR_OF_DAY)
                && accessor.isSupported(ChronoField.MINUTE_OF_HOUR);

        if (hasTime) {
            return LocalTime.from(accessor);
        }

        return null;
    }

    /**
     * Convert date in some locale as string to LocalDate using one of the default patterns.
     *
     * @param dateAsString date as sting
     * @return LocalDate
     */
    @Nullable
    public static LocalDate localeStringToLocalDate(String dateAsString) {

        final TemporalAccessor accessor = stringToTemporalAccessor(dateAsString);

        if (accessor == null) {
            return null;
        }

        final boolean hasDate = accessor.isSupported(ChronoField.YEAR)
                && accessor.isSupported(ChronoField.MONTH_OF_YEAR)
                && accessor.isSupported(ChronoField.DAY_OF_MONTH);

        if (hasDate) {
            return LocalDate.from(accessor);
        }

        return null;
    }

    /**
     * Convert date in some locale as string to LocalDateTime using one of the default patterns.
     *
     * @param dateAsString date as sting
     * @return LocalDateTime
     */
    @Nullable
    public static LocalDateTime localeStringToLocalDateTime(String dateAsString) {
        final LocalTime localTime = localeStringToLocalTime(dateAsString);
        final LocalDate localDate = localeStringToLocalDate(dateAsString);

        if (localTime == null || localDate == null) {
            return null;
        }

        return LocalDateTime.of(localDate, localTime);
    }

    /**
     * Convert date in some locale as string to Date using one of the default patterns.
     *
     * @param dateAsString date as sting
     * @return Date
     */
    @Nullable
    public static Date localeStringToDate(String dateAsString) {
        return localDateTime2Date(localeStringToLocalDateTime(dateAsString));
    }

    /**
     * Convert date as string to TemporalAccessor using one of the default patterns.
     *
     * @param dateAsString date as string
     * @return TemporalAccessor
     */
    @Nullable
    public static TemporalAccessor stringToTemporalAccessor(String dateAsString) {
        for (DateTimeFormatter dateTimeFormatter : DATE_TIME_FORMATTERS) {
            try {
                return dateTimeFormatter.parse(dateAsString);
            } catch (Exception ignore) {
                // Do nothing, because DATE_FORMATS_LOCAL_DATE_TIME contains different formats and exception is normal
            }
        }

        return null;
    }

    /**
     * Splits the provided {@code str} into a period ID and record ID.
     *
     * <p>Expects that the period ID consists of 20 or 21 digits, the record ID has a length of 36 characters (UUID),
     * and they are concatenated with a dash ('-').</p>
     *
     * @param str the original string to separate
     * @return a {@code Pair} containing the period ID as the first element and the record ID as the second element,
     *         or {@code null} if the original string is {@code null} or does not match the expected format.
     */
    @Nullable
    public static Pair<String, String> stringToPeriodAndRecordIds(final String str) {
        if (str == null) {
            return null;
        }

        final int dashIndex = str.indexOf(DASH);
        if (dashIndex == -1) {
            return null;
        }

        final String prefix = str.substring(0, dashIndex);
        final String uuid = str.substring(dashIndex + 1);

        if ((prefix.length() == 20 || prefix.length() == 21)
                && uuid.length() == UUID_LENGTH
                && isNumeric(prefix)
        ) {
            return Pair.of(prefix, uuid);
        }

        return null;
    }
}

