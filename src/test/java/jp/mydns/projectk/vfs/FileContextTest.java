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
import java.net.URI;
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
 * Test of class FileContext.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(FileSystemManagerParameterResolver.class)
class FileContextTest {

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
     * Test of {@code of} method.
     *
     * @since 1.0.0
     */
    @Test
    void testOf(FileSystemManager fsm) throws Exception {

        FileName expectName = fsm.resolveURI(tmpFile.toUri().toString());
        FileOption expectOption = new AccountContext("id", "password", "domain");

        JsonObject accountCtx = Json.createObjectBuilder().add("id", "id").add("password", "password")
                .add("domain", "domain").build();

        FileContext result = FileContext.of(Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .add("options", Json.createObjectBuilder().add("account", accountCtx)).build(), fsm);

        assertThat(result.getName()).isEqualTo(expectName);
        assertThat(result.getOptions()).containsExactly(expectOption);
    }

    /**
     * Test of {@code of} method. If malformed JSON as file context.
     *
     * @since 1.0.0
     */
    @Test
    void testOf_MalformedJson(FileSystemManager fsm) throws Exception {

        JsonObject malformed = Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .add("options", "malformed").build();

        assertThatIllegalArgumentException().isThrownBy(() -> FileContext.of(malformed, fsm))
                .withMessage("Malformed JSON as file context.");
    }

    /**
     * Test of {@code of} method. If unknown file system property.
     *
     * @since 1.0.0
     */
    @Test
    void testOf_UnknownFileSystemPropery(FileSystemManager fsm) throws Exception {

        JsonObject withUnknown = Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .add("options", Json.createObjectBuilder().add("unknown", JsonValue.EMPTY_JSON_OBJECT)).build();

        assertThatIllegalArgumentException().isThrownBy(() -> FileContext.of(withUnknown, fsm))
                .withMessage("Unknown file system property. [unknown]");
    }

    /**
     * Test of {@code of} method. If no found filename property.
     *
     * @since 1.0.0
     */
    @Test
    void testOf_NoFoundFileNameProperty(FileSystemManager fsm) throws Exception {

        assertThatIllegalArgumentException().isThrownBy(() -> FileContext.of(JsonValue.EMPTY_JSON_OBJECT, fsm))
                .withMessage("No found file name.");
    }

    /**
     * Test of toUri method.
     *
     * @since 1.0.0
     */
    @Test
    void testToUri(FileSystemManager fsm) throws Exception {

        URI expectUri = tmpFile.toUri();

        FileContext fileCtx = FileContext.of(Json.createObjectBuilder()
                .add("filename", tmpFile.toUri().toString()).build(), fsm);

        URI result = fileCtx.toUri();

        assertThat(result).isEqualTo(expectUri);
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

        FileContext.Builder builder = new FileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withOptions(List.of(new AccountContext("id", "password", null)));

        FileContext result = builder.build();

        assertThat(result.getName()).isEqualTo(expectName);
        assertThat(result.getOptions()).containsExactlyInAnyOrder(expectOption);
    }

    /**
     * Test of toFileObject method.
     *
     * @since 1.0.0
     */
    @Test
    void testToFileObject(FileSystemManager fsm) throws Exception {

        FileContext fileCtx = new FileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withOptions(List.of(new AccountContext(null, null, null))).build();

        try (var fo = fileCtx.toFileObject(fsm); var fc = fo.getContent(); var os = fc.getOutputStream(false);) {
            os.write("Hello".getBytes(StandardCharsets.UTF_8));
        }

        String content = new String(Files.readAllBytes(tmpFile), StandardCharsets.UTF_8);

        assertThat(content).isEqualTo("Hello");
    }

    /**
     * Test of {@code toJsonObject} method.
     *
     * @since 1.0.0
     */
    @Test
    void testToJsonObject(FileSystemManager fsm) throws Exception {

        JsonObject expect = Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .add("options", Json.createObjectBuilder()
                        .add("account", Json.createObjectBuilder()
                                .add("id", "id").add("password", "password").add("domain", "domain"))).build();

        FileContext fileCtx = FileContext.of(expect, fsm);

        JsonObject result = fileCtx.toJsonObject();

        assertThat(result).isEqualTo(expect);
    }

    /**
     * Test of {@code equals} method and {@code hashCode} method.
     *
     * @since 1.0.0
     */
    @Test
    void testEqualsHashCode(FileSystemManager fsm) throws Exception {

        FileContext fileCtx1 = new FileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withOptions(List.of(new AccountContext("i", "p", null))).build();

        FileContext fileCtx2 = new FileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withOptions(List.of(new AccountContext("i", "p", null))).build();

        FileContext fileCtx3 = new FileContext.Builder(fsm.resolveURI(tmpFile.toUri().toString()))
                .withOptions(List.of(new AccountContext("i", "p", "d"))).build();

        assertThat(fileCtx1).hasSameHashCodeAs(fileCtx2).isEqualTo(fileCtx2)
                .doesNotHaveSameHashCodeAs(fileCtx3).isNotEqualTo(fileCtx3);
    }

    /**
     * Test of {@code toString} method.
     *
     * @since 1.0.0
     */
    @Test
    void testToString(FileSystemManager fsm) throws Exception {

        JsonObject src = Json.createObjectBuilder().add("filename", tmpFile.toUri().toString())
                .add("options", Json.createObjectBuilder()
                        .add("account", Json.createObjectBuilder()
                                .add("id", "id").add("password", "password").add("domain", "domain"))).build();

        String expect = src.toString();

        FileContext fileCtx = FileContext.of(src, fsm);

        String result = fileCtx.toString();

        assertThat(result).isEqualTo(expect);
    }
}
