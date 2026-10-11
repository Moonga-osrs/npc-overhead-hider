/*
 * SPDX-License-Identifier: BSD-2-Clause
 * Copyright (c) 2026, Moonga
 */
package tv.moonga.npcoverheadhider;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

final class NpcIdFilter
{
    static final String FEROX = "16576,10382,10389,10383,10390,10379,10380,10381,7316,8721";
    static final String GODSWARS = "2215,2205,3162,3129,11278,11279,11280,11281,11282";

    static Set<Integer> build(String custom, boolean ferox, boolean godswars)
    {
        Set<Integer> ids = new HashSet<>();
        add(ids, custom);
        if (ferox) add(ids, FEROX);
        if (godswars) add(ids, GODSWARS);
        return Collections.unmodifiableSet(ids);
    }

    private static void add(Set<Integer> ids, String input)
    {
        if (input == null) return;
        for (String part : input.split(","))
        {
            String value = part.trim();
            if (!value.matches("[0-9]+")) continue;
            try { ids.add(Integer.parseInt(value)); }
            catch (NumberFormatException ignored) { /* Ignore overflowing IDs. */ }
        }
    }
}
