package com.example.analytics.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "page_counts")
public class PageCount {

    @Id
    private String url;
    private long count;

    protected PageCount() {}

    public PageCount(String url, long count) {
        this.url = url;
        this.count = count;
    }

    public String getUrl() { return url; }
    public long getCount() { return count; }
}
