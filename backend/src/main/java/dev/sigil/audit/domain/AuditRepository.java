package dev.sigil.audit.domain;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuditRepository extends JpaRepository<AuditEntry, UUID> {

  @Query(
      """
            SELECT e FROM AuditEntry e
            WHERE (:cursorTs IS NULL OR e.occurredAt < :cursorTs
                OR (e.occurredAt = :cursorTs AND e.id < :cursorId))
            ORDER BY e.occurredAt DESC, e.id DESC
            LIMIT :limit
            """)
  List<AuditEntry> findPage(
      @Param("cursorTs") Instant cursorTs,
      @Param("cursorId") UUID cursorId,
      @Param("limit") int limit);
}
