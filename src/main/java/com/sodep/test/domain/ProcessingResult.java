package com.sodep.test.domain;

import java.util.List;

public sealed interface ProcessingResult {

    record Processed(String externalId, String category, double computedValue)
            implements ProcessingResult {}

    record Skipped(String externalId, String reason)
            implements ProcessingResult {}

    record Failed(String externalId, String error, Throwable cause)
            implements ProcessingResult {}

    record BatchResult(List<ProcessingResult> results) {
        public long processedCount() {
            return results.stream().filter(r -> r instanceof Processed).count();
        }

        public long skippedCount() {
            return results.stream().filter(r -> r instanceof Skipped).count();
        }

        public long failedCount() {
            return results.stream().filter(r -> r instanceof Failed).count();
        }
    }
}
