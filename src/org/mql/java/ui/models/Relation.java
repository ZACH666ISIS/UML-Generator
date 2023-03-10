package org.mql.java.ui.models;

import org.mql.java.models.assotiation.AssociationType;

public class Relation {
	
	private long id1,
				 id2;
	private AssociationType type;
	
	public Relation() {
	}
	
	public Relation(long id1, long id2){
		this.id1 = id1;
		this.id2 = id2;
	}
	
	public Relation(long id1, long id2, AssociationType type) {
		super();
		this.id1 = id1;
		this.id2 = id2;
		this.setType(type);
	}
	
	

	public void setId1(long id1) {
		this.id1 = id1;
	}

	public void setId2(long id2) {
		this.id2 = id2;
	}

	public long getId1() {
		return id1;
	}
	public long getId2() {
		return id2;
	}

	public AssociationType getType() {
		return type;
	}

	public void setType(AssociationType type) {
		this.type = type;
	}
	
}
