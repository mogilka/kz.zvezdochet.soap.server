package kz.zvezdochet.soap.server;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Класс, представляющий Планету
 * @author Nataly
 * 
 * @see SkyPoint Точка небесной сферы
 */
@XmlRootElement(namespace="planet")
public class Planet extends SkyPoint {
	
	/**
	 * Признак ретроградности
	 */
	@XmlElement() boolean retrograde = false;
	
	public Planet() {
		super();
	}

	public Planet(String code, double coord) {
		super(code, coord);
	}
}
