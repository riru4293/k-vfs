/*
 * Copyright (c) 2023, Project-K
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package jp.mydns.projectk.vfs;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemManager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.FileSystemManagerParameterResolver;

/**
 * Test of class CsvFileContext.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(FileSystemManagerParameterResolver.class)
class CsvFileContextTest {

    private static Path tmpFile;

    @BeforeEach
    private void createTmpFile() throws IOException {
        tmpFile = Files.createTempFile(Path.of(System.getProperty("java.io.tmpdir")), null, null);
    }

    @AfterEach
    private void removeTmpFile() throws IOException {
        Files.deleteIfExists(tmpFile);
    }

    /**
     * Test of {@code of} method. If minimum.
     *
     * @since 1.0.0
     */
    @Test
    void testOf_Minimum(FileSystemManager fsm) throws Exception {

        FileName expectName = fsm.resolveURI(tmpFile.toUri().toString());

        CsvFileContext result = CsvFileContext.of(Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .build(), fsm);

        assertThat(result).returns(expectName, FileContext::getName)
                .returns(StandardCharsets.UTF_8, TextFileContext::getCharset)
                .returns("\n", TextFileContext::getLineEnd)
                .returns(',', CsvFileContext::getSeparator)
                .returns('\\', CsvFileContext::getEscape)
                .returns('"', CsvFileContext::getQuote);

        assertThat(result.getOptions()).isEmpty();

        assertThat(result.getHeaders()).isEmpty();

    }

    /**
     * Test of {@code of} method. If maximum.
     *
     * @since 1.0.0
     */
    @Test
    void testOf_Maximum(FileSystemManager fsm) throws Exception {

        FileName expectName = fsm.resolveURI(tmpFile.toUri().toString());
        FileOption expectOption = new AccountContext("id", "password", "domain");

        JsonObject accountCtx = Json.createObjectBuilder().add("id", "id").add("password", "password")
                .add("domain", "domain").build();

        CsvFileContext result = CsvFileContext.of(Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .add("charset", "UTF-8").add("lineEnd", "EOL")
                .add("separator", "_+").add("quote", "^++").add("escape", "=+++").add("headers", Json.createArrayBuilder()
                .add(Json.createArrayBuilder().add("1").add("2")).add(Json.createArrayBuilder().add("a").add("b")))
                .add("options", Json.createObjectBuilder().add("account", accountCtx)).build(), fsm);

        assertThat(result).returns(expectName, FileContext::getName)
                .returns(StandardCharsets.UTF_8, TextFileContext::getCharset)
                .returns("EOL", TextFileContext::getLineEnd)
                .returns('_', CsvFileContext::getSeparator)
                .returns('=', CsvFileContext::getEscape)
                .returns('^', CsvFileContext::getQuote);

        assertThat(result.getHeaders()).containsExactly(List.of("1", "2"), List.of("a", "b"));

        assertThat(result.getOptions()).containsExactly(expectOption);

    }

    /**
     * Test of {@code of} method. If malformed JSON as csv file context.
     *
     * @since 1.0.0
     */
    @Test
    void testOf_MalformedJson(FileSystemManager fsm) throws Exception {

        JsonObject malformed = Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .add("separator", JsonValue.FALSE).build();

        assertThatIllegalArgumentException().isThrownBy(() -> CsvFileContext.of(malformed, fsm))
                .withMessage("Malformed JSON as CSV file context.");
    }

    /**
     * Test of {@code of} method. If no separator.
     *
     * @since 1.0.0
     */
    @Test
    void testOf_NoSeparator(FileSystemManager fsm) throws Exception {

        JsonObject noSeparator = Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .add("separator", "").build();

        assertThatIllegalArgumentException().isThrownBy(() -> CsvFileContext.of(noSeparator, fsm))
                .withMessage("No found CSV separator.");
    }

    /**
     * Test of {@code of} method. If no quote.
     *
     * @since 1.0.0
     */
    @Test
    void testOf_NoQuote(FileSystemManager fsm) throws Exception {

        JsonObject noQuote = Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .add("quote", "").build();

        assertThatIllegalArgumentException().isThrownBy(() -> CsvFileContext.of(noQuote, fsm))
                .withMessage("No found CSV quote.");
    }

    /**
     * Test of {@code of} method. If no escape.
     *
     * @since 1.0.0
     */
    @Test
    void testOf_NoEscape(FileSystemManager fsm) throws Exception {

        JsonObject noQuote = Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .add("escape", "").build();

        assertThatIllegalArgumentException().isThrownBy(() -> CsvFileContext.of(noQuote, fsm))
                .withMessage("No found CSV escape.");
    }

    /**
     * Test of build method.
     *
     * @since 1.0.0
     */
    @Test
    void testBuild(FileSystemManager fsm) throws Exception {

        FileName expectName = fsm.resolveURI(tmpFile.toUri().toString());
        FileOption expectOption = new AccountContext("id", "password", null);

        CsvFileContext.Builder builder = new CsvFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withOptions(List.of(new AccountContext("id", "password", null)))
                .withSeparator('_').withQuote('=').withEscape('^').withHeaders(List.of(List.of("1"), List.of("2")))
                .withCharset(StandardCharsets.US_ASCII).withLineEnd("EOL");

        CsvFileContext result = builder.build();

        assertThat(result).returns(expectName, FileContext::getName)
                .returns(StandardCharsets.US_ASCII, TextFileContext::getCharset)
                .returns("EOL", TextFileContext::getLineEnd)
                .returns('_', CsvFileContext::getSeparator)
                .returns('^', CsvFileContext::getEscape)
                .returns('=', CsvFileContext::getQuote);

        assertThat(result.getHeaders()).containsExactly(List.of("1"), List.of("2"));

        assertThat(result.getOptions()).containsExactlyInAnyOrder(expectOption);

    }

    /**
     * Test of getSeparator method.
     *
     * @since 1.0.0
     */
    @Test
    void testGetSeparator(FileSystemManager fsm) throws Exception {

        CsvFileContext csvFileCtx = new CsvFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withSeparator('_').build();

        assertThat(csvFileCtx).returns('_', CsvFileContext::getSeparator);

    }

    /**
     * Test of getQuote method.
     *
     * @since 1.0.0
     */
    @Test
    void testGetQuote(FileSystemManager fsm) throws Exception {

        CsvFileContext csvFileCtx = new CsvFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withQuote('=').build();

        assertThat(csvFileCtx).returns('=', CsvFileContext::getQuote);

    }

    /**
     * Test of getEscape method.
     *
     * @since 1.0.0
     */
    @Test
    void testGetEscape(FileSystemManager fsm) throws Exception {

        CsvFileContext csvFileCtx = new CsvFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withEscape('^').build();

        assertThat(csvFileCtx).returns('^', CsvFileContext::getEscape);

    }

    /**
     * Test of getHeaders method.
     *
     * @since 1.0.0
     */
    @Test
    void testGetHeaders(FileSystemManager fsm) throws Exception {

        CsvFileContext csvFileCtx = new CsvFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withHeaders(List.of(List.of("1"), List.of("2"))).build();

        assertThat(csvFileCtx.getHeaders()).containsExactly(List.of("1"), List.of("2"));

    }

    /**
     * Test of toJsonObject method.
     *
     * @since 1.0.0
     */
    @Test
    void testToJsonObject(FileSystemManager fsm) throws Exception {

        JsonObject expect = Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .add("charset", "UTF-8").add("lineEnd", "EOL")
                .add("separator", "_").add("quote", "^").add("escape", "=").add("headers", Json.createArrayBuilder()
                .add(Json.createArrayBuilder().add("1").add("2")).add(Json.createArrayBuilder().add("a").add("b")))
                .add("options", Json.createObjectBuilder()
                        .add("account", Json.createObjectBuilder()
                                .add("id", "id").add("password", "password").add("domain", "domain"))).build();

        CsvFileContext csvFileCtx = CsvFileContext.of(expect, fsm);

        JsonObject result = csvFileCtx.toJsonObject();

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of {@code equals} method and {@code hashCode} method.
     *
     * @since 1.0.0
     */
    @Test
    void testEqualsHashCode(FileSystemManager fsm) throws Exception {

        CsvFileContext fileCtx1 = new CsvFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withCharset(StandardCharsets.UTF_16BE).withLineEnd("EOL")
                .withSeparator('_').withQuote('=').withEscape('^').withHeaders(List.of(List.of("1"), List.of("2")))
                .withOptions(List.of(new AccountContext("i", "p", null))).build();

        CsvFileContext fileCtx2 = new CsvFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withCharset(StandardCharsets.UTF_16BE).withLineEnd("EOL")
                .withSeparator('_').withQuote('=').withEscape('^').withHeaders(List.of(List.of("1"), List.of("2")))
                .withOptions(List.of(new AccountContext("i", "p", null))).build();

        CsvFileContext fileCtx3 = new CsvFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withCharset(StandardCharsets.UTF_16LE).withLineEnd("EOL")
                .withSeparator('-').withQuote('=').withEscape('^').withHeaders(List.of(List.of("1"), List.of("2")))
                .withOptions(List.of(new AccountContext("i", "p", null))).build();

        CsvFileContext fileCtx4 = new CsvFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withCharset(StandardCharsets.UTF_16BE).withLineEnd("EndOfLine")
                .withSeparator('_').withQuote('&').withEscape('^').withHeaders(List.of(List.of("1"), List.of("2")))
                .withOptions(List.of(new AccountContext("i", "p", null))).build();

        CsvFileContext fileCtx5 = new CsvFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withCharset(StandardCharsets.UTF_16BE).withLineEnd("EndOfLine")
                .withSeparator('_').withQuote('=').withEscape('~').withHeaders(List.of(List.of("1"), List.of("2")))
                .withOptions(List.of(new AccountContext("i", "p", null))).build();

        CsvFileContext fileCtx6 = new CsvFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withCharset(StandardCharsets.UTF_16BE).withLineEnd("EndOfLine")
                .withSeparator('_').withQuote('=').withEscape('^').withHeaders(List.of(List.of("1", "2")))
                .withOptions(List.of(new AccountContext("i", "p", null))).build();

        assertThat(fileCtx1).hasSameHashCodeAs(fileCtx2).isEqualTo(fileCtx2)
                .doesNotHaveSameHashCodeAs(fileCtx3).isNotEqualTo(fileCtx3)
                .doesNotHaveSameHashCodeAs(fileCtx4).isNotEqualTo(fileCtx4)
                .doesNotHaveSameHashCodeAs(fileCtx5).isNotEqualTo(fileCtx5)
                .doesNotHaveSameHashCodeAs(fileCtx6).isNotEqualTo(fileCtx6);

    }
}
