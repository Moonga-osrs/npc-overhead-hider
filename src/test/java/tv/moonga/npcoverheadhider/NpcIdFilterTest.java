/*
 * SPDX-License-Identifier: BSD-2-Clause
 * Copyright (c) 2026, Moonga
 */
package tv.moonga.npcoverheadhider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

public class NpcIdFilterTest
{
    @Test public void acceptsCommaSeparatedIdsAndIgnoresMalformedInput()
    {
        assertEquals(new HashSet<>(Arrays.asList(12876, 786, 867, 65)),
            NpcIdFilter.build("12876, 786,867,65,65,,oops,-1,1.5,999999999999", false, false));
        assertTrue(NpcIdFilter.build(null, false, false).isEmpty());
    }

    @Test public void combinesCustomIdsAndIndependentPresets()
    {
        Set<Integer> ferox = NpcIdFilter.build("65,16576", true, false);
        assertEquals(11, ferox.size()); assertTrue(ferox.contains(10389)); assertFalse(ferox.contains(2215));
        Set<Integer> godswars = NpcIdFilter.build("", false, true);
        assertEquals(9, godswars.size()); assertTrue(godswars.contains(11282)); assertFalse(godswars.contains(16576));
        assertEquals(20, NpcIdFilter.build("65", true, true).size());
    }
}
