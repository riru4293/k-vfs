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

/**
 * Test of class TextFileContext.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(FileSystemManagerParameterResolver.class)
class TextFileContextTest {

    private static Path tmpFile;

    @BeforeEach
    void createTmpFile() throws IOException {
        tmpFile = Files.createTempFile(Path.of(System.getProperty("java.io.tmpdir")), null, null);
    }

    @AfterEach
    void removeTmpFile() throws IOException {
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

        TextFileContext result = TextFileContext.of(Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .build(), fsm);

        assertThat(result).returns(expectName, FileContext::getName).returns(StandardCharsets.UTF_8, TextFileContext::getCharset)
                .returns("\n", TextFileContext::getLineEnd);

        assertThat(result.getOptions()).isEmpty();

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

        TextFileContext result = TextFileContext.of(Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .add("charset", "UTF-8").add("lineEnd", "EOL")
                .add("options", Json.createObjectBuilder().add("account", accountCtx)).build(), fsm);

        assertThat(result).returns(expectName, FileContext::getName)
                .returns(StandardCharsets.UTF_8, TextFileContext::getCharset)
                .returns("EOL", TextFileContext::getLineEnd);

        assertThat(result.getOptions()).containsExactly(expectOption);
    }

    /**
     * Test of {@code of} method. If invalid character code name.
     *
     * @since 1.0.0
     */
    @Test
    void testOf_InvalidCharset(FileSystemManager fsm) throws Exception {

        String fileName = tmpFile.toUri().toString();

        JsonObject invalidCharset = Json.createObjectBuilder().add("filename", fileName).add("charset", "INVALID").build();

        assertThatIllegalArgumentException().isThrownBy(() -> TextFileContext.of(invalidCharset, fsm))
                .withMessage("Invalid character code name.");

    }

    /**
     * Test of {@code of} method. If malformed JSON as text file context.
     *
     * @since 1.0.0
     */
    @Test
    void testOf_MalformedJson(FileSystemManager fsm) throws Exception {

        JsonObject malformed = Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .add("charset", JsonValue.EMPTY_JSON_ARRAY).build();

        assertThatIllegalArgumentException().isThrownBy(() -> TextFileContext.of(malformed, fsm))
                .withMessage("Malformed JSON as text file context.");
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

        TextFileContext.Builder builder = new TextFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withOptions(List.of(new AccountContext("id", "password", null)))
                .withCharset(StandardCharsets.US_ASCII).withLineEnd("EOL");

        TextFileContext result = builder.build();

        assertThat(result).returns(expectName, FileContext::getName)
                .returns(StandardCharsets.US_ASCII, TextFileContext::getCharset)
                .returns("EOL", TextFileContext::getLineEnd);

        assertThat(result.getOptions()).containsExactlyInAnyOrder(expectOption);

    }

    /**
     * Test of getCharset method.
     *
     * @since 1.0.0
     */
    @Test
    void testGetCharset(FileSystemManager fsm) throws Exception {

        TextFileContext textFileCtx = new TextFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withCharset(StandardCharsets.UTF_16).build();

        assertThat(textFileCtx).returns(StandardCharsets.UTF_16, TextFileContext::getCharset);

    }

    /**
     * Test of getLineEnd method.
     *
     * @since 1.0.0
     */
    @Test
    void testGetLineEnd(FileSystemManager fsm) throws Exception {

        TextFileContext textFileCtx = new TextFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withLineEnd("EOL").build();

        assertThat(textFileCtx).returns("EOL", TextFileContext::getLineEnd);
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
                .add("options", Json.createObjectBuilder()
                        .add("account", Json.createObjectBuilder()
                                .add("id", "id").add("password", "password").add("domain", "domain"))).build();

        TextFileContext textFileCtx = TextFileContext.of(expect, fsm);

        JsonObject result = textFileCtx.toJsonObject();

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of {@code equals} method and {@code hashCode} method.
     *
     * @since 1.0.0
     */
    @Test
    void testEqualsHashCode(FileSystemManager fsm) throws Exception {

        TextFileContext fileCtx1 = new TextFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withCharset(StandardCharsets.UTF_16BE).withLineEnd("EOL")
                .withOptions(List.of(new AccountContext("i", "p", null))).build();

        TextFileContext fileCtx2 = new TextFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withCharset(StandardCharsets.UTF_16BE).withLineEnd("EOL")
                .withOptions(List.of(new AccountContext("i", "p", null))).build();

        TextFileContext fileCtx3 = new TextFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withCharset(StandardCharsets.UTF_16LE).withLineEnd("EOL")
                .withOptions(List.of(new AccountContext("i", "p", null))).build();

        TextFileContext fileCtx4 = new TextFileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withCharset(StandardCharsets.UTF_16BE).withLineEnd("EndOfLine")
                .withOptions(List.of(new AccountContext("i", "p", null))).build();

        assertThat(fileCtx1).hasSameHashCodeAs(fileCtx2).isEqualTo(fileCtx2)
                .doesNotHaveSameHashCodeAs(fileCtx3).isNotEqualTo(fileCtx3)
                .doesNotHaveSameHashCodeAs(fileCtx4).isNotEqualTo(fileCtx4);

    }
}
