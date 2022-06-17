package com.teraime.chesstrainer;

public class Position {
	public int x;
	public int y;
	
	public Position() {}
	public Position(int column, int row) {
		x = column;
		y = row;
	}

	public int getColumn() {
		return x;
	}
	public int getRow() {
		return y;
	}

	public void set(int z,int w) {
		x = z;
		y = w;
	}

	public boolean equals(int column, int row) {
		return x==column && y == row;
	}



	}
