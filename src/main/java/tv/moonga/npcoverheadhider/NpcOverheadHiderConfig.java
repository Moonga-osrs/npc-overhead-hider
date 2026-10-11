/*
 * SPDX-License-Identifier: BSD-2-Clause
 * Copyright (c) 2026, Moonga
 */
package tv.moonga.npcoverheadhider;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(NpcOverheadHiderConfig.GROUP)
public interface NpcOverheadHiderConfig extends Config
{
    String GROUP = "npcoverheadhider";

    @ConfigItem(keyName = "customIds", name = "Custom NPC IDs", position = 0,
        description = "Comma-separated NPC IDs, for example: 12876,786,867,65. Spaces are allowed. Invalid entries are ignored.")
    default String customIds() { return ""; }

    @ConfigItem(keyName = "ferox", name = "Ferox Enclave", position = 1,
        description = "Hide speech for IDs: 16576,10382,10389,10383,10390,10379,10380,10381,7316,8721")
    default boolean ferox() { return false; }

    @ConfigItem(keyName = "godswars", name = "Godswars", position = 2,
        description = "Hide speech for IDs: 2215,2205,3162,3129,11278,11279,11280,11281,11282")
    default boolean godswars() { return false; }
}
