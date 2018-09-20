@Path("tests")
@Api("tests")
public class EndpointAnnotationCheckCompliant {
    @GET
    @Path("/")
    @ApiOperation(value = "Explain this operation", response = SomePojo.class, responseContainer = "List", code = HttpStatus.SC_OK)
    public Response allCompliant() {}

}
