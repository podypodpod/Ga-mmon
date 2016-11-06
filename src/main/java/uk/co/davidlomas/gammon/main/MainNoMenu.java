package uk.co.davidlomas.gammon.main;

import org.apache.log4j.Logger;

import uk.co.davidlomas.gammon.genes.StartGa;

public class MainNoMenu {
	final static Logger logger = Logger.getLogger(MainNoMenu.class);

	public static void main(final String args[]) {
		logger.trace("Starting Ga-mmon");
		new StartGa();
	}
}