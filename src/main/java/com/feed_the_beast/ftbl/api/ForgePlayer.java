package com.feed_the_beast.ftbl.api;

import com.feed_the_beast.ftbl.api.events.ForgePlayerEvent;
import com.mojang.authlib.GameProfile;
import latmod.lib.LMUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by LatvianModder on 09.02.2016.
 */
public abstract class ForgePlayer implements Comparable<ForgePlayer>, ICapabilityProvider
{
    public final Map<EntityEquipmentSlot, ItemStack> lastArmor;
    CapabilityDispatcher capabilities;
    private int teamID;
    private GameProfile gameProfile;

    ForgePlayer(GameProfile p)
    {
        setProfile(p);
        lastArmor = new HashMap<>();

        ForgePlayerEvent.AttachCapabilities event = new ForgePlayerEvent.AttachCapabilities(this);
        MinecraftForge.EVENT_BUS.post(event);
        capabilities = !event.getCapabilities().isEmpty() ? new CapabilityDispatcher(event.getCapabilities(), null) : null;
    }

    public final boolean hasTeam()
    {
        int id = getTeamID();
        return id > 0 && getWorld().teams.containsKey(id);
    }

    @Nullable
    public final ForgeTeam getTeam()
    {
        int id = getTeamID();
        return id > 0 ? getWorld().teams.get(getTeamID()) : null;
    }

    public int getTeamID()
    {
        return teamID;
    }

    public void setTeamID(int id)
    {
        teamID = Math.max(0, id);
    }

    @Override
    public final boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capabilities != null && capabilities.hasCapability(capability, facing);
    }

    @Override
    public final <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capabilities == null ? null : capabilities.getCapability(capability, facing);
    }

    public abstract boolean isOnline();

    public abstract EntityPlayer getPlayer();

    public abstract ForgePlayerMP toMP();

    @SideOnly(Side.CLIENT)
    public abstract ForgePlayerSP toSP();

    public abstract ForgeWorld getWorld();

    public final GameProfile getProfile()
    {
        return gameProfile;
    }

    public final void setProfile(GameProfile p)
    {
        if(p != null)
        {
            gameProfile = new GameProfile(p.getId(), p.getName());
        }
    }

    public final String getStringUUID()
    {
        return LMUtils.fromUUID(gameProfile.getId());
    }

    @Override
    public final int compareTo(ForgePlayer o)
    {
        return getProfile().getName().compareToIgnoreCase(o.getProfile().getName());
    }

    @Override
    public final String toString()
    {
        return gameProfile.getName();
    }

    @Override
    public final int hashCode()
    {
        return gameProfile.getId().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if(o == null)
        {
            return false;
        }
        else if(o == this)
        {
            return true;
        }
        else if(o instanceof UUID)
        {
            return gameProfile.getId().equals(o);
        }
        else if(o instanceof ForgePlayer)
        {
            return equalsPlayer((ForgePlayer) o);
        }
        return equalsPlayer(getWorld().getPlayer(o));
    }

    public boolean equalsPlayer(ForgePlayer p)
    {
        return p != null && (p == this || gameProfile.getId().equals(p.gameProfile.getId()));
    }

    public boolean isMCPlayer()
    {
        return false;
    }

    public void updateArmor()
    {
        if(getWorld().getSide().isServer() && isOnline())
        {
            lastArmor.clear();
            EntityPlayer ep = getPlayer();

            for(EntityEquipmentSlot e : EntityEquipmentSlot.values())
            {
                ItemStack is = ep.getItemStackFromSlot(e);

                if(is != null)
                {
                    lastArmor.put(e, is.copy());
                }
            }
        }
    }

    public void onLoggedIn(boolean firstLogin)
    {
        updateArmor();
    }

    public void onLoggedOut()
    {
        updateArmor();
    }

    public void onDeath()
    {
        updateArmor();
    }

    public void sendUpdate()
    {
    }

    public void sendInfoUpdate(ForgePlayer p)
    {
    }
}