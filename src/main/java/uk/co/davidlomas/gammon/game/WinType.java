package uk.co.davidlomas.gammon.game;

/**
 * How a game was won, which sets how many points it's worth. It depends on how far behind the loser was when the
 * winner bore off their last piece.
 */
public enum WinType {

	/**
	 * The loser had borne off at least one piece
	 */
	SINGLE(1, "a single game"),

	/**
	 * The loser hadn't borne off any pieces
	 */
	GAMMON(2, "a gammon"),

	/**
	 * The loser hadn't borne off any pieces, and still had one on the bar or in the winner's home quarter
	 */
	BACKGAMMON(3, "a backgammon"),

	/**
	 * Nobody won: a game between two AIs that went on too long and was cut short. Worth nothing to either, so a
	 * player can't gain by keeping a game going rather than finishing it
	 */
	DRAW(0, "a draw");

	private final int points;
	private final String description;

	WinType(final int points, final String description) {
		this.points = points;
		this.description = description;
	}

	public int getPoints() {
		return points;
	}

	/**
	 * @return what the win is called and what it's worth, such as "a gammon, worth 2 points"
	 */
	public String describe() {
		return description + ", worth " + points + (points == 1 ? " point" : " points");
	}
}
