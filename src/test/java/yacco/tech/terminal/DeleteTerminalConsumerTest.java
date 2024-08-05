package yacco.tech.terminal;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import yacco.tech.terminal.model.Terminal;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertNull;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
class DeleteTerminalConsumerTest {


    @Inject
    DeleteTerminalConsumer deleteTerminalConsumer;

    @Inject
    TerminalService service;

    @Test
    public void shouldConsumeDeleteTerminalEvent() throws ExecutionException, InterruptedException {
        // arrange
        Terminal toDelete = service.createTerminal(
                        "integration-test-delete-serial-1",
                        "integration-test-delete-model-1"
                )
                .await()
                .indefinitely();

        ConsumerRecord<String, String> consumerRecord = new ConsumerRecord<>("delete-terminal", 0, 0, null, toDelete.id.toString());

        // act
        CompletableFuture<Void> future = deleteTerminalConsumer.consumeTerminalDeletedEvent(consumerRecord).subscribeAsCompletionStage();
        future.get();

        // assert
        Optional<Terminal> optTerm = service.findBySerial("integration-test-serial").await().indefinitely();
        Terminal term = optTerm.orElse(null);
        assertNull(term);
    }
}