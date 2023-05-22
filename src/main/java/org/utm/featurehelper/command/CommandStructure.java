package org.utm.featurehelper.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.*;
import org.utm.featurehelper.network.MessageBoundingBox;
import org.utm.featurehelper.network.NetworkManager;
import org.utm.featurehelper.structure.ComponentFactory;
import org.utm.featurehelper.structure.StartFactory;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;

public class CommandStructure extends CommandBase {

    private List<StructureBoundingBox> bbList = new ArrayList<StructureBoundingBox>();
    private StructureBoundingBox lastBB;

    private Iterator<StructureComponent> it;
    private StructureStart start;

    @Override
    public String getCommandName() {
        return "structure";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.structure.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0)
            throw new WrongUsageException("commands.structure.usage");

        World world = sender.getEntityWorld();
        Random rand = new Random();

        if (args[0].equals("continue")) {

            if (this.start != null) {
                StructureBoundingBox bb = this.start.getBoundingBox();
                StructureBoundingBox newBB = new StructureBoundingBox(bb.minX - 1, bb.minZ - 1, bb.maxX + 1, bb.maxZ + 1);
                this.setLastBoundingBox(bb);
                if (this.it != null && this.it.hasNext()) {
                    StructureComponent component = this.it.next();
                    if (component.getBoundingBox().intersectsWith(newBB)) {
                        component.addComponentParts(world, rand, newBB);
                        this.addBoundingBox(component.getBoundingBox());
                    }
                    func_152373_a(sender, this, "commands.structure.continue.success");
                } else {
                    if (this.canAddBoundingBox())
                        this.addBoundingBox(null);
                    func_152373_a(sender, this, "commands.structure.continue.complete");
                }
            }

        } else if (args[0].equals("start")) {

            if (args.length < 5)
                throw new WrongUsageException("commands.structure.start.usage");

            int x = sender.getPlayerCoordinates().posX;
            int y = sender.getPlayerCoordinates().posY;
            int z = sender.getPlayerCoordinates().posZ;
            x = MathHelper.floor_double(func_110666_a(sender, x, args[1]));
            y = MathHelper.floor_double(func_110666_a(sender, y, args[2]));
            z = MathHelper.floor_double(func_110666_a(sender, z, args[3]));
            
            if (this.getStartNameMap().get(args[4]) == null)
                throw new WrongUsageException("commands.structure.start.invalidName");

            this.start = StartFactory.getStart(args[4], world, x >> 4, z >> 4, rand);

            boolean debug = false;
            if (args.length >= 6) {
                debug = parseBoolean(sender, args[5]);
                if (args.length >= 7) {
                    NBTTagCompound compound = this.start.func_143021_a(x >> 4, z >> 4);
                    String tagStr = func_147178_a(sender, args, 6).getUnformattedText();
                    try {
                        NBTBase nbtbase = JsonToNBT.func_150315_a(tagStr);
                        if (!(nbtbase instanceof NBTTagCompound)) {
                            throw new CommandException("commands.structure.start.invalidTag");
                        }
                        for (Object obj : ((NBTTagCompound) nbtbase).func_150296_c()) {
                            String key = (String) obj;
                            NBTBase value = ((NBTTagCompound) nbtbase).getTag(key);
                            compound.setTag(key, value);
                        }
                    } catch (NBTException e) {
                        throw new CommandException("commands.structure.start.tagError", e.getMessage());
                    }
                    this.start.func_143020_a(world, compound);
                }
            }

            StructureBoundingBox bb = this.start.getBoundingBox();
            StructureBoundingBox newBB = new StructureBoundingBox(bb.minX - 1, bb.minZ - 1, bb.maxX + 1, bb.maxZ + 1);
            this.clearBoundingBox();
            this.setLastBoundingBox(bb);
            if (debug) {
                this.it = this.start.getComponents().iterator();
                if (this.it.hasNext()) {
                    StructureComponent component = this.it.next();
                    if (component.getBoundingBox().intersectsWith(newBB)) {
                        component.addComponentParts(world, rand, newBB);
                        this.addBoundingBox(component.getBoundingBox());
                    }
                }
            } else {
                this.start.generateStructure(world, rand, newBB);
            }
            func_152373_a(sender, this, "commands.structure.start.success", x, y, z);

        } else if (args[0].equals("component")) {

            if (args.length < 5)
                throw new WrongUsageException("commands.structure.component.usage");

            int x = sender.getPlayerCoordinates().posX;
            int y = sender.getPlayerCoordinates().posY;
            int z = sender.getPlayerCoordinates().posZ;
            x = MathHelper.floor_double(func_110666_a(sender, x, args[1]));
            y = MathHelper.floor_double(func_110666_a(sender, y, args[2]));
            z = MathHelper.floor_double(func_110666_a(sender, z, args[3]));

            if (this.getComponentNameMap().get(args[4]) == null)
                throw new WrongUsageException("commands.structure.component.invalidName");

            StructureComponent component = ComponentFactory.getComponent(args[4], world, x, y, z, rand);
            if (component != null) {

                if (args.length >= 6) {
                    NBTTagCompound compound = component.func_143010_b();
                    String tagStr = func_147178_a(sender, args, 5).getUnformattedText();
                    try {
                        NBTBase nbtbase = JsonToNBT.func_150315_a(tagStr);
                        if (!(nbtbase instanceof NBTTagCompound)) {
                            throw new CommandException("commands.structure.component.invalidTag");
                        }
                        for (Object obj : ((NBTTagCompound) nbtbase).func_150296_c()) {
                            String key = (String) obj;
                            NBTBase value = ((NBTTagCompound) nbtbase).getTag(key);
                            compound.setTag(key, value);
                            /*if (key.equals("~BB")) {
                                int[] arr = compound.getIntArray("BB");
                                int[] offset = ((NBTTagIntArray) value).func_150302_c();
                                compound.setIntArray("BB", new int[] {
                                    arr[0] + offset[0],
                                    arr[1] + offset[1],
                                    arr[2] + offset[2],
                                    arr[3] + offset[3],
                                    arr[4] + offset[4],
                                    arr[5] + offset[5]
                                });
                            }*/
                        }
                    } catch (NBTException e) {
                        throw new CommandException("commands.structure.component.tagError", e.getMessage());
                    }
                    component.func_143009_a(world, compound);
                }

                StructureBoundingBox bb = component.getBoundingBox();
                StructureBoundingBox newBB = new StructureBoundingBox(bb.minX - 1, bb.minZ - 1, bb.maxX + 1, bb.maxZ + 1);
                component.addComponentParts(world, rand, newBB);
                this.setLastBoundingBox(bb);
                func_152373_a(sender, this, "commands.structure.component.success", bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);
            }

        } else if (args[0].equals("bb")) {

            if (args.length == 1)
                throw new WrongUsageException("commands.structure.bb.usage");
            if (args[1].equals("clear")) {
                this.clearBoundingBox();
                func_152373_a(sender, this, "commands.structure.bb.clear.success");
            } else {
                throw new WrongUsageException("commands.structure.bb.usage");
            }

        } else {
            throw new WrongUsageException("commands.structure.usage");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, new String[] {"start", "continue", "component", "bb"});
        else if (args.length == 2)
            if (args[0].equals("bb"))
                return getListOfStringsMatchingLastWord(args, new String[] {"clear"});
            else
                return null;
        else if (args.length == 5)
            if (args[0].equals("start"))
                return getListOfStringsFromIterableMatchingLastWord(args, this.getStartNameMap().keySet());
            else if (args[0].equals("component"))
                return getListOfStringsFromIterableMatchingLastWord(args, this.getComponentNameMap().keySet());
            else
                return null;
        else if (args.length == 6)
            if (args[0].equals("start"))
                return getListOfStringsMatchingLastWord(args, new String[] {"false", "true"});
            else
                return null;
        else
            return null;
    }

    public Map getStartNameMap() {
        try {
            Field field_143040_a = MapGenStructureIO.class.getDeclaredField("field_143040_a");
            field_143040_a.setAccessible(true);
            return (Map) field_143040_a.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map getComponentNameMap() {
        try {
            Field field_143039_c = MapGenStructureIO.class.getDeclaredField("field_143039_c");
            field_143039_c.setAccessible(true);
            return (Map) field_143039_c.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean canAddBoundingBox() {
        return !this.bbList.contains(null);
    }

    public void clearBoundingBox() {
        this.bbList.clear();
        this.lastBB = null;
        this.sendMessage();
    }

    public void addBoundingBox(StructureBoundingBox bb) {
        this.bbList.add(bb);
        this.sendMessage();
    }

    public void setLastBoundingBox(StructureBoundingBox bb) {
        this.lastBB = bb;
        this.sendMessage();
    }

    /*
    SimpleNetworkWrapper.sendToAll
    AbstractChannel.writeAndFlush
    DefaultChannelPipeline.writeAndFlush
    DefaultChannelHandlerContext.writeAndFlush
    DefaultChannelHandlerContext.writeAndFlush
    DefaultChannelHandlerContext.write
    DefaultChannelHandlerContext.invokeWrite
    MessageToMessageCodec.write
    MessageToMessageEncoder.write
    MessageToMessageCodec.encode
    FMLIndexedMessageToMessageCodec.encode
    FMLProxyPacket
    */
    public void sendMessage() {
        NBTTagList list = new NBTTagList();
        for (StructureBoundingBox bb : this.bbList) {
            if (bb == null)
                list.appendTag(new NBTTagIntArray(new int[0]));
            else
                list.appendTag(bb.func_151535_h());
        }
        MessageBoundingBox message = new MessageBoundingBox();
        message.bb = new NBTTagCompound();
        message.bb.setTag("BBList", list);
        if (this.lastBB != null)
            message.bb.setTag("lastBB", this.lastBB.func_151535_h());
        NetworkManager.instance.sendToAll(message);
    }

    public void sendMessageToPlayer(EntityPlayerMP player) {
        NBTTagList list = new NBTTagList();
        for (StructureBoundingBox bb : this.bbList) {
            if (bb == null)
                list.appendTag(new NBTTagIntArray(new int[0]));
            else
                list.appendTag(bb.func_151535_h());
        }
        MessageBoundingBox message = new MessageBoundingBox();
        message.bb = new NBTTagCompound();
        message.bb.setTag("BBList", list);
        if (this.lastBB != null)
            message.bb.setTag("lastBB", this.lastBB.func_151535_h());
        NetworkManager.instance.sendTo(message, player);
    }

}
