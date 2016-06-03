package com.feed_the_beast.ftbl.api.notification;

import com.feed_the_beast.ftbl.api.ForgeWorldSP;
import com.feed_the_beast.ftbl.api.MouseButton;
import com.feed_the_beast.ftbl.api.client.FTBLibClient;
import com.feed_the_beast.ftbl.api.client.gui.GuiScreenRegistry;
import com.feed_the_beast.ftbl.api.client.gui.PlayerAction;
import com.feed_the_beast.ftbl.api.client.gui.PlayerActionRegistry;
import com.google.gson.JsonElement;
import latmod.lib.LMUtils;
import latmod.lib.util.FinalIDObject;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import java.io.File;
import java.net.URI;

public abstract class ClickActionType extends FinalIDObject
{
    public static final ClickActionType ACTION = ClickActionRegistry.register(new ClickActionType("action")
    {
        @Override
        public void onClicked(JsonElement data, MouseButton button)
        {
            PlayerAction a = PlayerActionRegistry.get(data.getAsString());
            if(a != null && a.type.isSelf())
            {
                a.onClicked(ForgeWorldSP.inst.clientPlayer, ForgeWorldSP.inst.clientPlayer);
            }
        }
    });

    public static final ClickActionType CMD = ClickActionRegistry.register(new ClickActionType("cmd")
    {
        @Override
        public void onClicked(JsonElement data, MouseButton button)
        {
            FTBLibClient.execClientCommand("/" + data.getAsString());
        }
    });

    // Static //
    public static final ClickActionType SHOW_CMD = ClickActionRegistry.register(new ClickActionType("show_cmd")
    {
        @Override
        public void onClicked(JsonElement data, MouseButton button)
        {
            FTBLibClient.mc().displayGuiScreen(new GuiChat(data.getAsString()));
        }
    });

    public static final ClickActionType URL = ClickActionRegistry.register(new ClickActionType("url")
    {
        @Override
        public void onClicked(JsonElement data, MouseButton button)
        {
            try
            {
                LMUtils.openURI(new URI(data.getAsString()));
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    });

    public static final ClickActionType FILE = ClickActionRegistry.register(new ClickActionType("file")
    {
        @Override
        public void onClicked(JsonElement data, MouseButton button)
        {
            try
            {
                LMUtils.openURI(new File(data.getAsString()).toURI());
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    });

    public static final ClickActionType GUI = ClickActionRegistry.register(new ClickActionType("gui")
    {
        @Override
        public void onClicked(JsonElement data, MouseButton button)
        {
            GuiScreen gui = GuiScreenRegistry.openGui(FTBLibClient.mc().thePlayer, new ResourceLocation(data.getAsString()));

            if(gui != null)
            {
                FTBLibClient.mc().displayGuiScreen(gui);
            }
        }
    });

    public ClickActionType(String id)
    {
        super(id);
    }

    public static void init()
    {
    }

    public abstract void onClicked(JsonElement data, MouseButton button);
}