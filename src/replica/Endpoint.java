package replica;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pojos.RegisterContentPojo;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("")
public class Endpoint {

    private static final Logger logger = LogManager.getLogger(Endpoint.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Register register = RegisterSingleton.getRegister();

    public Endpoint() throws Exception {
        logger.info("Initiated endpoint");
    }

    @GET
    @Path("read")
    @Produces(APPLICATION_JSON)
    public Response read() {
        int waitTimeReceive = LatencySimulator.simulateLatency();
        logger.debug("Simulated latency of receiving request: {}ms", waitTimeReceive);
        RegisterContentPojo pojo = register.getRegisterContent();
        logger.debug("Reading timestamp: {}, value: {}", pojo.getTimestamp(), pojo.getValue());

        try {
            String json = objectMapper.writeValueAsString(pojo);
            int waitTimeSend = LatencySimulator.simulateLatency();
            logger.debug("Simulated latency of sending response: {}ms", waitTimeSend);
            return Response.ok(json).build();
        } catch (JsonProcessingException e) {
            logger.error("Error serializing object to JSON", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    @POST
    @Path("write")
    @Consumes(APPLICATION_JSON)
    public Response write(String requestBody) {
        try {
            RegisterContentPojo registerContent = objectMapper.readValue(requestBody, RegisterContentPojo.class);
            register.updateRegister(registerContent.getTimestamp(), registerContent.getId(), registerContent.getValue());
            int waitTimeReceive = LatencySimulator.simulateLatency();
            logger.debug("Simulated latency of receiving request: {}ms", waitTimeReceive);
            logger.debug("Writing timestamp: {}, value: {}",
                    registerContent.getTimestamp(), registerContent.getValue());
            int waitTimeSend = LatencySimulator.simulateLatency();
            logger.debug("Simulated latency of sending response: {}ms", waitTimeSend);
            return Response.ok().build();
        } catch (JsonProcessingException e) {
            logger.error("Error deserializing JSON to object", e);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
