package kz.zvezdochet.soap.server;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Расчётная конфигурация
 * @author Nataly Didenko
 *
 */
@XmlRootElement(namespace="configuration")
public class Configuration {
	
	public Configuration() {
		
	}
	
	Planet[] planets = null;
	House[] houses = null;
	
	public Planet[] getPlanets() {
		return planets;
	}
	public void setPlanets(Planet[] planets) {
		this.planets = planets;
	}
	public House[] getHouses() {
		return houses;
	}
	public void setHouses(House[] houses) {
		this.houses = houses;
	}
}
