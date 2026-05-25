package net.minecraft.src;

interface J_Functor {
	boolean matchesNode(Object var1);

	Object applyTo(Object var1);

	String shortForm();
}
