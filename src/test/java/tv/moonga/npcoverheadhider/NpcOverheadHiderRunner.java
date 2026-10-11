/*
 * SPDX-License-Identifier: BSD-2-Clause
 * Copyright (c) 2026, Moonga
 */
package tv.moonga.npcoverheadhider;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class NpcOverheadHiderRunner
{
    @SuppressWarnings("unchecked") // RuneLite loadBuiltin uses generic varargs.
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(NpcOverheadHiderPlugin.class);
        RuneLite.main(args);
    }
}
