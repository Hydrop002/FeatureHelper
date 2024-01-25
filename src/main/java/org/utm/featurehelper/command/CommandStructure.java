package org.utm.featurehelper.command;

import java.util.*;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.*;
import org.utm.featurehelper.network.MessageBoundingBox;
import org.utm.featurehelper.network.MessageRenderControl;
import org.utm.featurehelper.network.NetworkManager;
import org.utm.featurehelper.feature.ComponentFactory;
import org.utm.featurehelper.feature.StartFactory;

import javax.annotation.Nullable;

public class CommandStructure extends CommandBase {

    private List<StructureBoundingBox> bbList = new ArrayList<>();
    private StructureBoundingBox lastBB;
    private World startWorld;
    private World lastWorld;

    private Iterator<StructureComponent> it;
    private StructureStart start;

    @Override
    public String getName() {
        return "structure";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.structure.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0)
            throw new WrongUsageException("commands.structure.usage");

        World world = sender.getEntityWorld();
        Random rand = new Random();

        if (args[0].equals("continue")) {

            if (this.start != null) {
                if (world != this.startWorld)
                    throw new CommandException("commands.structure.continue.failed");
                this.lastWorld = world;
                StructureBoundingBox bb = this.start.getBoundingBox();
                StructureBoundingBox newBB = new StructureBoundingBox(bb.minX - 1, bb.minZ - 1, bb.maxX + 1, bb.maxZ + 1);
                this.setLastBoundingBox(bb);
                if (this.it != null && this.it.hasNext()) {
                    StructureComponent component = this.it.next();
                    if (component.getBoundingBox().intersectsWith(newBB)) {
                        component.addComponentParts(world, rand, newBB);
                        this.addBoundingBox(component.getBoundingBox());
                    }
                    notifyCommandListener(sender, this, "commands.structure.continue.success");
                } else {
                    if (this.canAddBoundingBox())
                        this.addBoundingBox(null);
                    notifyCommandListener(sender, this, "commands.structure.continue.complete");
                }
                this.sendMessage(world);
            } else {
                notifyCommandListener(sender, this, "commands.structure.continue.complete");
            }

        } else if (args[0].equals("start")) {

            if (args.length < 5)
                throw new WrongUsageException("commands.structure.start.usage");

            BlockPos blockPos = parseBlockPos(sender, args, 1, false);

            if (!StartFactory.getNameSet().contains(args[4]))
                throw new WrongUsageException("commands.structure.start.invalidName");

            this.start = StartFactory.getStart(args[4], world, blockPos.getX() >> 4, blockPos.getZ() >> 4, rand);
            this.startWorld = world;
            this.lastWorld = world;

            if (!this.start.isSizeableStructure())
                throw new CommandException("commands.structure.start.failed");

            boolean debug = false;
            if (args.length >= 6) {
                debug = parseBoolean(args[5]);
                if (args.length >= 7) {
                    NBTTagCompound compound = this.start.writeStructureComponentsToNBT(blockPos.getX() >> 4, blockPos.getZ() >> 4);
                    String tagStr = getChatComponentFromNthArg(sender, args, 6).getUnformattedText();
                    try {
                        NBTTagCompound tag = JsonToNBT.getTagFromJson(tagStr);
                        for (String key : tag.getKeySet()) {
                            NBTBase value = tag.getTag(key);
                            compound.setTag(key, value);
                        }
                    } catch (NBTException e) {
                        throw new CommandException("commands.structure.start.tagError", e.getMessage());
                    }
                    this.start.readStructureComponentsFromNBT(world, compound);
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
                        if (component instanceof StructureOceanMonumentPieces.MonumentBuilding) {
                            StructureOceanMonumentPieces.MonumentBuilding monument = (StructureOceanMonumentPieces.MonumentBuilding) component;
                            ArrayList<StructureOceanMonumentPieces.Piece> rooms = (ArrayList<StructureOceanMonumentPieces.Piece>) monument.childPieces;
                            ArrayList<StructureComponent> roomsCopy = (ArrayList<StructureComponent>) rooms.clone();
                            rooms.clear();
                            component.addComponentParts(world, rand, newBB);
                            this.it = roomsCopy.iterator();
                        } else {
                            component.addComponentParts(world, rand, newBB);
                        }
                        this.addBoundingBox(component.getBoundingBox());
                    }
                }
            } else {
                this.it = null;
                this.start.generateStructure(world, rand, newBB);
            }
            this.sendMessage(world);
            notifyCommandListener(sender, this, "commands.structure.start.success", blockPos.getX(), blockPos.getY(), blockPos.getZ());

        } else if (args[0].equals("component")) {

            if (args.length < 5)
                throw new WrongUsageException("commands.structure.component.usage");

            BlockPos blockPos = parseBlockPos(sender, args, 1, false);

            if (!ComponentFactory.getNameSet().contains(args[4]))
                throw new WrongUsageException("commands.structure.component.invalidName");

            StructureComponent component = ComponentFactory.getComponent(args[4], world, blockPos.getX(), blockPos.getY(), blockPos.getZ(), rand);
            if (component != null) {
                this.lastWorld = world;

                if (args.length >= 6) {
                    NBTTagCompound compound = component.createStructureBaseNBT();
                    String tagStr = getChatComponentFromNthArg(sender, args, 5).getUnformattedText();
                    try {
                        NBTTagCompound tag = JsonToNBT.getTagFromJson(tagStr);
                        for (String key : tag.getKeySet()) {
                            NBTBase value = tag.getTag(key);
                            compound.setTag(key, value);
                        }
                    } catch (NBTException e) {
                        throw new CommandException("commands.structure.component.tagError", e.getMessage());
                    }
                    component.readStructureBaseNBT(world, compound);
                }

                StructureBoundingBox bb = component.getBoundingBox();
                StructureBoundingBox newBB = new StructureBoundingBox(bb.minX - 1, bb.minZ - 1, bb.maxX + 1, bb.maxZ + 1);
                component.addComponentParts(world, rand, newBB);
                this.setLastBoundingBox(bb);
                this.sendMessage(world);
                notifyCommandListener(sender, this, "commands.structure.component.success", bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ);

            } else {
                notifyCommandListener(sender, this, "commands.structure.component.empty");
            }

        } else if (args[0].equals("bb")) {

            if (!(sender instanceof EntityPlayerMP))
                throw new CommandException("commands.structure.bb.failed");

            if (args.length == 1)
                throw new WrongUsageException("commands.structure.bb.usage");
            if (args[1].equals("hide")) {
                this.hideBoundingBox((EntityPlayerMP) sender);
                notifyCommandListener(sender, this, "commands.structure.bb.hide.success");
            } else if (args[1].equals("show")) {
                this.showBoundingBox((EntityPlayerMP) sender);
                notifyCommandListener(sender, this, "commands.structure.bb.show.success");
            } else {
                throw new WrongUsageException("commands.structure.bb.usage");
            }

        } else {
            throw new WrongUsageException("commands.structure.usage");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, new String[] {"start", "continue", "component", "bb"});
        if (args[0].equals("start"))
            if (args.length <= 4)
                return getTabCompletionCoordinate(args, 1, pos);
            else if (args.length == 5)
                return getListOfStringsMatchingLastWord(args, StartFactory.getNameSet());
            else if (args.length == 6)
                return getListOfStringsMatchingLastWord(args, new String[] {"false", "true"});
            else
                return Collections.emptyList();
        else if (args[0].equals("component"))
            if (args.length <= 4)
                return getTabCompletionCoordinate(args, 1, pos);
            else if (args.length == 5)
                return getListOfStringsMatchingLastWord(args, ComponentFactory.getNameSet());
            else
                return Collections.emptyList();
        else if (args[0].equals("bb"))
            if (args.length == 2)
                return getListOfStringsMatchingLastWord(args, new String[] {"hide", "show"});
            else
                return Collections.emptyList();
        else
            return Collections.emptyList();
    }

    public boolean canAddBoundingBox() {
        return !this.bbList.contains(null);
    }

    public void clearBoundingBox() {
        this.bbList.clear();
        this.lastBB = null;
    }

    public void addBoundingBox(@Nullable StructureBoundingBox bb) {
        this.bbList.add(bb);
    }

    public void setLastBoundingBox(StructureBoundingBox bb) {
        this.lastBB = bb;
    }

    public void sendMessage(World world) {
        NBTTagList list = new NBTTagList();
        for (StructureBoundingBox bb : this.bbList) {
            if (bb == null)
                list.appendTag(new NBTTagIntArray(new int[0]));
            else
                list.appendTag(bb.toNBTTagIntArray());
        }
        MessageBoundingBox message = new MessageBoundingBox();
        message.bb = new NBTTagCompound();
        if (world == this.startWorld)
            message.bb.setTag("BBList", list);
        else
            message.bb.setTag("BBList", new NBTTagList());
        if (world == this.lastWorld && this.lastBB != null)
            message.bb.setTag("lastBB", this.lastBB.toNBTTagIntArray());
        NetworkManager.instance.sendToDimension(message, world.provider.getDimension());
    }

    public void sendMessageToPlayer(EntityPlayerMP player, World world) {
        NBTTagList list = new NBTTagList();
        for (StructureBoundingBox bb : this.bbList) {
            if (bb == null)
                list.appendTag(new NBTTagIntArray(new int[0]));
            else
                list.appendTag(bb.toNBTTagIntArray());
        }
        MessageBoundingBox message = new MessageBoundingBox();
        message.bb = new NBTTagCompound();
        if (world == this.startWorld)
            message.bb.setTag("BBList", list);
        else
            message.bb.setTag("BBList", new NBTTagList());
        if (world == this.lastWorld && this.lastBB != null)
            message.bb.setTag("lastBB", this.lastBB.toNBTTagIntArray());
        NetworkManager.instance.sendTo(message, player);
    }

    public void hideBoundingBox(EntityPlayerMP player) {
        MessageRenderControl message = new MessageRenderControl();
        message.rc = new NBTTagCompound();
        message.rc.setByte("renderType", (byte) 0);
        message.rc.setBoolean("render", false);
        NetworkManager.instance.sendTo(message, player);
    }

    public void showBoundingBox(EntityPlayerMP player) {
        MessageRenderControl message = new MessageRenderControl();
        message.rc = new NBTTagCompound();
        message.rc.setByte("renderType", (byte) 0);
        message.rc.setBoolean("render", true);
        NetworkManager.instance.sendTo(message, player);
    }

}
