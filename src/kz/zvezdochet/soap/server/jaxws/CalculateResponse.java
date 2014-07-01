package kz.zvezdochet.soap.server.jaxws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name = "calculateResponse", namespace = "http://server.soap.zvezdochet.kz/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "calculateResponse", namespace = "http://server.soap.zvezdochet.kz/")
public class CalculateResponse {

    @XmlElement(name = "return", namespace = "")
    private kz.zvezdochet.soap.server.Configuration _return;

    /**
     * 
     * @return
     *     returns Configuration
     */
    public kz.zvezdochet.soap.server.Configuration getReturn() {
        return this._return;
    }

    /**
     * 
     * @param _return
     *     the value for the _return property
     */
    public void setReturn(kz.zvezdochet.soap.server.Configuration _return) {
        this._return = _return;
    }

}
