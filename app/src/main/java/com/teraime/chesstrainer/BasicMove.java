package com.teraime.chesstrainer;

public class BasicMove {

		protected Cord from,to;
		
		public BasicMove() {
			
		}
		
		public BasicMove(Cord from, Cord to) {
			this.from=from;
			this.to=to;
		}
		
		
		public int getFromColumn() {
			return from.column;
		}
		
		public int getFromRow() {
			return from.row;
		}
		
		public int getToColumn() {
			return to.column;
		}
		
		public int getToRow() {
			return to.row;
		}
		
		
		@Override
		public boolean equals(Object other) {
			if (other == null) return false;
		    if (other == this) return true;
		    if (!(other instanceof BasicMove))return false;
		    Move mother = (Move)other;
			return (this.from.equals(mother.from)&&
					this.to.equals(mother.to));
		}

	public Cord getFromCord() {
			return from;
	}
}

