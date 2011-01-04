// (c) Copyright 3ES Innovation Inc. 2009.  All rights reserved.

package com._3esi.platform.calculation.timeseries;

public interface Unit<UNIT extends Unit<UNIT>> {

    public String getName();

    public double deltaConversionFactor(UNIT toUnit);

}