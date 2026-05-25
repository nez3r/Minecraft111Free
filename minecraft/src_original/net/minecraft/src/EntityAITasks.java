package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;

public class EntityAITasks {
	private ArrayList field_46120_a = new ArrayList();
	private ArrayList field_46119_b = new ArrayList();

	public void func_46118_a(int var1, EntityAIBase var2) {
		this.field_46120_a.add(new EntityAITaskEntry(this, var1, var2));
	}

	public void func_46115_a() {
		ArrayList var1 = new ArrayList();
		Iterator var2 = this.field_46120_a.iterator();

		while(true) {
			EntityAITaskEntry var3;
			label46:
			do {
				while(var2.hasNext()) {
					var3 = (EntityAITaskEntry)var2.next();
					boolean var4 = this.field_46119_b.contains(var3);
					if(var4) {
						continue label46;
					}

					if(var3.field_46114_a.func_46082_a() && this.func_46116_a(var3)) {
						var1.add(var3);
						this.field_46119_b.add(var3);
					}
				}

				var2 = var1.iterator();

				while(var2.hasNext()) {
					var3 = (EntityAITaskEntry)var2.next();
					var3.field_46114_a.func_46080_e();
				}

				var2 = this.field_46119_b.iterator();

				while(var2.hasNext()) {
					var3 = (EntityAITaskEntry)var2.next();
					var3.field_46114_a.func_46081_b();
				}

				return;
			} while(var3.field_46114_a.func_46084_g() && this.func_46116_a(var3));

			var3.field_46114_a.func_46077_d();
			this.field_46119_b.remove(var3);
		}
	}

	private boolean func_46116_a(EntityAITaskEntry var1) {
		Iterator var2 = this.field_46120_a.iterator();

		while(var2.hasNext()) {
			EntityAITaskEntry var3 = (EntityAITaskEntry)var2.next();
			if(var3 != var1) {
				if(var1.field_46112_b >= var3.field_46112_b) {
					if(this.field_46119_b.contains(var3) && !this.func_46117_a(var1, var3)) {
						return false;
					}
				} else if(this.field_46119_b.contains(var3) && !var3.field_46114_a.func_46078_f()) {
					return false;
				}
			}
		}

		return true;
	}

	private boolean func_46117_a(EntityAITaskEntry var1, EntityAITaskEntry var2) {
		return (var1.field_46114_a.func_46083_c() & var2.field_46114_a.func_46083_c()) == 0;
	}
}
