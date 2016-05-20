package com.schytd.discount.enties;

public class BussinessExcepion extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public BussinessExcepion() {
		super();
	}

	public BussinessExcepion(String detailMessage) {
		super(detailMessage);
	}

	@Override
	public String getMessage() {
		return super.getMessage();
	}
	

}
