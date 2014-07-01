package kz.zvezdochet.soap.server;

import kz.zvezdochet.soap.server.jaxws.Calculate;
import kz.zvezdochet.soap.server.jaxws.CalculateResponse;

public class CalcAdapter {
	
	private Calculator calculator = new Calculator();

	public CalculateResponse calculate(Calculate request) {
		CalculateResponse response = null;
		try {
			int iyear = request.getArg0();
			int imonth = request.getArg1();
			int iday = request.getArg2();
			int ihour = request.getArg3();
			int imin = request.getArg4();
			int isec = request.getArg5();
			double dzone = request.getArg6();
			double dlat = request.getArg7();
			double dlon = request.getArg8();
			String hstype = request.getArg9();
			Configuration res = calculator.calculate(iyear, imonth, iday, 
				ihour, imin, isec, dzone, dlat, dlon, hstype);
		    response = new CalculateResponse();
		    response.setReturn(res);
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
	    return response;
	}
}
