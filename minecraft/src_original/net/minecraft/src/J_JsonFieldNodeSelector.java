package net.minecraft.src;

import java.util.Map;

final class J_JsonFieldNodeSelector extends J_LeafFunctor {
	final J_JsonStringNode field_27066_a;

	J_JsonFieldNodeSelector(J_JsonStringNode var1) {
		this.field_27066_a = var1;
	}

	public boolean a(Map var1) {
		return var1.containsKey(this.field_27066_a);
	}

	public String shortForm() {
		return "\"" + this.field_27066_a.getText() + "\"";
	}

	public J_JsonNode b(Map var1) {
		return (J_JsonNode)var1.get(this.field_27066_a);
	}

	public String toString() {
		return "a field called [\"" + this.field_27066_a.getText() + "\"]";
	}

	public Object typeSafeApplyTo(Object var1) {
		return this.b((Map)var1);
	}

	public boolean matchesNode(Object var1) {
		return this.a((Map)var1);
	}
}
