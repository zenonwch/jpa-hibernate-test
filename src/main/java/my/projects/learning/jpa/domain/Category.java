package my.projects.learning.jpa.domain;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Category {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "topic_id")
    private Set<Topic> topics = new HashSet<>();

    public Category() {
    }

    public Category(final String title) {
        this.title = title;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        final Category category = (Category) o;

        return id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
