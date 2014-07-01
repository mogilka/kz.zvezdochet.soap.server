package kz.zvezdochet.soap.server;

import javax.xml.bind.annotation.XmlElement;

/**
 * Класс, представляющий Точку Небесной сферы
 * @author Nataly
 *
 */
public abstract class SkyPoint {
	
	public SkyPoint() {}

	public SkyPoint(String code, double coord) {
		this.code = code;
		this.coord = coord;
	}
	
	/**
	 * Координата
	 */
	@XmlElement() double coord = 0.0;

    /**
     * Код
     */
	@XmlElement() String code;
}
