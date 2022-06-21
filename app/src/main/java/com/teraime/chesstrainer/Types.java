package com.teraime.chesstrainer;

public abstract class Types {

	
	 public static class MatePuzzle {

		 public boolean whiteToMove;
		 public String moves,fen;
		 
		 public MatePuzzle(String moves,String fen,boolean whiteToMove) {
			 this.whiteToMove=whiteToMove;
			 this.moves = moves;
			 this.fen = fen;
		 }
		 
	}
	 


	public static class TacticProblem {


		 public float rd;
		 public float rating;
		 public String board;
		 public String moves;
		 public boolean whiteToMove;
		 public Progressor.Difficulty difficulty;
		
		 public TacticProblem(float rd, float rating, String board, String moves,
							  boolean whiteToMove, Progressor.Difficulty difficulty) {
			this.rd = rd;
			this.rating = rating;
			this.board = board;
			this.moves = moves;
			this.whiteToMove = whiteToMove;
			this.difficulty = difficulty;
		}
	 
	 }
	 

	 

	public static enum ProblemType {
		TACTICS,GAME,
	}
	
	
	

	
	//stat counters for tactic problem.
	public static class TacticResult {
		
		/**
		 * @param score the score to set
		 */
		public void setScore(int score) {
			this.score = score;
		}

		/**
		 * @param greens the greens to set
		 */
		public void setGreens(int greens) {
			this.greens = greens;
		}

		/**
		 * @param reds the reds to set
		 */
		public void setReds(int reds) {
			this.reds = reds;
		}

		/**
		 * @param yellows the yellows to set
		 */
		public void setYellows(int yellows) {
			this.yellows = yellows;
		}

		/**
		 * @param fails the fails to set
		 */
		public void setFails(int fails) {
			this.fails = fails;
		}

		/**
		 * @param woos the woos to set
		 */
		public void setWoos(int woos) {
			this.woos = woos;
		}

		/**
		 * @param greenSequence the greenSequence to set
		 */
		public void setGreenSequence(int greenSequence) {
			this.greenSequence = greenSequence;
		}

		/**
		 * @return the score
		 */
		public int getScore() {
			return score;
		}

		/**
		 * @return the greens
		 */
		public int getGreens() {
			return greens;
		}

		/**
		 * @return the reds
		 */
		public int getReds() {
			return reds;
		}

		/**
		 * @return the yellows
		 */
		public int getYellows() {
			return yellows;
		}

		/**
		 * @return the fails
		 */
		public int getFails() {
			return fails;
		}

		/**
		 * @return the woos
		 */
		public int getWoos() {
			return woos;
		}

		/**
		 * @return the greenSequence
		 */
		public int getGreenSequence() {
			return greenSequence;
		}

		public TacticResult() {
			score=greens=reds=yellows=fails=woos=greenSequence=0;
		}
		
		private int score,greens,reds,yellows,fails,woos,greenSequence;
	}
}
