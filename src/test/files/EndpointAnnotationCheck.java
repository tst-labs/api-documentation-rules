@Path("tests")
@Api("tests")
public class EndpointAnnotationCheck {
    @GET
    @Path("/")
    @ApiOperation(value = "Explain this operation", response = SomePojo.class, responseContainer = "List", code = HttpStatus.SC_OK)
    public Response allCompliant() {}

    @GET // Noncompliant {{Methods annotated with @GET must also be annotated with @ApiOperation}}
    @Path("/")
    public Response allNonCompliant() {}

}
