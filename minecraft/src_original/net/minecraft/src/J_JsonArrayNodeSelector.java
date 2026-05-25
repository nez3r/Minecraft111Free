package net.minecraft.src;

import java.util.List;

final class J_JsonArrayNodeSelector extends J_LeafFunctor {
	public boolean a(J_JsonNode var1) {
		return EnumJsonNodeType.ARRAY == var1.getType();
	}

	public String shortForm() {
		return "A short form array";
	}

	public List b(J_JsonNode var1) {
		return var1.getElements();
	}

	public String toString() {
		return "an array";
	}

	public Object typeSafeApplyTo(Object var1) {
		return this.b((J_JsonNode)var1);
	}

	public boolean matchesNode(Object var1) {
		return this.a((J_JsonNode)var1);
	}
}
