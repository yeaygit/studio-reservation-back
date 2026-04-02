package com.toy.project.studio.setting.dto.response;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.MonthDay;

import com.toy.project.studio.setting.entity.ClosedDay;
import com.toy.project.studio.setting.enumeration.ClosedDayType;

public record ClosedDayResponse(
        Long id,
        ClosedDayType type,
        LocalDate closedDate,
        LocalDate specificDate,
        Integer annualMonth,
        Integer annualDay
) {

    public static ClosedDayResponse from(ClosedDay closedDay) {
        LocalDate closedDate = closedDay.getType() == ClosedDayType.SPECIFIC
                ? closedDay.getSpecificDate()
                : null;

        return new ClosedDayResponse(
                closedDay.getId(),
                closedDay.getType(),
                closedDate,
                closedDay.getSpecificDate(),
                closedDay.getAnnualMonth(),
                closedDay.getAnnualDay()
        );
    }

    public static ClosedDayResponse from(ClosedDay closedDay, int year) {
        return new ClosedDayResponse(
                closedDay.getId(),
                closedDay.getType(),
                resolveClosedDate(closedDay, year),
                closedDay.getSpecificDate(),
                closedDay.getAnnualMonth(),
                closedDay.getAnnualDay()
        );
    }

    private static LocalDate resolveClosedDate(ClosedDay closedDay, int year) {
        if (closedDay.getType() == ClosedDayType.SPECIFIC) {
            return closedDay.getSpecificDate();
        }
        if (closedDay.getAnnualMonth() == null || closedDay.getAnnualDay() == null) {
            return null;
        }
        try {
            return MonthDay.of(closedDay.getAnnualMonth(), closedDay.getAnnualDay()).atYear(year);
        } catch (DateTimeException exception) {
            return null;
        }
    }
}
