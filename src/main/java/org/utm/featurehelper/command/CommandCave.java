package org.utm.featurehelper.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import org.utm.featurehelper.feature.patch.CavesPatcher;

import java.util.List;
import java.util.Random;

public class CommandCave extends CommandBase {

    private CavesPatcher generator = new CavesPatcher();

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


            if (args[4].equals("room")) {

                float radius;
                if (args.length >= 6) {
                    radius = (float) parseDouble(sender, args[5]);
                } else {
                    radius = 1 + rand.nextFloat() * 6;
                }
                this.generator.generate(0, 0, x, y, z, radius, 0, 0, -1, 0, 0.5);

            } else if (args[4].equals("tunnel")) {

                float yaw = rand.nextFloat() * (float) Math.PI * 2F;
                float pitch = (rand.nextFloat() - 0.5F) / 4F;
                float radius = rand.nextFloat() * 2F + rand.nextFloat();
                int length = 0;
                boolean debug = false;
                if (args.length >= 6) {
                    if (!args[5].equals("*")) {  // useDefault
                        yaw = sender instanceof EntityPlayer ? ((EntityPlayer) sender).rotationYaw : 0;
                        yaw = (float) func_110666_a(sender, yaw, args[5]);
                    }
                    if (args.length >= 7) {
                        if (!args[6].equals("*")) {
                            pitch = sender instanceof EntityPlayer ? ((EntityPlayer) sender).rotationPitch : 0;
                            pitch = (float) func_110666_a(sender, pitch, args[6]);
                        }
                        if (args.length >= 8) {
                            if (!args[7].equals("*")) {
                                radius = (float) parseDouble(sender, args[7]);
                            }
                            if (args.length >= 9) {
                                length = parseInt(sender, args[8]);
                                if (args.length >= 10) {
                                    debug = parseBoolean(sender, args[9]);
                                }
                            }
                        }
                    }
                }
                this.generator.generate(0, 0, x, y, z, radius, yaw, pitch, 0, length, 1);

            } else {
                throw new WrongUsageException("commands.cave.start.usage");
            }

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
        else if (args.length == 5)
            return getListOfStringsMatchingLastWord(args, new String[] {"tunnel", "room"});
        else if (args.length == 11)
            if (args[4].equals("tunnel"))
                return getListOfStringsMatchingLastWord(args, new String[] {"false", "true"});
            else
                return null;
        else
            return null;
    }

}
