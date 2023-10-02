/* Copyright (c) 2023 Daniele Lombardi / Daniels118
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.ld.bw.chl.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

public abstract class StructArray<E extends Struct> extends Section {
	protected List<E> items = new ArrayList<E>();
	
	public abstract Class<E> getItemClass();
	
	public List<E> getItems() {
		return items;
	}
	
	public void setItems(List<E> items) {
		this.items = items;
	}
	
	/**Returns the size of this array in bytes.
	 * The default implementation calculates the size by summing up the size of all items in the array.
	 * If the items are fix-sized, you should override this method to improve its performances using the following formula:
	 *   return 4 + items.size() * SIZE_OF_ITEM;
	 */
	@Override
	public int getLength() {
		int s = 4;
		for (E item : items) {
			s += item.getLength();
		}
		return s;
	}
	
	@Override
	public void read(EndianDataInputStream str) throws Exception {
		items = readStructArray(str, getItemClass());
	}

	@Override
	public void write(EndianDataOutputStream str) throws Exception {
		writeStructArray(str, items);
	}
	
	/**Returns the classname of the items in this array followd by their quantity.
	 *
	 */
	@Override
	public String toString() {
		return getItemClass().getName() + "[" + items.size() + "]";
	}
	
	private static <E extends Struct> List<E> readStructArray(EndianDataInputStream str, Class<E> cls) throws Exception {
		Constructor<E> constructor = cls.getConstructor();
		int count = str.readInt();
		List<E> res = new ArrayList<E>(count);
		for (int i = 0; i < count; i++) {
			E e = constructor.newInstance();
			e.read(str);
			res.add(e);
		}
		return res;
	}
	
	private static void writeStructArray(EndianDataOutputStream str, List<? extends Struct> items) throws Exception {
		str.writeInt(items.size());
		for (Struct struct : items) {
			struct.write(str);
		}
	}
}
