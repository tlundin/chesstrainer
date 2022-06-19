package com.teraime.chesstrainer;

import android.util.Log;

public class BasicMove {

		public Cord from,to;
		
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
			boolean ret;
			if (other == null)
				ret= false;
		    if (other == this)
				ret = true;
		    if (!(other instanceof BasicMove))
				ret = false;
		    BasicMove mother = (BasicMove)other;

			ret = (this.from.equals(mother.from)&&
					this.to.equals(mother.to));
			Log.d("v","gets with ret "+ret+ "this from "+this.from+" this.to "+this.to);
			return ret;
		}

	public Cord getFromCord() {
			return from;
	}

	@Override
	public String toString() {
			return "FROM "+from.toString()+" TO: "+to.toString();
	}
}

