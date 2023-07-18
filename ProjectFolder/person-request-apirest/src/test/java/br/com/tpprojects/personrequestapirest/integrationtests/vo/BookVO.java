package br.com.tpprojects.personrequestapirest.integrationtests.vo;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@XmlRootElement
public class BookVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;
    private String author;
    private Date launchDate;
    private Double price;
    private String title;

    public BookVO() {
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public Date getLaunchDate() {
        return launchDate;
    }
    public void setLaunchDate(Date launchDate) {
        this.launchDate = launchDate;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookVO bookVO = (BookVO) o;
        return Objects.equals(getId(), bookVO.getId()) && Objects.equals(getAuthor(), bookVO.getAuthor()) && Objects.equals(getLaunchDate(), bookVO.getLaunchDate()) && Objects.equals(getPrice(), bookVO.getPrice()) && Objects.equals(getTitle(), bookVO.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getAuthor(), getLaunchDate(), getPrice(), getTitle());
    }
}
