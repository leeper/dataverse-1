package edu.harvard.iq.dataverse.authorization.providers.shib;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class ShibUtilTest {

    public ShibUtilTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getDisplayNameFromDiscoFeed method, of class ShibUtil.
     */
    @Test
    public void testGetDisplayNameFromDiscoFeed() {
//        System.out.println("getDisplayNameFromDiscoFeed");

        String discoFeedExample = null;
        try {
            discoFeedExample = new String(Files.readAllBytes(Paths.get("src/main/webapp/resources/dev/sample-shib-identities.json")));
        } catch (IOException ex) {
            Logger.getLogger(ShibUtilTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        ShibUtil shibUtil = new ShibUtil();

        String testShib = shibUtil.getDisplayNameFromDiscoFeed("https://idp.testshib.org/idp/shibboleth", discoFeedExample);
        assertEquals("TestShib Test IdP", testShib);

        String harvardStage = shibUtil.getDisplayNameFromDiscoFeed("https://stage.fed.huit.harvard.edu/idp/shibboleth", discoFeedExample);
        assertEquals("Harvard Test IdP", harvardStage);

        String minimal = shibUtil.getDisplayNameFromDiscoFeed("https://minimal.com/shibboleth", discoFeedExample);
        assertEquals(null, minimal);

        String unknown = shibUtil.getDisplayNameFromDiscoFeed("https://nosuchdomain.com/idp/shibboleth", discoFeedExample);
        assertEquals(null, unknown);

        String searchForNull = shibUtil.getDisplayNameFromDiscoFeed(null, discoFeedExample);
        assertEquals(null, searchForNull);

        /**
         * @todo Is it an error to pass a null discoFeed?
         */
//        String nullDiscoFeed = shibUtil.getDisplayNameFromDiscoFeed("https://idp.testshib.org/idp/shibboleth", null);
//        assertEquals(null, nullDiscoFeed);
        /**
         * @todo Is it an error to pass junk (non-JSON) as a discoFeed?
         */
//        String unparseAbleDiscoFeed = shibUtil.getDisplayNameFromDiscoFeed("https://idp.testshib.org/idp/shibboleth", "unparseAbleAsJson");
//        assertEquals(null, unparseAbleDiscoFeed);
    }

    @Test
    public void testFindBestFirstAndLastName() {

        ShibUserNameFields expected1 = new ShibUserNameFields("John", "Harvard");
        ShibUserNameFields actual1 = ShibUtil.findBestFirstAndLastName("John", "Harvard", null);
        assertEquals(expected1.getFirstName(), actual1.getFirstName());
        assertEquals(expected1.getLastName(), actual1.getLastName());

        ShibUserNameFields expected2 = new ShibUserNameFields("Guido", "van Rossum");
        ShibUserNameFields actual2 = ShibUtil.findBestFirstAndLastName("Guido", "van Rossum", null);
        assertEquals(expected2.getFirstName(), actual2.getFirstName());
        assertEquals(expected2.getLastName(), actual2.getLastName());

        ShibUserNameFields expected3 = new ShibUserNameFields("Philip Seymour", "Hoffman");
        ShibUserNameFields actual3 = ShibUtil.findBestFirstAndLastName("Philip Seymour", "Hoffman", "Philip Seymour Hoffman");
        assertEquals(expected3.getFirstName(), actual3.getFirstName());
        assertEquals(expected3.getLastName(), actual3.getLastName());

        ShibUserNameFields expected4 = new ShibUserNameFields("Edward", "Cummings");
        ShibUserNameFields actual4 = ShibUtil.findBestFirstAndLastName("Edward;e e", "Cummings", null);
        assertEquals(expected4.getFirstName(), actual4.getFirstName());
        assertEquals(expected4.getLastName(), actual4.getLastName());

        ShibUserNameFields expected5 = new ShibUserNameFields("Edward", "Cummings");
        ShibUserNameFields actual5 = ShibUtil.findBestFirstAndLastName("Edward;e e", "Cummings", "e e cummings");
        assertEquals(expected5.getFirstName(), actual5.getFirstName());
        assertEquals(expected5.getLastName(), actual5.getLastName());

        ShibUserNameFields expected6 = new ShibUserNameFields("Anthony", "Stark");
        ShibUserNameFields actual6 = ShibUtil.findBestFirstAndLastName("Tony;Anthony", "Stark", null);
        assertEquals(expected6.getFirstName(), actual6.getFirstName());
        assertEquals(expected6.getLastName(), actual6.getLastName());

        ShibUserNameFields expected7 = new ShibUserNameFields("Anthony", "Stark");
        ShibUserNameFields actual7 = ShibUtil.findBestFirstAndLastName("Anthony;Tony", "Stark", null);
        assertEquals(expected7.getFirstName(), actual7.getFirstName());
        assertEquals(expected7.getLastName(), actual7.getLastName());

        ShibUserNameFields expected8 = new ShibUserNameFields("Antoni", "Gaudí");
        ShibUserNameFields actual8 = ShibUtil.findBestFirstAndLastName("Antoni", "Gaudí i Cornet;Gaudí", null);
        assertEquals(expected8.getFirstName(), actual8.getFirstName());
        assertEquals(expected8.getLastName(), actual8.getLastName());
    }

    @Test
    public void testGenerateFriendlyLookingUserIdentifer() {
        int lengthOfUuid = UUID.randomUUID().toString().length();
        assertEquals("uid1", ShibUtil.generateFriendlyLookingUserIdentifer("uid1", null));
        assertEquals("uid1", ShibUtil.generateFriendlyLookingUserIdentifer("uid1", "email1@example.com"));
        assertEquals("email1", ShibUtil.generateFriendlyLookingUserIdentifer(null, "email1@example.com"));
        assertEquals(lengthOfUuid, ShibUtil.generateFriendlyLookingUserIdentifer(null, null).length());
        assertEquals(lengthOfUuid, ShibUtil.generateFriendlyLookingUserIdentifer(null, "").length());
        assertEquals(lengthOfUuid, ShibUtil.generateFriendlyLookingUserIdentifer("", null).length());
        assertEquals(lengthOfUuid, ShibUtil.generateFriendlyLookingUserIdentifer(null, "junkEmailAddress").length());
    }
}
