package ua.kpi.tzyapz.personal;

import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Розрахунок відпусток продукту «Персонал+»: нарахування днів, перенесення залишку,
 * тривалість заявки в робочих днях і перевірка можливості подати заявку
 * (вимоги BL-10 … BL-40 специфікації).
 */
public final class LeaveCalculator {

    /** Днів відпустки за кожен повний відпрацьований місяць (BL-10). */
    public static final int ACCRUAL_PER_MONTH = 2;
    /** Найбільша кількість нарахованих днів за рік (BL-10). */
    public static final int MAX_ACCRUAL_PER_YEAR = 24;
    /** Найбільша кількість днів, що переноситься на наступний рік (BL-20). */
    public static final int MAX_CARRY_OVER = 5;

    /**
     * Нараховані дні відпустки за відпрацьовані місяці (BL-10).
     *
     * @param monthsWorked кількість повних відпрацьованих місяців, не менше 0
     * @return нараховані дні (не більше {@link #MAX_ACCRUAL_PER_YEAR})
     * @throws IllegalArgumentException якщо monthsWorked від'ємне
     */
    public int accrued(int monthsWorked) {
        if (monthsWorked < 0) {
            throw new IllegalArgumentException("monthsWorked має бути >= 0");
        }
        return monthsWorked * ACCRUAL_PER_MONTH;
    }

    /**
     * Дні, що переносяться на наступний рік (BL-20).
     *
     * @param unused невикористані дні поточного року, не менше 0
     * @return перенесені дні (не більше {@link #MAX_CARRY_OVER})
     * @throws IllegalArgumentException якщо unused від'ємне
     */
    public int carryOver(int unused) {
        if (unused < 0) {
            throw new IllegalArgumentException("unused має бути >= 0");
        }
        return Math.min(unused, MAX_CARRY_OVER);
    }

    /**
     * Тривалість відпустки в робочих днях (BL-30): суботи й неділі не враховуються,
     * обидві межі включно.
     *
     * @param start перший день відпустки
     * @param end   останній день відпустки (не раніше за start)
     * @return кількість робочих днів у проміжку
     * @throws IllegalArgumentException якщо дата відсутня або start пізніша за end
     */
    public int durationDays(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Обидві дати обов'язкові");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Дата початку пізніша за дату завершення");
        }
        int days = 0;
        for (LocalDate d = start; d.isBefore(end); d = d.plusDays(1)) {
            if (isWorkingDay(d)) {
                days++;
            }
        }
        return days;
    }

    /**
     * Чи можна подати заявку (BL-40): тривалість не менша за 1 і не більша за баланс.
     *
     * @param balance  поточний баланс днів
     * @param duration тривалість заявки в робочих днях
     * @return true, якщо заявку можна подати
     */
    public boolean canRequest(int balance, int duration) {
        return duration >= 1 && duration <= balance;
    }

    private static boolean isWorkingDay(LocalDate d) {
        DayOfWeek w = d.getDayOfWeek();
        return w != DayOfWeek.SATURDAY && w != DayOfWeek.SUNDAY;
    }
}
