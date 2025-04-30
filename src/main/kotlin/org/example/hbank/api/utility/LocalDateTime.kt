package org.example.hbank.api.utility

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

fun Instant.toLocalDateTome(): LocalDateTime =
    LocalDateTime
        .ofInstant(
            this,
            ZoneId.of("Africa/Algiers")
        )

fun LocalDateTime.toInstant(): Instant =
    atZone(ZoneId.of("Africa/Algiers"))
        .toInstant()

fun Instant.startOfTheDay(): Instant =
    toLocalDateTome()
        .toLocalDate()
        .atTime(LocalTime.MIN)
        .toInstant()

fun Instant.endOfTheDay(): Instant =
    toLocalDateTome()
        .toLocalDate()
        .atTime(LocalTime.MAX)
        .toInstant()

fun Instant.startOfTheMonth(): Instant =
    toLocalDateTome()
        .toLocalDate()
        .with(TemporalAdjusters.firstDayOfMonth())
        .atTime(LocalTime.MIN)
        .toInstant()

fun Instant.endOfTheMonth(): Instant =
    toLocalDateTome()
        .toLocalDate()
        .with(TemporalAdjusters.lastDayOfMonth())
        .atTime(LocalTime.MAX)
        .toInstant()

fun Instant.startOfTheYear(): Instant =
    toLocalDateTome()
        .toLocalDate()
        .with(TemporalAdjusters.firstDayOfYear())
        .atTime(LocalTime.MIN)
        .toInstant()


fun Instant.endOfTheYear(): Instant =
    toLocalDateTome()
        .toLocalDate()
        .with(TemporalAdjusters.lastDayOfYear())
        .atTime(LocalTime.MAX)
        .toInstant()

