package com.example.demo;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class TimeBoundaryUtilsTest {

    private static final Date TEST_DATE_NON_NULL = new Date(1678886400000L); // Example: 2023-03-15T12:00:00Z
    private static final long TEST_DATE_NON_NULL_TIMESTAMP = 1678886400000L;

    private static final Date DATE_1 = new Date(1000000000000L); // Approx 2001-09-09
    private static final Date DATE_2 = new Date(1100000000000L); // Approx 2004-11-13
    private static final Date DATE_1_COPY = new Date(1000000000000L); // Same as DATE_1

    static Stream<Arguments> toUpperBoundArgs() {
        return Stream.of(
                Arguments.of(TEST_DATE_NON_NULL, TEST_DATE_NON_NULL_TIMESTAMP),
                Arguments.of(null, TimeBoundaryUtils.TIME_POSITIVE_INFINITY)
        );
    }

    @ParameterizedTest
    @MethodSource("toUpperBoundArgs")
    void toUpperBound_variousInputs_returnsCorrectTimestampOrPositiveInfinity(Date inputDate, long expectedTimestamp) {
        long actualTimestamp = TimeBoundaryUtils.toUpperBound(inputDate);
        assertEquals(expectedTimestamp, actualTimestamp);
    }

    static Stream<Arguments> toLowerBoundArgs() {
        return Stream.of(
                Arguments.of(TEST_DATE_NON_NULL, TEST_DATE_NON_NULL_TIMESTAMP),
                Arguments.of(null, TimeBoundaryUtils.TIME_NEGATIVE_INFINITY)
        );
    }

    @ParameterizedTest
    @MethodSource("toLowerBoundArgs")
    void toLowerBound_variousInputs_returnsCorrectTimestampOrNegativeInfinity(Date inputDate, long expectedTimestamp) {
        long actualTimestamp = TimeBoundaryUtils.toLowerBound(inputDate);
        assertEquals(expectedTimestamp, actualTimestamp);
    }

    static Stream<Arguments> upperBoundCompareToArgs() {
        return Stream.of(
                // what < to
                Arguments.of(DATE_1, DATE_2, -1),
                // what == to
                Arguments.of(DATE_1, DATE_1_COPY, 0),
                Arguments.of(TEST_DATE_NON_NULL, TEST_DATE_NON_NULL, 0),
                // what > to
                Arguments.of(DATE_2, DATE_1, 1),
                // what is null, to is not null (null is positive infinity, so null > date_2)
                Arguments.of(null, DATE_2, 1),
                // what is not null, to is null (null is positive infinity, so date_1 < null)
                Arguments.of(DATE_1, null, -1),
                // what is null, to is null (both positive infinity, so null == null)
                Arguments.of(null, null, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("upperBoundCompareToArgs")
    void upperBoundCompareTo_variousInputCombinations_returnsCorrectComparisonResult(Date what, Date to, int expectedResult) {
        int actualResult = TimeBoundaryUtils.upperBoundCompareTo(what, to);
        assertEquals(expectedResult, actualResult);
    }

    static Stream<Arguments> lowerBoundCompareToArgs() {
        return Stream.of(
                // what < to
                Arguments.of(DATE_1, DATE_2, -1),
                // what == to
                Arguments.of(DATE_1, DATE_1_COPY, 0),
                Arguments.of(TEST_DATE_NON_NULL, TEST_DATE_NON_NULL, 0),
                // what > to
                Arguments.of(DATE_2, DATE_1, 1),
                // what is null, to is not null (null is negative infinity, so null < date_2)
                Arguments.of(null, DATE_2, -1),
                // what is not null, to is null (null is negative infinity, so date_1 > null)
                Arguments.of(DATE_1, null, 1),
                // what is null, to is null (both negative infinity, so null == null)
                Arguments.of(null, null, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("lowerBoundCompareToArgs")
    void lowerBoundCompareTo_variousInputCombinations_returnsCorrectComparisonResult(Date what, Date to, int expectedResult) {
        int actualResult = TimeBoundaryUtils.lowerBoundCompareTo(what, to);
        assertEquals(expectedResult, actualResult);
    }

    static Stream<Arguments> selectLatestArgs() {
        return Stream.of(
                // what is later than to
                Arguments.of(DATE_2, DATE_1, DATE_2),
                // to is later than what
                Arguments.of(DATE_1, DATE_2, DATE_2),
                // what and to are equal
                Arguments.of(DATE_1, DATE_1_COPY, DATE_1_COPY), // or DATE_1, depends on impl detail for equal
                Arguments.of(TEST_DATE_NON_NULL, TEST_DATE_NON_NULL, TEST_DATE_NON_NULL),
                // what is null, to is not null (null is positive infinity, so 'what' (null) is latest)
                Arguments.of(null, DATE_2, null),
                // what is not null, to is null (null is positive infinity, so 'to' (null) is latest)
                Arguments.of(DATE_1, null, null),
                // Both what and to are null (both positive infinity, 'to' (null) is returned due to <=)
                Arguments.of(null, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("selectLatestArgs")
    void selectLatest_variousInputCombinations_returnsLaterDate(Date what, Date to, Date expectedDate) {
        Date actualDate = TimeBoundaryUtils.selectLatest(what, to);
        assertEquals(expectedDate, actualDate);
    }

    static Stream<Arguments> selectEarliestArgs() {
        return Stream.of(
                // what is earlier than to
                Arguments.of(DATE_1, DATE_2, DATE_1),
                // to is earlier than what
                Arguments.of(DATE_2, DATE_1, DATE_1),
                // what and to are equal
                Arguments.of(DATE_1, DATE_1_COPY, DATE_1_COPY), // or DATE_1, depends on impl detail for equal
                Arguments.of(TEST_DATE_NON_NULL, TEST_DATE_NON_NULL, TEST_DATE_NON_NULL),
                // what is null, to is not null (null is negative infinity, so 'what' (null) is earliest)
                Arguments.of(null, DATE_2, null),
                // what is not null, to is null (null is negative infinity, so 'to' (null) is earliest)
                Arguments.of(DATE_1, null, null),
                // Both what and to are null (both negative infinity, 'to' (null) is returned due to >=)
                Arguments.of(null, null, null)
        );
    }

    @ParameterizedTest
    @MethodSource("selectEarliestArgs")
    void selectEarliest_variousInputCombinations_returnsEarlierDate(Date what, Date to, Date expectedDate) {
        Date actualDate = TimeBoundaryUtils.selectEarliest(what, to);
        assertEquals(expectedDate, actualDate);
    }
}
