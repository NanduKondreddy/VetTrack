package com.vettrack.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "import_errors")
public class ImportError {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_file", nullable = false)
    private String sourceFile;

    @Column(name = "row_number", nullable = false)
    private Integer rowNumber;

    @Column(nullable = false)
    private String reason;

    @Column(name = "raw_row", nullable = false, columnDefinition = "TEXT")
    private String rawRow;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public ImportError() {}

    public ImportError(String sourceFile, Integer rowNumber, String reason, String rawRow) {
        this.sourceFile = sourceFile;
        this.rowNumber = rowNumber;
        this.reason = reason;
        this.rawRow = rawRow;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = OffsetDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSourceFile() { return sourceFile; }
    public void setSourceFile(String sourceFile) { this.sourceFile = sourceFile; }
    public Integer getRowNumber() { return rowNumber; }
    public void setRowNumber(Integer rowNumber) { this.rowNumber = rowNumber; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getRawRow() { return rawRow; }
    public void setRawRow(String rawRow) { this.rawRow = rawRow; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
