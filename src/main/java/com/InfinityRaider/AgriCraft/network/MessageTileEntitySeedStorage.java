package com.InfinityRaider.AgriCraft.network;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.storage.SeedStorageSlot;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class MessageTileEntitySeedStorage implements IMessage {
    private int x;
    private int y;
    private int z;
    private int slotId;
    private int amount;
    private int growth;
    private int gain;
    private int strength;

    public MessageTileEntitySeedStorage() {}

    public MessageTileEntitySeedStorage(int x, int y, int z, SeedStorageSlot slot) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.slotId = slot.getSlotId();
        this.amount = slot.count;
        NBTTagCompound tag = slot.getTag();
        this.growth = tag.getInteger(Names.NBT.growth);
        this.gain = tag.getInteger(Names.NBT.gain);
        this.strength = tag.getInteger(Names.NBT.strength);
    }

    private NBTTagCompound getTag() {
        NBTTagCompound tag = new NBTTagCompound();
        SeedHelper.setNBT(tag, (short) growth, (short) gain, (short) strength, true);
        return tag;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();
        this.slotId = buf.readInt();
        this.amount = buf.readInt();
        this.growth = buf.readInt();
        this.gain = buf.readInt();
        this.strength = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.x);
        buf.writeInt(this.y);
        buf.writeInt(this.z);
        buf.writeInt(this.slotId);
        buf.writeInt(this.amount);
        buf.writeInt(this.growth);
        buf.writeInt(this.gain);
        buf.writeInt(this.strength);
    }

    public static class MessageHandler implements IMessageHandler<MessageTileEntitySeedStorage, IMessage> {
        @Override
        public IMessage onMessage(MessageTileEntitySeedStorage message, MessageContext context) {
            TileEntity te = FMLClientHandler.instance().getClient().theWorld.getTileEntity(message.x, message.y, message.z);
            if(te!=null && te instanceof TileEntitySeedStorage) {
                TileEntitySeedStorage storage = (TileEntitySeedStorage) te;
                ItemStack stack = storage.getLockedSeed();
                stack.stackSize = message.amount;
                stack.stackTagCompound = message.getTag();
                storage.setInventorySlotContents(message.slotId, stack);
            }
            return null;
        }
    }
}
