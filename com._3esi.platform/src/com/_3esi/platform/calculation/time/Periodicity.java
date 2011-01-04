// (c) Copyright 3ES Innovation Inc. 2009.  All rights reserved.

package com._3esi.platform.calculation.time;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.Months;
import org.joda.time.Years;

import com._3esi.platform.desktop.util.Messages;

public enum Periodicity {
    /** assumes a standard day, 24 hours (ignoring daylight savings time and leap seconds) */
    DAILY(1.0, "D", "Day", "Periodicity.DAILY") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        @Override
        public LocalDate addTo(LocalDate date, int periodCount) {
            return date.plusDays(periodCount);
        }

        @Override
        public double periodsBetweenWithMonthlyOffset(LocalDate startDate, LocalDate endDate,
                int offsetMonth) {
            return Days.daysBetween(startDate, endDate).getDays();
        }

        @Override
        public double periodsBetweenProratedMonthly(LocalDate startDate, LocalDate endDate) {
            return periodsBetween(startDate, endDate);
        }

        @Override
        public LocalDate startOfPeriodContainingWithMonthlyOffset(LocalDate date, int offsetMonth) {
            return date;
        }

    },

    /** assumes 7 standard days */
    WEEKLY(7.0, "W", "Week", "Periodicity.WEEKLY") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        @Override
        public LocalDate addTo(LocalDate date, int periodCount) {
            return date.plusDays(7 * periodCount);
        }

        @Override
        public double periodsBetweenWithMonthlyOffset(LocalDate startDate, LocalDate endDate,
                int offsetMonth) {
            return Days.daysBetween(startDate, endDate).getDays() / 7.0;
        }

        @Override
        public double periodsBetweenProratedMonthly(LocalDate startDate, LocalDate endDate) {
            return periodsBetween(startDate, endDate);
        }

        @Override
        public LocalDate startOfPeriodContainingWithMonthlyOffset(LocalDate date, int offsetMonth) {
            return date;
        }

    },

    /** assumes a "mean" month, 30.4375 standard days */
    MONTHLY(30.4375, "M", "Month", "Periodicity.MONTHLY") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        @Override
        public LocalDate addTo(LocalDate date, int periodCount) {
            return date.plusDays(1).plusMonths(periodCount).minusDays(1);
        }

        @Override
        public double periodsBetweenWithMonthlyOffset(LocalDate startDate, LocalDate endDate,
                int offsetMonth) {
            int startDay = startDate.getDayOfMonth();
            int endDay = endDate.getDayOfMonth();
            LocalDate start = 1 == startDay ? startDate : startDate.withDayOfMonth(1);
            LocalDate end = 1 == endDay ? endDate : endDate.withDayOfMonth(1);
            double months = Months.monthsBetween(start, end).getMonths();
            if (1 != startDay)
                months -= (startDay - 1.0) / Days.daysBetween(start, start.plusMonths(1)).getDays();
            if (1 != endDay)
                months += (endDay - 1.0) / Days.daysBetween(end, end.plusMonths(1)).getDays();
            return months;
        }

        @Override
        public double periodsBetweenProratedMonthly(LocalDate startDate, LocalDate endDate) {
            return periodsBetween(startDate, endDate);
        }

        @Override
        public LocalDate startOfPeriodContainingWithMonthlyOffset(LocalDate date, int offsetMonth) {
            return date.withDayOfMonth(1);
        }
    },

    /** assumes a "mean" quarter, 91.3125 standard days */
    QUARTERLY(91.3125, "Q", "Quarter", "Periodicity.QUARTERLY") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        @Override
        public LocalDate addTo(LocalDate date, int periodCount) {
            return date.plusDays(1).plusMonths(3 * periodCount).minusDays(1);
        }

        @Override
        public double periodsBetweenWithMonthlyOffset(LocalDate startDate, LocalDate endDate,
                int offsetMonth) {
            LocalDate start = startOfPeriodContainingWithMonthlyOffset(startDate, offsetMonth);
            LocalDate end = startOfPeriodContainingWithMonthlyOffset(endDate, offsetMonth);
            double quarters = Months.monthsBetween(start, end).getMonths() / 3;
            if (!startDate.isEqual(start))
                quarters -= 1.0 * Days.daysBetween(start, startDate).getDays()
                        / Days.daysBetween(start, start.plusMonths(3)).getDays();
            if (!endDate.isEqual(end))
                quarters += 1.0 * Days.daysBetween(end, endDate).getDays()
                        / Days.daysBetween(end, end.plusMonths(3)).getDays();
            return quarters;
        }

        @Override
        public double periodsBetweenProratedMonthly(LocalDate startDate, LocalDate endDate) {
            return MONTHLY.periodsBetween(startDate, endDate) / 3.0;
        }

        @Override
        public LocalDate startOfPeriodContainingWithMonthlyOffset(LocalDate date, int offsetMonth) {
            final int orginalMonth = date.getMonthOfYear();
            final int idx = (12 + orginalMonth - offsetMonth) % 12;
            final int newMonth = (idx / 3 * 3 + offsetMonth) % 12;
            final int diffYear = newMonth > orginalMonth ? -1 : 0;
            return date.withDayOfMonth(1).withMonthOfYear(newMonth).plusYears(diffYear);
        }

    },

    /** assumes a "mean" year, 365.25 standard days */
    YEARLY(365.25, "Y", "Year", "Periodicity.YEARLY") { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        @Override
        public LocalDate addTo(LocalDate date, int periodCount) {
            return date.plusDays(1).plusYears(periodCount).minusDays(1);
        }

        @Override
        public double periodsBetweenWithMonthlyOffset(LocalDate startDate, LocalDate endDate,
                int offsetMonth) {
            LocalDate start = startDate.withDayOfYear(1);
            LocalDate end = endDate.withDayOfYear(1);
            double years = Years.yearsBetween(start, end).getYears();
            if (!startDate.isEqual(start))
                years -= 1.0 * Days.daysBetween(start, startDate).getDays()
                        / Days.daysBetween(start, start.plusYears(1)).getDays();
            if (!endDate.isEqual(end))
                years += 1.0 * Days.daysBetween(end, endDate).getDays()
                        / Days.daysBetween(end, end.plusYears(1)).getDays();
            return years;
        }

        @Override
        public double periodsBetweenProratedMonthly(LocalDate startDate, LocalDate endDate) {
            return MONTHLY.periodsBetween(startDate, endDate) / 12.0;
        }

        @Override
        public LocalDate startOfPeriodContainingWithMonthlyOffset(LocalDate date, int offsetMonth) {
            return date.minusMonths(offsetMonth - 1).withDayOfYear(1).plusMonths(offsetMonth - 1);
        }

    };

    private final double _lengthInStandardDays;
    private final String _code;
    private final String _name;
    private final String _labelMessageCode;

    private Periodicity(double lengthInStandardDays, String code, String name,
            String labelMessageCode) {
        _lengthInStandardDays = lengthInStandardDays;
        _code = code;
        _name = name;
        _labelMessageCode = labelMessageCode;
    }

    public double getLengthInStandardDays() {
        return _lengthInStandardDays;
    }

    public String getLabel() {
        return Messages.getString(_labelMessageCode);
    }

    public String getCode() {
        return _code;
    }

    public abstract LocalDate addTo(LocalDate date, int periodCount);

    public abstract double periodsBetweenWithMonthlyOffset(LocalDate startDate, LocalDate endDate,
            int offsetMonth);

    public abstract double periodsBetweenProratedMonthly(LocalDate startDate, LocalDate endDate);

    public abstract LocalDate startOfPeriodContainingWithMonthlyOffset(LocalDate date,
            int offsetMonth);

    public double periodsBetween(LocalDate startDate, LocalDate endDate) {
        return periodsBetweenWithMonthlyOffset(startDate, endDate, 1);
    }

    public LocalDate startOfPeriodContaining(LocalDate date) {
        return startOfPeriodContainingWithMonthlyOffset(date, 1);
    }

    public static Periodicity forCode(String code) {
        for (Periodicity periodicity : values()) {
            if (periodicity._code.equals(code))
                return periodicity;
        }
        throw new IllegalArgumentException();
    }

    public static Periodicity forName(String name) {
        for (Periodicity periodicity : values()) {
            if (periodicity._name.equals(name))
                return periodicity;
        }
        throw new IllegalArgumentException(name);
    }

}
