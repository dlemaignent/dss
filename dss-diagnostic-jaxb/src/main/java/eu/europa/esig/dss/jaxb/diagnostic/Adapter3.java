//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.3.0 
// See <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.09.25 at 10:21:52 AM CEST 
//


package eu.europa.esig.dss.jaxb.diagnostic;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import eu.europa.esig.dss.validation.TimestampedObjectType;

public class Adapter3
    extends XmlAdapter<String, TimestampedObjectType>
{


    public TimestampedObjectType unmarshal(String value) {
        return (eu.europa.esig.dss.jaxb.parsers.TimestampedObjectTypeParser.parse(value));
    }

    public String marshal(TimestampedObjectType value) {
        return (eu.europa.esig.dss.jaxb.parsers.TimestampedObjectTypeParser.print(value));
    }

}
