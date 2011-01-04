// (c) Copyright 3ES Innovation Inc. 2009.  All rights reserved.

package com._3esi.platform.calculation.timeseries;

import com._3esi.platform.calculation.time.Periodicity;

public final class RateUnit<UNIT extends Unit<UNIT>> {
    private final UNIT _unit;
    private final Periodicity _periodicity;

    public RateUnit(UNIT unit, Periodicity periodicity) {
        _unit = unit;
        _periodicity = periodicity;
    }

    public UNIT getNumerator() {
        return _unit;
    }

    public Periodicity getDenominator() {
        return _periodicity;
    }

    public double deltaConversionFactor(RateUnit<UNIT> toRateUnit) {
        UNIT toUnit = toRateUnit.getNumerator();
        double toDays = toRateUnit.getDenominator().getLengthInStandardDays();
        double fromDays = getDenominator().getLengthInStandardDays();
        double factor = getNumerator().deltaConversionFactor(toUnit) * toDays / fromDays;
        return factor;
    }

    @Override
    public String toString() {
        return _unit.toString() + " " + _periodicity.name(); //$NON-NLS-1$
    }
}
