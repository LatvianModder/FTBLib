package ftb.lib.mod;

import ftb.lib.api.*;
import latmod.lib.FastList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class FTBUIntegration // FTBLIntegration
{
	public static FTBUIntegration instance = null;
	
	public abstract void onReloaded(EventFTBReload e);
	public abstract void onModeSet(EventFTBModeSet e);
	public abstract void onFTBWorldServer(EventFTBWorldServer e);
	public abstract void onFTBWorldClient(EventFTBWorldClient e);
	public abstract void onServerTick(World w);
	public abstract void onPlayerJoined(EntityPlayer player);
	public abstract int getPlayerID(Object player);
	public abstract FastList<String> getPlayerNames(boolean online);
	public abstract String[] getOfflinePlayerNames();
}