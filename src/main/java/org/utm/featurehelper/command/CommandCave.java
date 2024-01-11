package org.utm.featurehelper.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import org.utm.featurehelper.feature.patch.CavesPatcher;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CommandCave extends CommandBase {

    @Override
    public String getCommandName() {
        return "cave";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.cave.usage";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0)
            throw new WrongUsageException("commands.cave.usage");

        World world = sender.getEntityWorld();
        Random rand = new Random();

        if (args[0].equals("start")) {

            if (args.length < 5)
                throw new WrongUsageException("commands.cave.start.usage");

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

            CavesPatcher.removeAll();

            if (args[4].equals("room")) {

                float radius;
                if (args.length >= 6) {
                    radius = (float) parseDouble(sender, args[5]);
                } else {
                    radius = 1 + rand.nextFloat() * 6;
                }
                new CavesPatcher().generate(world, x, y, z, radius, 0, 0, -1, 0, 0.5, false);

            } else if (args[4].equals("tunnel")) {

                float yaw = rand.nextFloat() * (float) Math.PI * 2F;
                float pitch = (rand.nextFloat() - 0.5F) / 4F;
                float radius = rand.nextFloat() * 2F + rand.nextFloat();
                if (rand.nextInt(10) == 0)
                    radius *= rand.nextFloat() * rand.nextFloat() * 3 + 1;
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
                new CavesPatcher().generate(world, x, y, z, radius, yaw, pitch, 0, length, 1, debug);

            } else {
                throw new WrongUsageException("commands.cave.start.usage");
            }

            func_152373_a(sender, this, "commands.cave.start.success", x, y, z);

        } else if (args[0].equals("continue")) {

            CavesPatcher current = CavesPatcher.getCurrent();
            if (current != null) {
                current.addRoom();
                double[] pos = current.getPos();
                func_152373_a(sender, this, "commands.cave.continue.success", pos[0], pos[1], pos[2]);
            } else {
                func_152373_a(sender, this, "commands.cave.continue.complete");
            }

        } else if (args[0].equals("trail")) {

            if (args.length == 1)
                throw new WrongUsageException("commands.cave.trail.usage");
            if (args[1].equals("clear")) {
                CavesPatcher.removeAll();
                func_152373_a(sender, this, "commands.cave.trail.clear.success");
            } else {
                throw new WrongUsageException("commands.cave.trail.usage");
            }

        } else {
            throw new WrongUsageException("commands.cave.usage");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, new String[] {"start", "continue", "trail"});
        else if (args.length == 2)
            if (args[0].equals("trail"))
                return getListOfStringsMatchingLastWord(args, new String[] {"clear"});
            else
                return null;
        else if (args.length == 5) {
            List list = getListOfStringsMatchingLastWord(args, new String[]{"tunnel", "room"});
            if (list.contains("tunnel"))
                func_152373_a(sender, this, "commands.cave.start.tunnel.usage");
            if (list.contains("room"))
                func_152373_a(sender, this, "commands.cave.start.room.usage");
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
                    float radius = rand.nextFloat() * 2F + rand.nextFloat();
                    if (rand.nextInt(10) == 0)
                        radius *= rand.nextFloat() * rand.nextFloat() * 3 + 1;
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

}
