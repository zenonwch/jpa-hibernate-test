package my.projects.learning.jpa.domain;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Topic {
    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public Topic() {
    }

    public Topic(final String title) {
        this.title = title;
    }

    public void setCategory(final Category category) {
        category.getTopics().add(this);
        this.category = category;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Topic)) return false;

        final Topic topic = (Topic) o;

        return id.equals(topic.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
