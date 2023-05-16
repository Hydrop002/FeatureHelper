package org.utm.featurehelper.structure;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureComponent;

public interface IComponent {
	
	public StructureComponent getComponent(World world, int x, int y, int z, Random rand);

}
