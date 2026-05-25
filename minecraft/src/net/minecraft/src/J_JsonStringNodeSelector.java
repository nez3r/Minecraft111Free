package net.minecraft.src;

final class J_JsonStringNodeSelector extends J_LeafFunctor {
	public boolean a(J_JsonNode var1) {
		return EnumJsonNodeType.STRING == var1.getType();
	}

	public String shortForm() {
		return "A short form string";
	}

	public String b(J_JsonNode var1) {
		return var1.getText();
	}

	public String toString() {
		return "a value that is a string";
	}

	public Object typeSafeApplyTo(Object var1) {
		return this.b((J_JsonNode)var1);
	}

	public boolean matchesNode(Object var1) {
		return this.a((J_JsonNode)var1);
	}
}
