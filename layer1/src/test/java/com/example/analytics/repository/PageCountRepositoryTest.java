package com.example.analytics.repository;

import com.example.analytics.model.PageCount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class PageCountRepositoryTest {

    @Autowired
    PageCountRepository repo;

    @Test
    void insertsNewUrl() {
        repo.upsertCount("/pricing");

        assertThat(repo.findById("/pricing"))
            .isPresent()
            .hasValueSatisfying(pc -> assertThat(pc.getCount()).isEqualTo(1));
    }

    @Test
    void incrementsExistingUrl() {
        repo.upsertCount("/pricing");
        repo.upsertCount("/pricing");
        repo.upsertCount("/pricing");

        assertThat(repo.findById("/pricing").map(PageCount::getCount)).hasValue(3L);
    }

    @Test
    void tracksMultipleUrlsIndependently() {
        repo.upsertCount("/pricing");
        repo.upsertCount("/features");
        repo.upsertCount("/pricing");

        assertThat(repo.findById("/pricing").map(PageCount::getCount)).hasValue(2L);
        assertThat(repo.findById("/features").map(PageCount::getCount)).hasValue(1L);
    }
}
