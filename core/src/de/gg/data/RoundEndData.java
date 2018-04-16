package de.gg.data;

/**
 * This data is sent after a round ends to setup the next round. It contains all
 * end round calculations i.e. the salary costs, tuition effects, etc.
 */
public class RoundEndData {

	private int openingHourNextDay;
	private int closingHourNextDay;

	/**
	 * @return the hour the market work begins on the next day.
	 */
	public int getOpeningHourNextDay() {
		return openingHourNextDay;
	}

	public void setOpeningHourNextDay(int openingHourNextDay) {
		this.openingHourNextDay = openingHourNextDay;
	}

	/**
	 * @return the hour the market work ends on the next day.
	 */
	public int getClosingHourNextDay() {
		return closingHourNextDay;
	}

	public void setClosingHourNextDay(int closingHourNextDay) {
		this.closingHourNextDay = closingHourNextDay;
	}

}
