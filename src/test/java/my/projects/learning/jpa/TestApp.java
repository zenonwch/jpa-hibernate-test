package my.projects.learning.jpa;

import my.projects.learning.jpa.domain.Category;
import my.projects.learning.jpa.domain.Topic;
import my.projects.learning.jpa.util.logging.CustomJavaLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestApp {
    private EntityManager em;
    private EntityTransaction trx;

    @BeforeAll
    static void init() {
        CustomJavaLogger.readLoggerConfigurationPropertiesFromResource("logger.properties");
    }

    @BeforeEach
    void setUp() {
        final EntityManagerFactory emf = Persistence.createEntityManagerFactory("JpaHibernateTest");
        em = emf.createEntityManager();
        trx = em.getTransaction();
        trx.begin();
    }

    @AfterEach
    void tearDown() {
        if (trx.isActive()) {
            trx.commit();
        }
        em.getEntityManagerFactory().close();
    }

    @Test
    void startHibernate() {
        assertNotNull(em);
    }

    @Test
    void persistCategory() {
        final Category category = new Category("New Category");
        em.persist(category);
        // sync entity: PersistenceContext -> DB
        em.flush();
        final Category savedCategory = em.find(Category.class, 1L);
        assertEquals(category, savedCategory);
    }

    @Test
    void lifeSycle() {
        // 1. New | Transient
        final Category category = new Category("New Category");

        // 2. Managed | Persistent
        em.persist(category);

        // 3. Transaction closed, all entities detached from context
        em.getTransaction().commit();
        em.getTransaction().begin();

        // 4. Managed
        final Category saved = em.find(Category.class, 1L);

        // 5. Detached
        em.detach(category);
        em.detach(saved);

        // 6. managed (copy of category) - Managed, category - remains Detached
        final Category managed = em.merge(category);

        // 7. Removed
        em.remove(managed);

        assertNotNull(category);
    }

    @Test
    void persistTwice_withoutFlush() {
        final Category category = new Category("New Category");
        em.persist(category);
        em.clear();
        assertThrows(PersistenceException.class, () -> em.persist(category));
    }

    @Test
    void persistTwice_withFlush() {
        final Category category = new Category("New Category");
        em.persist(category);
        em.flush();
        em.clear();
        assertThrows(PersistenceException.class, () -> em.persist(category));
    }

    @Test
    void persistCategoryWithTopic() {
        final Category category = new Category("New Category");

        final Topic topic = new Topic("New Topic");
        topic.setCategory(category);

        em.persist(category);
        em.flush();
        em.clear();
        final List<Category> savedCategories = em.createQuery("SELECT c FROM Category c", Category.class).getResultList();
        assertEquals(1, savedCategories.size());

        final Category savedCategory = em.find(Category.class, 1L);

        assertNotNull(savedCategory);
        assertNotNull(savedCategory.getTopics());
        assertEquals(1, savedCategory.getTopics().size());
        assertTrue(savedCategory.getTopics().contains(topic));
    }
}
