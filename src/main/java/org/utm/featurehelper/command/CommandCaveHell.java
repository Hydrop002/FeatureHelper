package org.utm.featurehelper.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.utm.featurehelper.feature.patch.CavesHellPatcher;
import org.utm.featurehelper.network.MessageRenderControl;
import org.utm.featurehelper.network.NetworkManager;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class CommandCaveHell extends CommandBase {

    @Override
    public String getName() {
        return "cavehell";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "commands.cavehell.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0)
            throw new WrongUsageException("commands.cavehell.usage");

        World world = sender.getEntityWorld();
        Random rand = new Random();

        if (args[0].equals("start")) {

            if (args.length < 5)
                throw new WrongUsageException("commands.cavehell.start.usage");

            Vec3d pos = sender.getPositionVector();
            double x = parseDouble(pos.x, args[1], true);
            double y = parseDouble(pos.y, args[2], false);
            double z = parseDouble(pos.z, args[3], true);

            CavesHellPatcher.removeAll();
            if (args[4].equals("room")) {

                float radius;
                if (args.length >= 6) {
                    radius = (float) parseDouble(args[5]);
                } else {
                    radius = 1 + rand.nextFloat() * 6;
                }
                new CavesHellPatcher().generate(world, x, y, z, radius, 0, 0, -1, 0, 0.5, false);

            } else if (args[4].equals("tunnel")) {

                float yaw = rand.nextFloat() * (float) Math.PI * 2F;
                float pitch = (rand.nextFloat() - 0.5F) / 4F;
                float radius = rand.nextFloat() * 2F + rand.nextFloat();
                int length = 0;
                boolean debug = false;
                if (args.length >= 6) {
                    yaw = sender instanceof EntityPlayer ? ((EntityPlayer) sender).rotationYaw : 0;
                    yaw = (float) parseDouble(yaw, args[5], false);
                    yaw = (yaw + 90F) * (float) Math.PI / 180F;
                    if (args.length >= 7) {
                        pitch = sender instanceof EntityPlayer ? ((EntityPlayer) sender).rotationPitch : 0;
                        pitch = (float) parseDouble(pitch, args[6], false);
                        pitch = -pitch * (float) Math.PI / 180F;
                        if (args.length >= 8) {
                            radius = (float) parseDouble(args[7]);
                            if (args.length >= 9) {
                                length = parseInt(args[8]);
                                if (args.length >= 10) {
                                    debug = parseBoolean(args[9]);
                                }
                            }
                        }
                    }
                }
                new CavesHellPatcher().generate(world, x, y, z, radius * 2.0F, yaw, pitch, 0, length, 0.5, debug);

            } else {
                throw new WrongUsageException("commands.cavehell.start.usage");
            }
            CavesHellPatcher.sendMessage();

            notifyCommandListener(sender, this, "commands.cavehell.start.success", x, y, z);

        } else if (args[0].equals("continue")) {

            CavesHellPatcher current = CavesHellPatcher.getCurrent();
            if (current != null) {
                if (world != CavesHellPatcher.worldObj)
                    throw new CommandException("commands.cavehell.continue.failed");
                current.addRoom();
                Vec3d pos = current.getPos();
                notifyCommandListener(sender, this, "commands.cavehell.continue.success", pos.x, pos.y, pos.z);
            } else {
                notifyCommandListener(sender, this, "commands.cavehell.continue.complete");
            }

        } else if (args[0].equals("trail")) {

            if (!(sender instanceof EntityPlayerMP))
                throw new CommandException("commands.cavehell.trail.failed");

            if (args.length == 1)
                throw new WrongUsageException("commands.cavehell.trail.usage");
            if (args[1].equals("hide")) {
                this.hideTrail((EntityPlayerMP) sender);
                notifyCommandListener(sender, this, "commands.cavehell.trail.hide.success");
            } else if (args[1].equals("show")) {
                this.showTrail((EntityPlayerMP) sender);
                notifyCommandListener(sender, this, "commands.cavehell.trail.show.success");
            } else {
                throw new WrongUsageException("commands.cavehell.trail.usage");
            }

        } else {
            throw new WrongUsageException("commands.cavehell.usage");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, new String[] {"start", "continue", "trail"});
        else if (args.length == 2)
            if (args[0].equals("trail"))
                return getListOfStringsMatchingLastWord(args, new String[] {"hide", "show"});
            else
                return Collections.emptyList();
        else if (args.length == 5) {
            List<String> list = getListOfStringsMatchingLastWord(args, new String[]{"tunnel", "room"});
            if (list.contains("tunnel"))
                notifyCommandListener(sender, this, "commands.cavehell.start.tunnel.usage");
            if (list.contains("room"))
                notifyCommandListener(sender, this, "commands.cavehell.start.room.usage");
            return list;
        } else if (args.length == 6) {
            if (args[0].equals("start")) {
                if (args[4].equals("room")) {
                    Random rand = new Random();
                    float radius = 1 + rand.nextFloat() * 6;
                    return Arrays.asList(String.valueOf(radius));
                } else if (args[4].equals("tunnel")) {
                    Random rand = new Random();
                    float yaw = rand.nextFloat() * 360;
                    return Arrays.asList(String.valueOf(yaw));
                }
            }
            return Collections.emptyList();
        } else if (args.length == 7) {
            if (args[0].equals("start")) {
                if (args[4].equals("tunnel")) {
                    Random rand = new Random();
                    float pitch = (rand.nextFloat() - 0.5F) * 45F / (float) Math.PI;
                    return Arrays.asList(String.valueOf(pitch));
                }
            }
            return Collections.emptyList();
        } else if (args.length == 8) {
            if (args[0].equals("start")) {
                if (args[4].equals("tunnel")) {
                    Random rand = new Random();
                    float radius = rand.nextFloat() * 2F + rand.nextFloat();
                    return Arrays.asList(String.valueOf(radius));
                }
            }
            return Collections.emptyList();
        } else if (args.length == 10)
            if (args[0].equals("start") && args[4].equals("tunnel"))
                return getListOfStringsMatchingLastWord(args, new String[] {"false", "true"});
            else
                return Collections.emptyList();
        else
            return Collections.emptyList();
    }

    public void hideTrail(EntityPlayerMP player) {
        MessageRenderControl message = new MessageRenderControl();
        message.rc = new NBTTagCompound();
        message.rc.setByte("renderType", (byte) 2);
        message.rc.setBoolean("render", false);
        NetworkManager.instance.sendTo(message, player);
    }

    public void showTrail(EntityPlayerMP player) {
        MessageRenderControl message = new MessageRenderControl();
        message.rc = new NBTTagCompound();
        message.rc.setByte("renderType", (byte) 2);
        message.rc.setBoolean("render", true);
        NetworkManager.instance.sendTo(message, player);
    }

}
