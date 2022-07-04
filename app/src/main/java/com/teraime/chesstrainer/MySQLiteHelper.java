package com.teraime.chesstrainer;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String COLUMN_ID = "_id";
	private static final String DB_NAME = "chess.db";
	private static String DB_PATH=null;

	private SQLiteDatabase myDataBase;

	private final Context myContext;

	public MySQLiteHelper(Context context) {
		super(context, DB_NAME, null, 1);
		myContext=context;

		if (android.os.Build.VERSION.SDK_INT >= 17)
			DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
		else
			DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
		Log.d("path", DB_PATH);
	}


	public void createDataBase() throws IOException {
		boolean dbExist = checkDataBase();
		Log.d("db","dbExist is "+dbExist);
		if (!dbExist) {
		  copyDatabase();
		}
	}

	private boolean checkDataBase() {
		String myPath = DB_PATH + DB_NAME;
		File dbFile = myContext.getDatabasePath(myPath);
		return (dbFile.exists());
	}

	private void copyDatabase() {

		InputStream myInput;
		OutputStream outStream;
		try {
			myInput = myContext.getAssets().open(DB_NAME);
			String file = DB_PATH + DB_NAME;
			outStream = new FileOutputStream(file);

			byte[] buffer = new byte[1024];
			int length = 0;
			while ((length = myInput.read(buffer)) >= 0) {
				outStream.write(buffer, 0, length);
			}
			outStream.flush();
			myInput.close();
			outStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void openDataBase() throws SQLException {
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
	}

	@Override
	public synchronized void close() {
		if (myDataBase !=null ) 
			myDataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}


	public Types.TacticProblem getTacticProblemZ(int minRating, int maxRating) {
		float rd = 12f,rating=1331.2f;
		String board = "rrxxxxkxpxqbnxxPRxxpxxxpxxxPxpxxxBPxpbxxxxxxPxxxQxxNxxBPxRxxxxKx";
		String moves="15:Px:7:xP:,1:rx:33:Br:,33:rR:57:Rx:,10:qx:26:xq:,";
		boolean whiteToMove=true;
		return new Types.TacticProblem(rd,rating,board,moves,whiteToMove, Progressor.Difficulty.normal);

	}

	public List<Types.TacticProblem> getTacticProblems(int minRating, int maxRating) {
		List<Types.TacticProblem> ret = new LinkedList<Types.TacticProblem>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c;
		int rd, rating;
		String board,moves,tmp;
		boolean whiteToMove;
		c = db.rawQuery("SELECT * from tactic WHERE rating >"+minRating+" AND rating <"+maxRating+" ORDER BY rating", null);
		c.moveToFirst();
		while(!c.isAfterLast()) {
			rd = c.getInt(1);
			rating = c.getInt(2);
			board = c.getString(3);
			moves = c.getString(4);
			tmp = c.getString(5);
			whiteToMove = false;
			if (tmp != null && tmp.equals("w"))
				whiteToMove = true;
			ret.add(new Types.TacticProblem(rd, rating, board, moves, whiteToMove, Progressor.Difficulty.normal));
			c.moveToNext();
		}
		return ret;
	}
	public List<Types.TacticProblem> getTacticProblems(int size, int minRating, int maxRating) {
		//openDataBase();
		List<Types.TacticProblem> ret = new LinkedList<Types.TacticProblem>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c;
		Cursor d;
		Cursor e;
		//if (maxRating > 0)
		//	c = db.rawQuery("SELECT * from tactic WHERE rating >"+minRating+" AND rating <"+maxRating+" ORDER BY RANDOM() LIMIT "+size, null);

		c = db.rawQuery("SELECT * from tactic WHERE rating >"+minRating+" ORDER BY rating LIMIT "+size, null);
		d = db.rawQuery("SELECT * from tactic WHERE rating >"+(minRating+100)+" ORDER BY rating LIMIT "+size/10, null);
		e = db.rawQuery("SELECT * from tactic WHERE rating >"+(minRating+200)+" ORDER BY rating LIMIT "+size/10, null);
		c.moveToFirst();
		d.moveToFirst();
		e.moveToFirst();
		int rd, rating;
		String board,moves,tmp;
		boolean whiteToMove;
		Random r = new Random();
		int lvlC = 1;
		while(!c.isAfterLast()) {
			if (lvlC%5==0) {
				if (r.nextBoolean() && !d.isAfterLast()) {
					rd = d.getInt(1);
					rating = d.getInt(2);
					board = d.getString(3);
					moves = d.getString(4);
					tmp = d.getString(5);

					whiteToMove = false;
					if (tmp != null && tmp.equals("w"))
						whiteToMove = true;
					ret.add(new Types.TacticProblem(rd, rating, board, moves, whiteToMove, Progressor.Difficulty.hard));
					d.moveToNext();
				} else {
					if (!e.isAfterLast()) {
						rd = e.getInt(1);
						rating = e.getInt(2);
						board = e.getString(3);
						moves = e.getString(4);
						tmp = e.getString(5);
						whiteToMove = false;
						if (tmp != null && tmp.equals("w"))
							whiteToMove = true;
						ret.add(new Types.TacticProblem(rd, rating, board, moves, whiteToMove, Progressor.Difficulty.nightmare));
						e.moveToNext();
					}
				}
			} else {
				rd = c.getInt(1);
				rating = c.getInt(2);
				board = c.getString(3);
				moves = c.getString(4);
				tmp = c.getString(5);
				whiteToMove=false;
				if (tmp!=null && tmp.equals("w"))
					whiteToMove = true;
				ret.add(new Types.TacticProblem(rd,rating,board,moves,whiteToMove,Progressor.Difficulty.normal));
				c.moveToNext();
			}
			lvlC++;
		}
		return ret;
	}


	public Types.TacticProblem getTacticProblem(float lastMin, int plusLevel) {
		SQLiteDatabase db = this.getReadableDatabase();
		//Cursor c = db.rawQuery("SELECT * from tactic WHERE rating >"+min+" AND rating <"+max+" ORDER BY RANDOM() LIMIT 1", null);
		Cursor c = db.rawQuery("SELECT * from tactic WHERE rating >"+lastMin+" ORDER BY rating LIMIT "+plusLevel, null);
		c.moveToFirst();
		for (int i=0;i<c.getCount();i++) {
			Log.d("v","int rating for "+i+" is "+c.getFloat(2));
			c.moveToNext();
		}
		c.moveToLast();
		float rd, rating;
		String board,moves,tmp;
		boolean whiteToMove;
		rd = c.getInt(1);
		rating = c.getFloat(2);
		board = c.getString(3);
		moves = c.getString(4);
		tmp = c.getString(5);
		whiteToMove=false;
		Log.d("v","DB returns "+rating+" from "+c.getCount()+" results. Pluslevel is "+plusLevel);
		if (tmp!=null && tmp.equals("w"))
			whiteToMove = true;
		return new Types.TacticProblem(rd,rating,board,moves,whiteToMove, Progressor.Difficulty.normal);

	}

	public Types.MatePuzzle getMatePuzzle(int nMoves) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery("SELECT * from mate WHERE matein ="+nMoves+" ORDER BY RANDOM() LIMIT 1", null);
		c.moveToFirst();
		String moves,fen;
		boolean whiteToMove;
		moves = c.getString(2);
		fen = c.getString(3);
		
		//whiteToMove=true;
		//moves = "1. Qg7";
		//fen = "2N2B2/r1p2p2/1P4RQ/3kpP1N/2p3R1/1n6/r1n1P2K/1B6 w - - 0 1]";

		
		if (moves!=null && moves.length()>0 && fen !=null) {
			whiteToMove = !(moves.startsWith("1.."));
			return new Types.MatePuzzle(moves,fen,whiteToMove);
		}
		return null;
		

	}

} 
