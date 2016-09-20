package fr.gouv.vitam.metadata.rest;

import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Header;
import com.jayway.restassured.response.Headers;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import fr.gouv.vitam.api.config.MetaDataConfiguration;
import fr.gouv.vitam.common.SystemPropertyUtil;
import fr.gouv.vitam.common.database.parser.request.GlobalDatasParser;
import fr.gouv.vitam.common.database.server.elasticsearch.ElasticsearchNode;
import fr.gouv.vitam.common.junit.JunitHelper;
import fr.gouv.vitam.common.server.VitamServer;
import fr.gouv.vitam.core.database.collections.MetadataCollections;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.jhades.JHades;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import javax.ws.rs.core.Response.Status;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.with;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

public class UpdateUnitResourceTest {


    private static final String DATA =
        "{ \"_id\": \"aeaqaaaaaeaaaaakaarp4akuuf2ldmyaaaaq\", " + "\"data\": \"data2\" }";
    private static final String DATA2 =
        "{ \"_id\": \"aeaqaaaaaeaaaaakaarp4akuuf2ldmyaaaab\"," + "\"data\": \"data2\" }";

    private static final String ID_UNIT = "aeaqaaaaaeaaaaakaarp4akuuf2ldmyaaaab";
    private static final String DATA_URI = "/metadata/v1";
    private static final String DATABASE_NAME = "vitam-test";
    private static final String JETTY_CONFIG = "jetty-config-test.xml";
    private static MongodExecutable mongodExecutable;
    static MongodProcess mongod;

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();
    private static File elasticsearchHome;

    private final static String CLUSTER_NAME = "vitam-cluster";
    private final static String HOST_NAME = "127.0.0.1";
    private static int TCP_PORT = 9300;
    private static int HTTP_PORT = 9200;
    private static Node node;

    private static final String SERVER_HOST = "localhost";

    private static final String X_HTTP_Method = "X-Http-Method-Override";
    private static final String GET = "GET";

    private static final String BODY_TEST = "{$query: {$eq: {\"data\" : \"data2\" }}, $projection: {}, $filter: {}}";
    private static JunitHelper junitHelper;
    private static int serverPort;
    private static int dataBasePort;


    private static final String buildDSLWithOptions(String query, String data) {
        return "{ $roots : [ '' ], $query : [ " + query + " ], $data : " + data + " }";
    }


    private static String createJsonStringWithDepth(int depth) {
        final StringBuilder obj = new StringBuilder();
        if (depth == 0) {
            return " \"b\" ";
        }
        obj.append("{ \"a\": ").append(createJsonStringWithDepth(depth - 1)).append("}");
        return obj.toString();
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        // Identify overlapping in particular jsr311
        new JHades().overlappingJarsReport();
        junitHelper = new JunitHelper();

        //ES
        TCP_PORT = junitHelper.findAvailablePort();
        HTTP_PORT = junitHelper.findAvailablePort();

        elasticsearchHome = tempFolder.newFolder();
        Settings settings = Settings.settingsBuilder()
            .put("http.enabled", true)
            .put("discovery.zen.ping.multicast.enabled", false)
            .put("transport.tcp.port", TCP_PORT)
            .put("http.port", HTTP_PORT)
            .put("path.home", elasticsearchHome.getCanonicalPath())
            .build();

        node = nodeBuilder()
            .settings(settings)
            .client(false)
            .clusterName(CLUSTER_NAME)
            .node();

        node.start();

        List<ElasticsearchNode> nodes = new ArrayList<ElasticsearchNode>();
        nodes.add(new ElasticsearchNode(HOST_NAME, TCP_PORT));

        dataBasePort = junitHelper.findAvailablePort();

        final MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(new MongodConfigBuilder()
            .version(Version.Main.PRODUCTION)
            .net(new Net(dataBasePort, Network.localhostIsIPv6()))
            .build());
        mongod = mongodExecutable.start();

        final MetaDataConfiguration configuration =
            new MetaDataConfiguration(SERVER_HOST, dataBasePort, DATABASE_NAME, CLUSTER_NAME, nodes, JETTY_CONFIG);
        serverPort = junitHelper.findAvailablePort();
        SystemPropertyUtil.set(VitamServer.PARAMETER_JETTY_SERVER_PORT, Integer.toString(serverPort));
        MetaDataApplication.run(configuration);
        RestAssured.port = serverPort;
        RestAssured.basePath = DATA_URI;
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        MetaDataApplication.stop();
        mongod.stop();
        mongodExecutable.stop();
        junitHelper.releasePort(dataBasePort);
        junitHelper.releasePort(serverPort);

        if (node != null) {
            node.close();
        }

        junitHelper.releasePort(TCP_PORT);
        junitHelper.releasePort(HTTP_PORT);
    }

    @After
    public void tearDown() {
        MetadataCollections.C_UNIT.getCollection().drop();
    }
    // Unit by ID (request and uri)



    @Test
    public void given_2units_insert_when_UpdateUnitsByID_thenReturn_Found() throws Exception {
        with()
            .contentType(ContentType.JSON)
            .body(buildDSLWithOptions("", DATA2)).when()
            .post("/units").then()
            .statusCode(Status.CREATED.getStatusCode());

        with()
            .contentType(ContentType.JSON)
            .body(buildDSLWithOptions("", DATA)).when()
            .post("/units").then()
            .statusCode(Status.CREATED.getStatusCode());

        given()
            .contentType(ContentType.JSON)
            .body(BODY_TEST).when()
            .put("/units/" + ID_UNIT).then()
            .statusCode(Status.METHOD_NOT_ALLOWED.getStatusCode());

        given().headers(Headers.headers(new Header(X_HTTP_Method, GET)))
            .contentType(ContentType.JSON)
            .body(BODY_TEST).when()
            .put("/units/" + ID_UNIT).then()
            .statusCode(Status.FOUND.getStatusCode());
    }

    @Test
    public void given_emptyQuery_when_UpdateByID_thenReturn_Bad_Request() {

        given()
            .contentType(ContentType.JSON)
            .header(X_HTTP_Method, "GET")
            .body("")
            .when()
            .put("/units/" + ID_UNIT)
            .then()
            .statusCode(Status.BAD_REQUEST.getStatusCode());
    }


    @Test
    public void given_bad_header_when_UpdateByID_thenReturn_Not_allowed() {

        given()
            .contentType(ContentType.JSON)
            .header(X_HTTP_Method, "ABC")
            .body(BODY_TEST)
            .when()
            .put("/units/" + ID_UNIT)
            .then()
            .statusCode(Status.METHOD_NOT_ALLOWED.getStatusCode());
    }

    @Test
    public void shouldReturn_Request_Entity_Too_Large_If_DocumentIsTooLarge() throws Exception {
        int limitRequest = GlobalDatasParser.limitRequest;
        GlobalDatasParser.limitRequest = 99;
        given()
            .contentType(ContentType.JSON)
            .header(X_HTTP_Method, "GET")
            .body(buildDSLWithOptions("", createJsonStringWithDepth(101))).when()
            .put("/units/" + ID_UNIT).then()
            .statusCode(Status.REQUEST_ENTITY_TOO_LARGE.getStatusCode());
        GlobalDatasParser.limitRequest = limitRequest;
    }

    @Test
    public void shouldReturnErrorRequestBadRequestIfDocumentIsTooLarge() throws Exception {
        int limitRequest = GlobalDatasParser.limitRequest;
        GlobalDatasParser.limitRequest = 99;
        given()
            .contentType(ContentType.JSON)
            .header(X_HTTP_Method, "GET")
            .body(buildDSLWithOptions("", createJsonStringWithDepth(101))).when()
            .put("/units/" + ID_UNIT).then()
            .statusCode(Status.REQUEST_ENTITY_TOO_LARGE.getStatusCode());
        GlobalDatasParser.limitRequest = limitRequest;
    }



    @Test
    public void shouldReturnErrorRequestBadRequest() throws Exception {
        given()
            .contentType(ContentType.JSON)
            .header(X_HTTP_Method, "GET")
            .body(buildDSLWithOptions("", "lkvhvgvuyqvkvj")).when()
            .put("/units/" + ID_UNIT).then()
            .statusCode(Status.BAD_REQUEST.getStatusCode());
    }


    @Ignore
    @Test
    public void given_pathWithId_when_get_UpdateByID_thenReturn_Found() {

        given()
            .contentType(ContentType.JSON)
            .body(BODY_TEST)
            .when()
            .put("/units/" + ID_UNIT)
            .then()
            .statusCode(Status.FOUND.getStatusCode());
    }

}