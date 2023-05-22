package org.utm.featurehelper.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;

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

        Random rand = new Random();

        if (args[0].equals("start")) {

            if (args.length < 4)
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

            double yaw = rand.nextDouble() * Math.PI * 2;
            double pitch = (rand.nextDouble() - 0.5) / 4;
            double radius = rand.nextDouble() * 2 + rand.nextDouble();
            int length = 0;
            boolean isRoom = false;
            int branches = 1;
            if (args.length >= 5) {
                if (!args[4].equals("*")) {  // useDefault
                    yaw = sender instanceof EntityPlayer ? ((EntityPlayer) sender).rotationYaw : 0;
                    yaw = func_110666_a(sender, yaw, args[4]);
                }
                if (args.length >= 6) {
                    if (!args[5].equals("*")) {
                        pitch = sender instanceof EntityPlayer ? ((EntityPlayer) sender).rotationPitch : 0;
                        pitch = func_110666_a(sender, pitch, args[5]);
                    }
                    if (args.length >= 7) {
                        if (!args[6].equals("*")) {
                            radius = parseDouble(sender, args[6]);
                        }
                        if (args.length >= 8) {
                            length = parseInt(sender, args[7]);
                            if (args.length >= 9) {
                                isRoom = parseBoolean(sender, args[8]);
                                if (args.length >= 10) {
                                    branches = parseInt(sender, args[9]);
                                }
                            }
                        }
                    }
                }
            }

            // TODO: generateCave
            func_152373_a(sender, this, "commands.cave.start.success");

        } else if (args[0].equals("continue")) {

            // TODO: continue
            func_152373_a(sender, this, "commands.cave.continue.success");

        } else {
            throw new WrongUsageException("commands.cave.continue.usage");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args, new String[] {"start", "continue"});
        else if (args.length == 9)
            return getListOfStringsMatchingLastWord(args, new String[] {"false", "true"});
        else
            return null;
    }

}
