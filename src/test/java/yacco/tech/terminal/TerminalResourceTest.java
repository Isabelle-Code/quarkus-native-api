package yacco.tech.terminal;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import yacco.tech.terminal.model.CreateTerminalDTO;
import yacco.tech.terminal.model.Terminal;
import yacco.tech.terminal.model.TerminalDTO;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
class TerminalResourceTest {

    @Inject
    TerminalService service;

    @Test
    @Transactional
    public void shouldCreateTerminalAndList() {
        // arrange
        CreateTerminalDTO createTerminal = new CreateTerminalDTO();
        createTerminal.setSerial("create-terminal-test-1");
        createTerminal.setModel("model");

        // act
        Response body = given()
                .contentType("application/json")
                .body(createTerminal)
                .when()
                .post("/terminals")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // assert
        String terminalId = body.jsonPath().getString("id");

        Terminal term = service.findById(terminalId).await().indefinitely().orElse(null);
        assertNotNull(term);
        assertEquals("create-terminal-test-1", term.serial);
        assertEquals("model", term.model);

        // cleanup
        service.deleteById(terminalId).await().indefinitely();

    }

    @Test
    @Transactional
    public void shouldDeleteTerminal() {
        // arrange
        CreateTerminalDTO createTerminal = new CreateTerminalDTO();
        createTerminal.setSerial("delete-terminal-test-1");
        createTerminal.setModel("model");

        Terminal term = service
                .createTerminal(createTerminal.getSerial(), createTerminal.getModel())
                .await().indefinitely();

        // act
        given()
                .when()
                .delete("/terminals/terminal/" + term.id)
                .then()
                .statusCode(204);

        // assert
        Terminal deletedTerm = service
                .findBySerial(createTerminal.getSerial()).await().indefinitely()
                .orElse(null);

        assertNull(deletedTerm);

    }

    @Test
    @Transactional
    public void shouldUpdateTerminal() {
        // arrange
        CreateTerminalDTO createTerminal = new CreateTerminalDTO();
        createTerminal.setSerial("update-terminal-test-1");
        createTerminal.setModel("model");

        Terminal term = service
                .createTerminal(createTerminal.getSerial(), createTerminal.getModel())
                .await().indefinitely();

        // act
        given()
                .contentType("application/json")
                .body("{\"status\":\"ACTIVE\"}")
                .when()
                .patch("/terminals/terminal/" + term.id)
                .then()
                .statusCode(200);

        // assert
        Terminal updatedTerm = service.findBySerial(createTerminal.getSerial())
                .await().indefinitely().orElse(null);

        assertNotNull(updatedTerm);
        assertEquals("ACTIVE", updatedTerm.status.toString());

        // cleanup
        service.deleteById(term.id.toString()).await().indefinitely();
    }

    @Test
    @Transactional
    public void shouldFindOneTerminal() {
        // arrange
        CreateTerminalDTO createTerminal = new CreateTerminalDTO();
        createTerminal.setSerial("find-terminal-test-1");
        createTerminal.setModel("model");

        Terminal term = service
                .createTerminal(createTerminal.getSerial(), createTerminal.getModel())
                .await().indefinitely();

        // act
        Response body = given()
                .when()
                .get("/terminals/terminal/" + term.id)
                .then()
                .statusCode(200)
                .extract()
                .response();

        // assert
        assertEquals(term.id.toString(), body.jsonPath().getString("id"));
        assertEquals(term.serial, body.jsonPath().getString("serial"));
        assertEquals(term.model, body.jsonPath().getString("model"));

        // cleanup
        service.deleteById(term.id.toString()).await().indefinitely();
    }

    @Test
    @Transactional
    public void shouldReturnBadRequestCreatingTerminalWithSameSerialID() {
        // arrange
        CreateTerminalDTO createTerminal = new CreateTerminalDTO();
        createTerminal.setSerial("create-terminal-test-2");
        createTerminal.setModel("model");

        // act
        given()
                .contentType("application/json")
                .body(createTerminal)
                .when()
                .post("/terminals")
                .then()
                .statusCode(200);

        // assert
        given()
                .contentType("application/json")
                .body(createTerminal)
                .when()
                .post("/terminals")
                .then()
                .statusCode(400);

        // cleanup
        service.findBySerial(createTerminal.getSerial()).await().indefinitely()
                .ifPresent(term -> service.deleteById(term.id.toString()).await().indefinitely());
    }

    @Test
    @Transactional
    public void shouldReturnNotFoundWhenUpdateTerminalWithInvalidID() {
        // act
        given()
                .contentType("application/json")
                .body("{\"status\":\"ACTIVE\"}")
                .when()
                .patch("/terminals/terminal/665d8dae858b3f0b9f3b1957")
                .then()
                .statusCode(404);
    }

    @Test
    @Transactional
    public void shouldReturnNotFoundWhenTryingToFindOneInvalidTerminal() {
        // act
        given()
                .when()
                .get("/terminals/terminal/61ad8dae858b3f0b9f3b1957")
                .then()
                .statusCode(404);
    }

    @Test
    @Transactional
    public void shouldListAllTerminals() {
        // arrange
        CreateTerminalDTO createTerminal = new CreateTerminalDTO();
        createTerminal.setSerial("list-terminal-test-1");
        createTerminal.setModel("model");

        Terminal term = service
                .createTerminal(createTerminal.getSerial(), createTerminal.getModel())
                .await().indefinitely();

        // act
        Response body = given()
                .when()
                .get("/terminals")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // assert
        List<TerminalDTO> list = body.jsonPath().getList("data", TerminalDTO.class);
        assert (!list.isEmpty());
        assert (list.stream().anyMatch(t -> t.getId().equals(term.id.toString())));

        // cleanup
        service.deleteById(term.id.toString()).await().indefinitely();
    }

    @Test
    @Transactional
    public void shouldReturnErrorWhenTryingToCreateTerminalWithDuplicatedSerial() {
        // arrange
        CreateTerminalDTO createTerminal = new CreateTerminalDTO();
        createTerminal.setSerial("create-terminal-test-3");
        createTerminal.setModel("model");

        // act
        given()
                .contentType("application/json")
                .body(createTerminal)
                .when()
                .post("/terminals")
                .then()
                .statusCode(200);

        // assert
        given()
                .contentType("application/json")
                .body(createTerminal)
                .when()
                .post("/terminals")
                .then()
                .statusCode(400);

        // cleanup
        service.findBySerial(createTerminal.getSerial()).await().indefinitely()
                .ifPresent(term -> service.deleteById(term.id.toString()).await().indefinitely());
    }
}