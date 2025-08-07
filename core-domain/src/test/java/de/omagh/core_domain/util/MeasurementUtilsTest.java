package de.omagh.core_domain.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class MeasurementUtilsTest {
    @Test
    public void luxToPPFD_convertsUsingFactor() {
        float result = MeasurementUtils.luxToPPFD(1000f, 0.0185f);
        assertEquals(18.5f, result, 0.0001f);
    }

    @Test
    public void ppfdToDLI_convertsUsingHours() {
        float result = MeasurementUtils.ppfdToDLI(500f, 12);
        assertEquals(21.6f, result, 0.0001f);
    }
}
