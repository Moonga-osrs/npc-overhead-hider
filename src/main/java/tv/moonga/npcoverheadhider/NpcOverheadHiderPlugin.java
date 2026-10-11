/*
 * SPDX-License-Identifier: BSD-2-Clause
 * Copyright (c) 2026, Moonga
 */
package tv.moonga.npcoverheadhider;

import com.google.inject.Provides;
import java.util.Collections;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.NPCComposition;
import net.runelite.api.Renderable;
import net.runelite.client.callback.RenderCallback;
import net.runelite.client.callback.RenderCallbackManager;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(name = "NPC Overhead Hider",
    description = "Hide NPC speech by ID, with Ferox Enclave and Godswars presets",
    tags = {"npc", "overhead", "text", "hide", "ferox", "godwars"})
public class NpcOverheadHiderPlugin extends Plugin
{
    @Inject private RenderCallbackManager renderCallbacks;
    @Inject private NpcOverheadHiderConfig config;
    private volatile Set<Integer> hiddenIds = Collections.emptySet();
    private final RenderCallback drawListener = new RenderCallback()
    {
        @Override
        public boolean addEntity(Renderable renderable, boolean drawingUI)
        {
            return shouldDraw(renderable, drawingUI);
        }
    };

    @Override
    protected void startUp()
    {
        reload();
        renderCallbacks.register(drawListener);
    }

    @Override
    protected void shutDown()
    {
        renderCallbacks.unregister(drawListener);
        hiddenIds = Collections.emptySet();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (NpcOverheadHiderConfig.GROUP.equals(event.getGroup())) reload();
    }

    private void reload()
    {
        hiddenIds = NpcIdFilter.build(config.customIds(), config.ferox(), config.godswars());
    }

    boolean shouldDraw(Renderable renderable, boolean drawingUI)
    {
        // Entity Hider uses this hook to skip all actor UI. Keep drawing the UI:
        // clear only speech immediately before it is drawn, preserving bars/icons.
        if (!drawingUI || !(renderable instanceof NPC)) return true;
        NPC npc = (NPC) renderable;
        Set<Integer> ids = hiddenIds;
        if (ids.isEmpty()) return true;
        boolean matches = ids.contains(npc.getId());
        if (!matches)
        {
            NPCComposition transformed = npc.getTransformedComposition();
            matches = transformed != null && ids.contains(transformed.getId());
        }
        if (matches && npc.getOverheadText() != null) npc.setOverheadText(null);
        // No OverheadTextChanged subscriber: clearing text cannot recurse here.
        return true;
    }

    @Provides
    NpcOverheadHiderConfig provideConfig(ConfigManager manager)
    {
        return manager.getConfig(NpcOverheadHiderConfig.class);
    }
}
