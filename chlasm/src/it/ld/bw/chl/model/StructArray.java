/* Copyright (c) 2023-2025 Daniele Lombardi / Daniels118
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

import java.util.ArrayList;
import java.util.List;

import it.ld.utils.EndianDataInputStream;
import it.ld.utils.EndianDataOutputStream;

public abstract class StructArray<E extends Struct> extends Struct {
	public static final int MAX_ITEMS = 32 * 1024 * 1024;
	
	protected ArrayList<E> items = new ArrayList<E>();
	
	public abstract Class<E> getItemClass();
	public abstract E createItem();
	
	public ArrayList<E> getItems() {
		return items;
	}
	
	public void setItems(ArrayList<E> items) {
		this.items = items;
	}
	
	@Override
	public void read(EndianDataInputStream str) throws Exception {
		items = readStructArray(str);
	}

	@Override
	public void write(EndianDataOutputStream str) throws Exception {
		writeStructArray(str, items);
	}
	
	public String getTypeName() {
		return getItemClass().getSimpleName();
	}
	
	/**Returns the class name of the items in this array followed by their quantity.
	 */
	@Override
	public String toString() {
		return getTypeName() + "[" + items.size() + "]";
	}
	
	private ArrayList<E> readStructArray(EndianDataInputStream str) throws Exception {
		int count = str.readInt();
		if (count < 0) throw new Exception("Invalid "+getTypeName()+" count: " + count);
		if (count > MAX_ITEMS) throw new Exception("Too many "+getTypeName()+"s: " + count);
		ArrayList<E> res = new ArrayList<E>(count);
		for (int i = 0; i < count; i++) {
			try {
				E e = readItem(str, i);
				res.add(e);
			} catch (Exception e) {
				throw new Exception(e.getMessage() + ", reading " + getTypeName() + " " + i, e);
			}
		}
		return res;
	}
	
	protected E readItem(EndianDataInputStream str, int index) throws Exception {
		E e = createItem();
		e.read(str);
		return e;
	}
	
	private void writeStructArray(EndianDataOutputStream str, List<? extends Struct> items) throws Exception {
		str.writeInt(items.size());
		for (int i = 0; i < items.size(); i++) {
			try {
				Struct struct = items.get(i);
				struct.write(str);
			} catch (Exception e) {
				throw new Exception(e.getMessage() + ", writing " + getTypeName() + " " + i, e);
			}
		}
	}
}
