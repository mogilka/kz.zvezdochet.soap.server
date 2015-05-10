package kz.zvezdochet.soap.server;

import javax.jws.WebMethod;
import javax.jws.WebService;

import swisseph.SweConst;
import swisseph.SweDate;
import swisseph.SwissEph;
import swisseph.SwissLib;

/**
 * Класс, предоставляющий методы, доступные через веб-сервис
 * @author nataly
 *
 */
@WebService
public class Calculator {

	/**
	 * Расчет конфигурации гороскопа
	 * @param iyear год
	 * @param imonth месяц
	 * @param iday день
	 * @param ihour час
	 * @param imin минута
	 * @param isec секунда
	 * @param dzone часовой пояс
	 * @param dlat широта
	 * @param dlon долгота
	 * @param hstype код системы астрологических домов
	 */
	@WebMethod
	public Configuration calculate(int iyear, int imonth, int iday, 
			int ihour, int imin, int isec, 
			double dzone, double dlat, double dlon, String hstype) {
    	Configuration configuration = new Configuration();
    	try {
	  		//обрабатываем координаты места
	  		if (0 == dlat && 0 == dlon)
	  			dlat = 53.45; //по умолчанию Гринвич
	  		int ilondeg, ilonmin, ilonsec, ilatdeg, ilatmin, ilatsec;
	  		ilondeg = CalcUtil.trunc(Math.abs(dlon));
	  		ilonmin = CalcUtil.trunc(Math.abs(dlon) - ilondeg) * 100;
	  		ilonsec = 0;
	  		ilatdeg = CalcUtil.trunc(Math.abs(dlat));
	  		ilatmin = CalcUtil.trunc(Math.abs(dlat) - ilatdeg) * 100;
	  		ilatsec = 0;

	  	  	SwissEph sweph = new SwissEph();
			sweph.swe_set_topo(dlon, dlat, 0);
	  		long iflag = SweConst.SEFLG_SIDEREAL | SweConst.SEFLG_SPEED | SweConst.SEFLG_TRUEPOS | SweConst.SEFLG_TOPOCTR;
	  	  	sweph.swe_set_ephe_path("WEB-INF/lib/ephe");
	  	  	sweph.swe_set_sid_mode(SweConst.SE_SIDM_DJWHAL_KHUL, 0, 0);

	  		//обрабатываем время
	  		double timing = (double)ihour;
	  		if (dzone < 0)
	  			timing -= dzone;
	  		else {
	  			if (timing >= dzone)
	  				timing -= dzone;
	  			else
	  				timing = timing + 24 - dzone;
	  		}
	  		if (timing >= 24)
	  			timing -= 24;
	  		ihour = (int)Math.round(timing / 1);

	  		//обрабатываем дату
	  		double tjd, tjdet, tjdut, tsid, armc, dhour, deltat;
	  		@SuppressWarnings("unused")
			double eps_true, nut_long, glon, glat;
	  		dhour = ihour + imin / 60.0 + isec / 3600.0;
	  		tjd = SweDate.getJulDay(iyear, imonth, iday, dhour, true);
	  		deltat = SweDate.getDeltaT(tjd);
	  		//Universal Time
	  		tjdut = tjd;
	  		tjdet = tjd + deltat;
	  		
	  		//расчёт эфемерид планет
	  		@SuppressWarnings("unused")
			long rflag;
	  		double[] pcoords = new double[15];
	  		double[] xx = new double[6];
	  		char[] serr = new char[256];
	  		StringBuffer sb = new StringBuffer(new String(serr));
	  		int[] pindexes = getPlanetIndexes();
	  		Planet[] planets = getPlanets();
	  		for (int i = 0; i < pindexes.length; i++) {
	  		    rflag = sweph.swe_calc(tjdet, pindexes[i], (int)iflag, xx, sb);
	  		    pcoords[i] = xx[0];
	  		    int n = constToPlanet(i);
	  		    if (n >= 0)
	  		    	planets[n].coord = pcoords[i];
	  		    planets[n].retrograde = xx[3] < 0;
	  		}
	  		//рассчитываем координату Кету по значению Раху
	  		if (Math.abs(pcoords[10]) > 180)
	  			planets[3].coord = pcoords[10] - 180;
	  		else
	  			planets[3].coord = pcoords[10] + 180;
	  		configuration.setPlanets(planets);
	
	  		//расчёт куспидов домов
	  		//{ for houses: ecliptic obliquity and nutation }
	  		rflag = sweph.swe_calc(tjdet, SweConst.SE_ECL_NUT, 0, xx, sb);
	  		eps_true = xx[0];
	  		nut_long = xx[2];
	  		//{ geographic position }
	  		glon = ilondeg + ilonmin / 60.0 + ilonsec / 3600.0;
	  		if (dlon < 0) glon = -glon;
	  		glat = ilatdeg + ilatmin / 60.0 + ilatsec / 3600.0;
	  		if (dlat < 0) glat = -glat;
	  		//{ sidereal time }
	  		tsid = new SwissLib().swe_sidtime(tjdut);
	  		tsid = tsid + glon / 15;
	  		armc = tsid * 15;
	  		//{ house method }
	  		double[] ascmc = new double[10];
	  		double[] hcusps = new double[13];
	  		//по умолчанию используем систему Плацидуса
	  		//if (null == hstype) hstype = 'P';
	  		sweph.swe_houses(tjdut, SweConst.SEFLG_SIDEREAL, glat, glon, hstype.charAt(0), hcusps, ascmc);
	  		for (int i = 0; i < 13; i++)
	  			hcusps[i] = hcusps[i];
	  		House[] houses = calcHouseParts(hcusps);
	  		configuration.setHouses(houses);
	  		sweph.swe_close();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}
        return configuration;
	}

  	/**
  	 * Метод, возвращающий массив планет по Швейцарским эфемеридам
  	 * @return массив индексов планет
  	 */
  	private int[] getPlanetIndexes() {
  		int[] list = new int[15];
  		for (int i = SweConst.SE_SUN; i < SweConst.SE_MEAN_NODE; i++)
  			list[i] = i;
  		list[10] = SweConst.SE_TRUE_NODE;
  		list[11] = SweConst.SE_MEAN_APOG;
  		list[12] = SweConst.SE_CHIRON;
  		list[13] = SweConst.SE_WHITE_MOON;
  		list[14] = SweConst.SE_PROSERPINA;
  		return list;
  	}

  	/**
  	 * Поиск соответствия планет Швейцарских эфемерид
  	 * с их эквивалентами в данной программе
  	 * @param i индекс планеты в Швейцарских эфемеридах
  	 * @return индекс планеты в системе
  	 */
  	private int constToPlanet(int i) {
  		switch(i) {
  		case 0: case 1: return i;
  		case 2: case 3: case 4: return i + 2;
  		case 5: case 6: return i + 4;
  		case 7: case 8: case 9: return i + 5;
  		case 10: return 2;
  		case 11: return 8;
  		case 12: return 11;
  		case 13: return 7;
  		case 14: return 15;
  		default: return -1;
  		}
  	}

	private Planet[] getPlanets() {
		String[] names = {
			"Sun", "Moon", "Rakhu", "Kethu", "Mercury", "Venus", "Mars", 
			"Selena", "Lilith", "Jupiter", "Saturn", "Chiron", "Uranus", 
			"Neptune", "Pluto", "Proserpina" };
		Planet[] planets = new Planet[names.length];
		for (int i = 0; i < names.length; i++)
			planets[i] = new Planet(names[i], 0.0);
		return planets;
	}

  	/**
  	 * Расчет третей домов
  	 * @param hcoords массив вычисленных основных домов
  	 */
  	private House[] calcHouseParts(double[] hcoords) {
  		House[] houses = getHouses();
		try {
	  		byte multiple;
	  		//шерстим трети домов, минуя основные куспиды
	  		for (int j = 1; j < 37; j++) {
	  			House h = houses[j - 1];
	  			int i = CalcUtil.trunc((j + 2) / 3);
	  			if (h.isMain())
	  	  			h.coord = hcoords[i];
	  			else {
	  				double one = CalcUtil.degToDec(hcoords[i]);
	  				if (i == 12) i = 0;
	  				double two = CalcUtil.degToDec(hcoords[i + 1]);
	  				if ((one > 300) && (one < 360) && (two < 60))
	  					two = two + 360;
	  				//вычисляем и сохраняем значения вершин третей дома
	  				//учитываем, что индекс последней трети всегда кратен трем
	  				if (j % 3 == 0) 
	  					multiple = 2; 
	  				else 
	  					multiple = 1;
	  				double res = multiple * ((two - one) / 3) + one;
	  				if (res > 360) 
	  					res = CalcUtil.decToDeg(res - 360); 
	  				else 
	  					res = CalcUtil.decToDeg(res);
	  	  			h.coord = res;
	  			}
	  		}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return houses;
  	}

	private House[] getHouses() {
		String[] names = {
			"I", "I_2", "I_3", "II", "II_2", "II_3", "III", "III_2", "III_3", 
			"IV", "IV_2", "IV_3", "V", "V_2", "V_3", "VI", "VI_2", "VI_3", 
			"VII", "VII_2", "VII_3", "VIII", "VIII_2", "VIII_3", 
			"IX", "IX_2", "IX_3", "X", "X_2", "X_3", "XI", "XI_2", "XI_3", 
			"XII", "XII_2", "XII_3" };
		House[] houses = new House[names.length];
		for (int i = 0; i < names.length; i++)
			houses[i] = new House(names[i], 0.0, i + 1);
		return houses;
	}
}
