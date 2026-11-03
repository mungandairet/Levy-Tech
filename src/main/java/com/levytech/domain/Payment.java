package com.levytech.domain;
import java.time.LocalDate;

public record Payment(int id, int  traderId, long amountNgwee, LocalDate paidOn, String reference) {
}