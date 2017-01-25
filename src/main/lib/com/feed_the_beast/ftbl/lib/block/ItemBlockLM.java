package com.feed_the_beast.ftbl.lib.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

public class ItemBlockLM extends ItemBlock
{
    public ItemBlockLM(Block b, boolean hasSubtypes)
    {
        super(b);

        if(hasSubtypes)
        {
            setHasSubtypes(true);
            setMaxDamage(0);
        }
    }

    @Override
    public int getMetadata(int metadata)
    {
        return metadata;
    }
}