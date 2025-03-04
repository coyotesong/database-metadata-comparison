package com.coyotesong.database.formatters;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class MarkdownFormatterTest {

    /*
        DOCKER_REPOS.put("ibmcom/db2", "https://icr.io/db2_community/db2");
        MAVEN_REPOS.put("com.ibm.db2.jcc.DB2Driver", Collections.singletonMap("com.ibm.db2", "jcc")); // 11.5.9.0
     */

    /*
    @Test
    public void testGetDockerRepo() {
        final MarkdownFormatter formatter = new MarkdownFormatter();
        for (String name : AbstractOutputFormatter.DOCKER_REPOS.keySet()) {
            final String url = formatter.getDockerRepo(name + ":1");
            assertThat(url, equalTo(AbstractOutputFormatter.DOCKER_REPOS.get(name)));
        }

        // check for miss...
    }

    @Test
    public void testGetMavenRepo() {
        final MarkdownFormatter formatter = new MarkdownFormatter();
        for (String driverName : AbstractOutputFormatter.MAVEN_REPOS.keySet()) {
            //final String url = formatter.getMavenRepo(name);
            //assertThat(url, equalTo(AbstractOutputFormatter.DOCKER_REPOS.get(unversionedName)));
        }
    }
     */
}
