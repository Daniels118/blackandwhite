package it.ld.bw.chl.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.LinkedHashSet;
import java.util.function.UnaryOperator;

public class MonitoredArrayList<E> extends ArrayList<E> {
	private static final long serialVersionUID = 1L;
	
	private final LinkedHashSet<ListListener<E>> listeners = new LinkedHashSet<>();
	
	public MonitoredArrayList() {
		super();
	}
	
	public MonitoredArrayList(int initialCapacity) {
		super(initialCapacity);
	}
	
	public void addListener(ListListener<E> listener) {
		listeners.add(listener);
	}
	
	public void removeListener(ListListener<E> listener) {
		listeners.remove(listener);
	}
	
	@Override
	public boolean add(E e) {
		boolean r = super.add(e);
		if (r) {
			for (ListListener<E> listener : listeners) {
				listener.itemAdded(this, e);
			}
		}
		return r;
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean r = super.addAll(c);
		if (r) {
			for (ListListener<E> listener : listeners) {
				listener.multipleChanges(this);
			}
		}
		return r;
	}
	
	@Override
	public void clear() {
		if (!this.isEmpty()) {
			super.clear();
			for (ListListener<E> listener : listeners) {
				listener.multipleChanges(this);
			}
		}
	}
	
	@Override
	public E remove(int index) {
		E e = super.remove(index);
		for (ListListener<E> listener : listeners) {
			listener.itemRemoved(this, e);
		}
		return e;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		boolean r = super.remove(o);
		if (r) {
			for (ListListener<E> listener : listeners) {
				listener.itemRemoved(this, (E)o);
			}
		}
		return r;
	}
	
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean r = super.removeAll(c);
		if (r) {
			for (ListListener<E> listener : listeners) {
				listener.multipleChanges(this);
			}
		}
		return r;
	}
	
	@Override
	public void replaceAll(UnaryOperator<E> operator) {
		super.replaceAll(operator);
		for (ListListener<E> listener : listeners) {
			listener.multipleChanges(this);
		}
	}
	
	
	public interface ListListener<E> extends EventListener {
		public void itemAdded(MonitoredArrayList<E> list, E item);
		public void itemRemoved(MonitoredArrayList<E> list, E item);
		public void multipleChanges(MonitoredArrayList<E> list);
	}
}
