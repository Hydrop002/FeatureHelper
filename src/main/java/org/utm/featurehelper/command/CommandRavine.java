package org.utm.featurehelper.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import org.utm.featurehelper.feature.patch.RavinePatcher;
import org.utm.featurehelper.network.MessageRenderControl;
import org.utm.featurehelper.network.NetworkManager;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommandRavine extends CommandBase {

    @Override
    public String getCommandName() {
        return "ravine";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.ravine.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0)
            throw new WrongUsageException("commands.ravine.usage");

        World world = sender.getEntityWorld();
        Random rand = new Random();

        if (args[0].equals("start")) {

            if (args.length < 5)
                throw new WrongUsageException("commands.ravine.start.usage");

            double x;
            double y;
            double z;
            if (sender instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) sender;
                x = player.posX;
                y = player.posY;
                z = player.posZ;
            } else {
                x = sender.getPlayerCoordinates().posX + 0.5;
                y = sender.getPlayerCoordinates().posY + 0.5;
                z = sender.getPlayerCoordinates().posZ + 0.5;
            }
            x = func_110666_a(sender, x, args[1]);
            y = func_110666_a(sender, y, args[2]);
            z = func_110666_a(sender, z, args[3]);

            RavinePatcher.removeAll();
            if (args[4].equals("tunnel")) {

                float yaw = rand.nextFloat() * (float) Math.PI * 2F;
                float pitch = (rand.nextFloat() - 0.5F) / 4F;
                float radius = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
                int length = 0;
                boolean debug = false;
                if (args.length >= 6) {
                    yaw = sender instanceof EntityPlayer ? ((EntityPlayer) sender).rotationYaw : 0;
                    yaw = (float) func_110666_a(sender, yaw, args[5]);
                    yaw = (yaw + 90F) * (float) Math.PI / 180F;
                    if (args.length >= 7) {
                        pitch = sender instanceof EntityPlayer ? ((EntityPlayer) sender).rotationPitch : 0;
                        pitch = (float) func_110666_a(sender, pitch, args[6]);
                        pitch = -pitch * (float) Math.PI / 180F;
                        if (args.length >= 8) {
                            radius = (float) parseDouble(sender, args[7]);
                            if (args.length >= 9) {
                                length = parseInt(sender, args[8]);
                                if (args.length >= 10) {
                                    debug = parseBoolean(sender, args[9]);
                                }
                            }
                        }
                    }
                }
                new RavinePatcher().generate(world, x, y, z, radius, yaw, pitch, 0, length, 3, debug);

            } else {
                throw new WrongUsageException("commands.ravine.start.usage");
            }
            RavinePatcher.sendMessage();

            func_152373_a(sender, this, "commands.ravine.start.success", x, y, z);

        } else if (args[0].equals("continue")) {

            RavinePatcher current = RavinePatcher.getCurrent();
            if (current != null) {
                if (world != RavinePatcher.worldObj)
                    throw new CommandException("commands.ravine.continue.failed");
                current.addRoom();
                double[] pos = current.getPos();
                func_152373_a(sender, this, "commands.ravine.continue.success", pos[0], pos[1], pos[2]);
            } else {
                func_152373_a(sender, this, "commands.ravine.continue.complete");
            }

        } else if (args[0].equals("trail")) {

            if (!(sender instanceof EntityPlayerMP))
                throw new CommandException("commands.ravine.trail.failed");

            if (args.length == 1)
                throw new WrongUsageException("commands.ravine.trail.usage");
            if (args[1].equals("hide")) {
                this.hideTrail((EntityPlayerMP) sender);
                func_152373_a(sender, this, "commands.ravine.trail.hide.success");
            } else if (args[1].equals("show")) {
                this.showTrail((EntityPlayerMP) sender);
                func_152373_a(sender, this, "commands.ravine.trail.show.success");
            } else {
                throw new WrongUsageException("commands.ravine.trail.usage");
            }

        } else {
            throw new WrongUsageException("commands.ravine.usage");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, new String[] {"start", "continue", "trail"});
        else if (args.length == 2)
            if (args[0].equals("trail"))
                return getListOfStringsMatchingLastWord(args, new String[] {"hide", "show"});
            else
                return null;
        else if (args.length == 5) {
            func_152373_a(sender, this, "commands.ravine.start.usage");
            return getListOfStringsMatchingLastWord(args, new String[]{"tunnel"});
        } else if (args.length == 6) {
            if (args[0].equals("start")) {
                if (args[4].equals("tunnel")) {
                    Random rand = new Random();
                    float yaw = rand.nextFloat() * 360;
                    return Arrays.asList(String.valueOf(yaw));
                }
            }
            return null;
        } else if (args.length == 7) {
            if (args[0].equals("start")) {
                if (args[4].equals("tunnel")) {
                    Random rand = new Random();
                    float pitch = (rand.nextFloat() - 0.5F) * 45F / (float) Math.PI;
                    return Arrays.asList(String.valueOf(pitch));
                }
            }
            return null;
        } else if (args.length == 8) {
            if (args[0].equals("start")) {
                if (args[4].equals("tunnel")) {
                    Random rand = new Random();
                    float radius = (rand.nextFloat() * 2.0F + rand.nextFloat()) * 2.0F;
                    return Arrays.asList(String.valueOf(radius));
                }
            }
            return null;
        } else if (args.length == 10)
            if (args[0].equals("start") && args[4].equals("tunnel"))
                return getListOfStringsMatchingLastWord(args, new String[] {"false", "true"});
            else
                return null;
        else
            return null;
    }

    public void hideTrail(EntityPlayerMP player) {
        MessageRenderControl message = new MessageRenderControl();
        message.rc = new NBTTagCompound();
        message.rc.setByte("renderType", (byte) 3);
        message.rc.setBoolean("render", false);
        NetworkManager.instance.sendTo(message, player);
    }

    public void showTrail(EntityPlayerMP player) {
        MessageRenderControl message = new MessageRenderControl();
        message.rc = new NBTTagCompound();
        message.rc.setByte("renderType", (byte) 3);
        message.rc.setBoolean("render", true);
        NetworkManager.instance.sendTo(message, player);
    }

}
