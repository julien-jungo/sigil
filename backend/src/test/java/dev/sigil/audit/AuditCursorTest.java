package dev.sigil.audit;

import static org.assertj.core.api.Assertions.assertThat;

import dev.sigil.audit.service.AuditCursor;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AuditCursorTest {

  @Test
  void roundTripEncoding() {
    var ts = Instant.parse("2026-01-15T10:30:00Z");
    var id = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    var cursor = new AuditCursor(ts, id);

    var encoded = cursor.encode();
    var decoded = AuditCursor.decode(encoded);

    assertThat(decoded.occurredAt()).isEqualTo(ts);
    assertThat(decoded.id()).isEqualTo(id);
  }

  @Test
  void encodedStringIsUrlSafe() {
    var cursor = new AuditCursor(Instant.now(), UUID.randomUUID());
    var encoded = cursor.encode();
    assertThat(encoded).doesNotContain("+", "/", "=");
  }

  @Test
  void differentCursorsProduceDifferentEncodings() {
    var a = new AuditCursor(Instant.parse("2026-01-01T00:00:00Z"), UUID.randomUUID());
    var b = new AuditCursor(Instant.parse("2026-01-02T00:00:00Z"), UUID.randomUUID());
    assertThat(a.encode()).isNotEqualTo(b.encode());
  }
}
