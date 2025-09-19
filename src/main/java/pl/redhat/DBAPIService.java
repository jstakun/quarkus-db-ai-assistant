package pl.redhat;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

import io.smallrye.mutiny.Multi;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DBAPIService {

    @Inject
    DBRepository dbRepository;

    private EventAssistant eventAssistant;
    private CustomerRepresentativeAssistant crAssistant;
    private VendorRepresentativeAssistant vendorAssistant;

    public DBAPIService(EventAssistant eventAssistant, CustomerRepresentativeAssistant crAssistant, VendorRepresentativeAssistant vendorAssistant) {
        this.eventAssistant = eventAssistant;
        this.crAssistant = crAssistant;
        this.vendorAssistant = vendorAssistant;
    }

    @GET
    @Path("/events/search")
    public Response searchEvents(@QueryParam("latitude") Double latitude,
                                 @QueryParam("longitude") Double longitude,
                                 @QueryParam("type") String type,
                                 @QueryParam("startDate") String startDate,
                                 @QueryParam("endDate") String endDate) {
        List<Event> events = dbRepository.searchEvents(latitude, longitude, type, startDate, endDate);
        return Response.ok(events).build();
    }

    @GET
    @Path("/events/query/{question}")
    @Produces(MediaType.TEXT_PLAIN)
    public Multi<String> queryAIEvents(String question) {
        final Multi<String> response = eventAssistant.assistUser(question);
        return response;
    }

    @GET
    @Path("/customer/query/{question}")
    @Produces(MediaType.TEXT_PLAIN)
    public String queryAICustomer(String question) {
        final String response = crAssistant.assistUser(question);
        return response;
    }

    @GET
    @Path("/vendor/query/{question}")
    @Produces(MediaType.TEXT_PLAIN)
    public String  queryAIVendor(String question) {
        final Multi<String> response = vendorAssistant.assistUser(question);
        return response.collect().asList()
            .onItem().transform(list -> String.join("", list))
            .await().indefinitely();
    }
}

