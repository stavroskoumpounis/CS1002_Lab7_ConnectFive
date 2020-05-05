package edu.lbic.lab7.exceptions;

public class InvalidPlayerNumberException extends Exception{
	
	private static final long serialVersionUID = -2018484683416381077L;

	/**
	 * A more read friendly exceptions for trying to set an invalid number for a player
	 * @return the message stating why the exception was thrown
	 */
	@Override
	public String getMessage()
	{
		return "Invalid player number : enter 1 or 2";
	}
}