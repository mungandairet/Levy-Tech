package com.levytech.domain;
import java.time.LocalDate;

public record Payment(int traderId, long amountNgwee, LocalDate date, String cycle) {
}