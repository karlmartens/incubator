// (c) Copyright 3ES Innovation Inc. 2009.  All rights reserved.

package com._3esi.platform.calculation.timeseries;

import org.joda.time.LocalDate;

public interface RateTimeSeries<UNIT extends Unit<UNIT>> {

    public double getRate(LocalDate date);

    public RateUnit<UNIT> getRateUnit();

    public double getCumulative(LocalDate date);

    public UNIT getCumulativeUnit();

    public LocalDate getStartDate();

    public LocalDate getEndDate();

    public double getEndRate();
}
