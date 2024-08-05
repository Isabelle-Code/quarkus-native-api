package yacco.tech.terminal;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import yacco.tech.terminal.model.CreateTerminalDTO;
import yacco.tech.terminal.model.TerminalDTO;
import yacco.tech.terminal.model.TerminalListDTO;
import yacco.tech.terminal.model.UpdateTerminalDTO;

@Path("/terminals")
public class TerminalResource {

    private final TerminalService service;

    public TerminalResource(TerminalService service) {
        this.service = service;
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Uni<TerminalDTO> create(CreateTerminalDTO createTerminal) {
        return
                service
                        .createTerminal(
                                createTerminal.getSerial(),
                                createTerminal.getModel()
                        )
                        .map(TerminalDTO::fromEntity);


    }

    @GET
    @Produces("application/json")
    public Uni<TerminalListDTO> findAll() {
        return
                service
                        .listAll()
                        .map(TerminalDTO::fromEntity)
                        .collect().asList()
                        .onItem()
                        .transform(TerminalListDTO::new);
    }

    @GET
    @Path("/terminal/{terminalID}")
    public Uni<TerminalDTO> findOne(@PathParam("terminalID") String id) {
        return
                service.findById(id)
                        .onItem()
                        .transformToUni(optTerm -> {
                            if (optTerm.isPresent()) {
                                return Uni.createFrom().item(optTerm.get());
                            } else {
                                throw new NotFoundException();
                            }
                        })
                        .onItem()
                        .transform(TerminalDTO::fromEntity);
    }

    @DELETE
    @Path("/terminal/{terminalID}")
    public Uni<Void> deleteOne(@PathParam("terminalID") String id) {
        return
                service
                        .deleteById(id)
                        .onItemOrFailure()
                        .transformToUni(((terminal, throwable) -> Uni.createFrom().voidItem()));
    }

    @PATCH
    @Path("/terminal/{terminalID}")
    public Uni<TerminalDTO> updateOne(@PathParam("terminalID") String id, UpdateTerminalDTO dto) {
        return service
                .updateById(id, dto.getStatus())
                .map(TerminalDTO::fromEntity);

    }
}