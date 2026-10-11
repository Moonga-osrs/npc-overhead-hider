/*
 * SPDX-License-Identifier: BSD-2-Clause
 * Copyright (c) 2026, Moonga
 */
package tv.moonga.npcoverheadhider;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.Collections;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Player;
import org.junit.Test;
import static org.junit.Assert.*;

public class NpcSpeechDrawTest
{
    private NpcOverheadHiderPlugin plugin() throws Exception
    {
        NpcOverheadHiderPlugin p = new NpcOverheadHiderPlugin();
        Field f = NpcOverheadHiderPlugin.class.getDeclaredField("hiddenIds");
        f.setAccessible(true);
        f.set(p, Collections.singleton(65));
        return p;
    }

    private NPC npc(int id, Integer transformedId, String[] speech)
    {
        NPCComposition composition = transformedId == null ? null : (NPCComposition) Proxy.newProxyInstance(
            NPCComposition.class.getClassLoader(), new Class<?>[]{NPCComposition.class},
            (proxy, method, args) -> { if (method.getName().equals("getId")) return transformedId;
                throw new AssertionError("Unexpected composition access: " + method.getName()); });
        return (NPC) Proxy.newProxyInstance(NPC.class.getClassLoader(), new Class<?>[]{NPC.class},
            (proxy, method, args) -> {
                switch (method.getName())
                {
                    case "getId": return id;
                    case "getTransformedComposition": return composition;
                    case "getOverheadText": return speech[0];
                    case "setOverheadText": speech[0] = (String) args[0]; return null;
                    default: throw new AssertionError("Other NPC state touched: " + method.getName());
                }
            });
    }

    @Test public void clearsOnlySelectedSpeechAndAllowsAllUi() throws Exception
    {
        String[] speech = {"Hello"};
        NPC npc = npc(65, null, speech);
        NpcOverheadHiderPlugin p = plugin();
        assertTrue(p.shouldDraw(npc, true)); assertNull(speech[0]);
        assertTrue(p.shouldDraw(npc, true));
        speech[0] = "New speech";
        assertTrue(p.shouldDraw(npc, true)); assertNull(speech[0]);
    }
    @Test public void preservesUnselectedNpcsIncludingMissingTransforms() throws Exception
    {
        String[] speech = {"Hello"};
        assertTrue(plugin().shouldDraw(npc(99, null, speech), true));
        assertEquals("Hello", speech[0]);
    }
    @Test public void matchesTransformedIds() throws Exception
    {
        String[] speech = {"Hello"};
        assertTrue(plugin().shouldDraw(npc(99, 65, speech), true)); assertNull(speech[0]);
    }
    @Test public void leavesThreeDimensionalDrawAndPlayersUntouched() throws Exception
    {
        String[] speech = {"Hello"};
        assertTrue(plugin().shouldDraw(npc(65, null, speech), false)); assertEquals("Hello", speech[0]);
        Player player = (Player) Proxy.newProxyInstance(Player.class.getClassLoader(), new Class<?>[]{Player.class},
            (proxy, method, args) -> { throw new AssertionError("Player state touched"); });
        assertTrue(plugin().shouldDraw(player, true));
    }
    @Test public void registersReloadsAndUnregistersCallback() throws Exception
    {
        NpcOverheadHiderPlugin plugin = new NpcOverheadHiderPlugin();
        net.runelite.client.callback.RenderCallbackManager callbacks =
            new net.runelite.client.callback.RenderCallbackManager();
        String[] custom = {"65"};
        NpcOverheadHiderConfig config = (NpcOverheadHiderConfig) Proxy.newProxyInstance(
            NpcOverheadHiderConfig.class.getClassLoader(), new Class<?>[]{NpcOverheadHiderConfig.class},
            (proxy, method, args) -> {
                if (method.getName().equals("customIds")) return custom[0];
                return false;
            });
        Field managerField = NpcOverheadHiderPlugin.class.getDeclaredField("renderCallbacks");
        managerField.setAccessible(true); managerField.set(plugin, callbacks);
        Field configField = NpcOverheadHiderPlugin.class.getDeclaredField("config");
        configField.setAccessible(true); configField.set(plugin, config);
        String[] speech = {"Before startup"};
        NPC npc = npc(65, null, speech);
        assertTrue(callbacks.addEntity(npc, true)); assertEquals("Before startup", speech[0]);
        plugin.startUp();
        assertTrue(callbacks.addEntity(npc, true)); assertNull(speech[0]);
        custom[0] = "";
        net.runelite.client.events.ConfigChanged change = new net.runelite.client.events.ConfigChanged();
        change.setGroup(NpcOverheadHiderConfig.GROUP);
        plugin.onConfigChanged(change);
        speech[0] = "No filter";
        assertTrue(callbacks.addEntity(npc, true)); assertEquals("No filter", speech[0]);
        custom[0] = "65"; plugin.onConfigChanged(change);
        plugin.shutDown();
        speech[0] = "After shutdown";
        assertTrue(callbacks.addEntity(npc, true)); assertEquals("After shutdown", speech[0]);
    }
}
