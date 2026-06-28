package dev.sigil.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ResultTest {

    @Test
    void okIsOk() {
        var result = Result.ok("value");
        assertThat(result.isOk()).isTrue();
        assertThat(result.isErr()).isFalse();
        assertThat(((Result.Ok<String, ?>) result).value()).isEqualTo("value");
    }

    @Test
    void errIsErr() {
        var result = Result.err("error");
        assertThat(result.isErr()).isTrue();
        assertThat(result.isOk()).isFalse();
        assertThat(((Result.Err<?, String>) result).error()).isEqualTo("error");
    }

    @Test
    void switchExpression() {
        Result<Integer, String> result = Result.ok(42);
        var output = switch (result) {
            case Result.Ok<Integer, String> ok -> "value=" + ok.value();
            case Result.Err<Integer, String> err -> "error=" + err.error();
        };
        assertThat(output).isEqualTo("value=42");
    }
}
