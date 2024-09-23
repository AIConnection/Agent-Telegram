package com.github.connectionai.agents.core.bdi.pln;

import java.util.List;

public abstract class PLNBase {
	
	private final List<PLNTask> items;

    public PLNBase(final List<PLNTask> items) {
    	
        this.items = items;
    }

	public List<PLNTask> getItems() {
		return items;
	}
	
	public void add(final PLNTask plnTask) {
		this.items.add(plnTask);
	}
	
	public void remove(final PLNTask plnTask) {
		this.items.remove(plnTask);
	}
	
	public List<String> handles(){

		return items
				.stream()
				.map(PLNTask::handle)
				.toList();
	}
}
