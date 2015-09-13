package kz.zvezdochet.soap.server;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Класс, представляющий Астрологический дом
 * @author Nataly Didenko
 *
 */
@XmlRootElement(namespace="house")
public class House extends SkyPoint {
	
	public House() {
		super();
	}

	public House(String code, double coord, int number) {
		super(code, coord);
		this.number = number;
	}

    /**
     * Порядковый номер
     */
	@XmlElement() int number;

	/**
	 * Проверка, является ли дом основным
	 * @return <i>true</i> - если дом относится к главным куспидам
	 */
	public boolean isMain() {
  		int[] hmain = {1,4,7,10,13,16,19,22,25,28,31,34};
  		for (int i : hmain)
  			if (i == this.number) return true;
  		return false;
	}
}
