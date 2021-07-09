package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("itest")
@Sql(value = {
        "/sql_injection/create-user-before.sql",
        "/sql_injection/create-post-before.sql",
        "/sql_injection/create-comments-before.sql"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(value = {
        "/sql_injection/create-comments-after.sql",
        "/sql_injection/delete-tags-after.sql",
        "/sql_injection/create-post-after.sql",
        "/sql_injection/create-user-after.sql"
}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public abstract class AbstractIntegrationIT {
    /**
     * Web application context.
     */
    @Autowired
    protected WebApplicationContext ctx;

    /**
     * Mock mvc.
     */
    protected MockMvc mockMvc;

    /**
     * Object mapper.
     */
    @Autowired
    protected ObjectMapper mapper;

    /**
     * Create mock mvc.
     */
    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(ctx)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }
}
