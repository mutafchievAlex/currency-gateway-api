package com.example.gateway.api.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen")
@JacksonXmlRootElement(localName = "error")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiErrorRepresentation {

    @JsonProperty("title")
    @JacksonXmlProperty(localName = "title")
    @NotNull
    private String title;

    @JsonProperty("detail")
    @JacksonXmlProperty(localName = "detail")
    @NotNull
    private String detail;

    @JsonProperty("status")
    @JacksonXmlProperty(localName = "status")
    @NotNull
    private Integer status;

    public ApiErrorRepresentation title(String title) {
        this.title = title;
        return this;
    }

    public ApiErrorRepresentation detail(String detail) {
        this.detail = detail;
        return this;
    }

    public ApiErrorRepresentation status(Integer status) {
        this.status = status;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ApiErrorRepresentation that)) {
            return false;
        }
        return Objects.equals(title, that.title)
                && Objects.equals(detail, that.detail)
                && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, detail, status);
    }

    @Override
    public String toString() {
        return "ApiErrorRepresentation{" +
                "title='" + title + '\'' +
                ", detail='" + detail + '\'' +
                ", status=" + status +
                '}';
    }
}
