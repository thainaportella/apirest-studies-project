package br.com.tpprojects.personrequestapirest.integrationtests.vo.wrappers;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

@XmlRootElement
public class WrapperBookVO implements Serializable {

    private static final long serialVersionUID = 1L;
    @JsonProperty("_embedded")
    private BookEmbeddedVO embedded;

    public WrapperBookVO() {}

    public BookEmbeddedVO getEmbedded() {
        return embedded;
    }

    public void setEmbedded(BookEmbeddedVO embedded) {
        this.embedded = embedded;
    }
}
