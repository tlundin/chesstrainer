package com.teraime.chesstrainer;

public class Cord {
	public int column;
	public int row;
	
	public Cord() {}
	public Cord(int column, int row) {
		this.column = column;
		this.row = row;
	}

	public Cord(int location) {
		row = location/8;
		column = location - row*8;

	}

	public int getColumn() {
		return column;
	}
	public int getRow() {
		return row;
	}

	public void set(int z,int w) {
		column = z;
		row = w;
	}

	public boolean equals(int column, int row) {
		return this.column ==column && this.row == row;
	}

	@Override
	public boolean equals(Object other) {
		Cord mother = (Cord)other;
		return column == mother.column && row == mother.row;
	}

    @Override
	public String toString() {
		return "["+column+","+row+"]";
	}

	}
