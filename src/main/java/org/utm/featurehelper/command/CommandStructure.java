package org.utm.featurehelper.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.utm.featurehelper.structure.ComponentFactory;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;

public class CommandStructure extends CommandBase {

	private List<StructureBoundingBox> bbList = new ArrayList();
	private StructureBoundingBox lastBB;

	@Override
	public String getCommandName() {
		return "structure";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {
		return "commands.structure.usage";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length == 0)
			throw new WrongUsageException("commands.structure.usage");

		World world = sender.getEntityWorld();
		if (args[0].equals("continue")) {
			// TODO
		} else if (args[0].equals("start")) {
			if (args.length < 5)
				throw new WrongUsageException("commands.structure.start.usage");

			int x = sender.getPlayerCoordinates().posX;
	        int y = sender.getPlayerCoordinates().posY;
	        int z = sender.getPlayerCoordinates().posZ;
	        x = MathHelper.floor_double(func_110666_a(sender, (double)x, args[1]));
	        y = MathHelper.floor_double(func_110666_a(sender, (double)y, args[2]));
	        z = MathHelper.floor_double(func_110666_a(sender, (double)z, args[3]));
	        // TODO
		} else if (args[0].equals("component")) {
			if (args.length < 5)
				throw new WrongUsageException("commands.structure.component.usage");

			int x = sender.getPlayerCoordinates().posX;
	        int y = sender.getPlayerCoordinates().posY;
	        int z = sender.getPlayerCoordinates().posZ;
	        x = MathHelper.floor_double(func_110666_a(sender, (double)x, args[1]));
	        y = MathHelper.floor_double(func_110666_a(sender, (double)y, args[2]));
	        z = MathHelper.floor_double(func_110666_a(sender, (double)z, args[3]));

	        if (this.getComponentNameMap().get(args[4]) == null)
	        	throw new WrongUsageException("commands.structure.component.invalidName");

	        Random rand = new Random();
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
			} else {
				throw new WrongUsageException("commands.structure.bb.usage");
			}
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

	public void clearBoundingBox() {
		this.bbList.clear();
		this.lastBB = null;
	}
	
	public void setLastBoundingBox(StructureBoundingBox bb) {
		this.lastBB = bb;
	}
	
	public StructureBoundingBox getLastBoundingBox() {
		return this.lastBB;
	}

}
