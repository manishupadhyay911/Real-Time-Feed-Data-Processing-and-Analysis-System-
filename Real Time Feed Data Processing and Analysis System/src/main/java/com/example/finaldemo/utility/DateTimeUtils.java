package com.example.finaldemo.utility;

import com.google.protobuf.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;


public class DateTimeUtils {

    public static String localDateTimeToSqlDate(LocalDateTime ts){
        DateTimeFormatter dateTimeFormatter =new DateTimeFormatterBuilder()
                .appendPattern("dd-MM-yyyy hh:mm:ss")
                .optionalStart()
                .appendPattern(".")
                .appendFraction(ChronoField.MICRO_OF_SECOND, 1, 6, false)
                .optionalEnd()
                .toFormatter();
        return ts.format(dateTimeFormatter);
    }

    public static java.sql.Timestamp dateToTimeStamp(String date) {
        LocalDateTime dateTemp = LocalDateTime.parse(date);
        Instant instant = dateTemp.toInstant(ZoneOffset.UTC);
        return java.sql.Timestamp.from(instant);
    }
    public static java.sql.Timestamp protoTimestampToSqlTimestamp(Timestamp timestamp) {
        return java.sql.Timestamp.from(Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos()));
    }
    public static Timestamp instantToProtoTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }
    public static Instant protoTimestampToInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }
    private DateTimeUtils() {
    }
}
