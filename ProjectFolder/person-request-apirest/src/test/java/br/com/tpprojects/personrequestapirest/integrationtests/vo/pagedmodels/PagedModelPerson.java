package br.com.tpprojects.personrequestapirest.integrationtests.vo.pagedmodels;

import br.com.tpprojects.personrequestapirest.integrationtests.vo.PersonVO;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlElement;

import java.util.List;

@XmlRootElement
public class PagedModelPerson {
    @XmlElement(name = "content")
    private List<PersonVO> content;

    public PagedModelPerson() {
    }

    public List<PersonVO> getContent() {
        return content;
    }

    public void setContent(List<PersonVO> content) {
        this.content = content;
    }
}
