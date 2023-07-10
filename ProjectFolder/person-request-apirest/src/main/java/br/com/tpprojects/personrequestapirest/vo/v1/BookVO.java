package br.com.tpprojects.personrequestapirest.vo.v1;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@JsonPropertyOrder({"id", "author", "launch_date", "price", "title"})
public class BookVO extends RepresentationModel<BookVO> implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    @Mapping("id")
    private Integer key;
    private String author;
    private Date launchDate;
    private Double price;
    private String title;

    public BookVO(Integer key, String author, Date launchDate, Double price, String title) {
        this.key = key;
        this.author = author;
        this.launchDate = launchDate;
        this.price = price;
        this.title = title;
    }

    public BookVO() {
    }

    public Integer getKey() {
        return key;
    }
    public void setKey(Integer key) {
        this.key = key;
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
        return Objects.equals(getKey(), bookVO.getKey()) && Objects.equals(getAuthor(), bookVO.getAuthor()) && Objects.equals(getLaunchDate(), bookVO.getLaunchDate()) && Objects.equals(getPrice(), bookVO.getPrice()) && Objects.equals(getTitle(), bookVO.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getAuthor(), getLaunchDate(), getPrice(), getTitle());
    }
}
