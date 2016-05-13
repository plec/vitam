package fr.gouv.vitam.client;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.junit.Test;

public class StatusMetaDataClientTest extends JerseyTest {

	private static final String url = "http://localhost:8082";
	private static final MetaDataClient client = new MetaDataClient(url);	
	Supplier<Response> mock;

	@Override
	protected Application configure() {
		enable(TestProperties.LOG_TRAFFIC);
		enable(TestProperties.DUMP_ENTITY);
		forceSet(TestProperties.CONTAINER_PORT, "8082");
		mock = mock(Supplier.class);
		return new ResourceConfig().registerInstances(new MyUnitsResource(mock));
	}

	@Path("/metadata/v1")
	public static class MyUnitsResource {
		private Supplier<Response> expectedResponse;

		public MyUnitsResource(Supplier<Response> expectedResponse) {
			this.expectedResponse = expectedResponse;
		}

		@Path("status")
		@GET
		public Response status(String url) {
			return Response.status(Status.OK).build();
		}
	}
	
	@Test
	public void shouldGetStatusOK() {
		when(mock.get()).thenReturn(Response.status(Status.OK).build());
		Response response = client.status();
		assertEquals(Status.OK.getStatusCode(), response.getStatus());
	}
}