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
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonCollectors;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toUnmodifiableMap;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.FileSystemOptions;

/**
 * Information of one file or directory.
 *
 * <p>
 * Implementation requirements.
 * <ul>
 * <li>This class is immutable and thread-safe.</li>
 * <li>This class and JSON can be converted bidirectionally.</li>
 * <li>Can be creates a {@link FileObject} from this class.</li>
 * <li>Files must not be accessed from this class.</li>
 * </ul>
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
public class FileContext {

    private final FileName name;
    private final List<FileOption> options;

    /**
     * Construct a new instance from JSON.
     *
     * @param fileCtx a JSON representation of the {@code FileContext}
     * <p>
     * JSON Format<pre><code>
     * {
     *     "filename": "A URI representation file"
     *    ,"options": {
     *         "An option name": "An option value"
     *     }
     * }
     * </code></pre>
     *
     * Example: In case of the local file.
     * <pre><code>
     * {
     *     "filename": "file:///path/to/any/file.example"
     * }
     * </code></pre>
     *
     * Example: In case of the FTP file.
     * <pre><code>
     * {
     *     "filename": "ftp://hostname:21/path/to/any/file.example"
     *    ,"options": {
     *         "account": {"id": "ftp-user", "password": "ftp-pass"}
     *        ,"ftp:usePassiveMode"   : true
     *        ,"ftp:connectionTimeout": "PT5S"
     *        ,"ftp:dataTimeout"      : "PT1M"
     *        ,"ftp:socketTimeout"    : "PT5S"
     *     }
     * }
     * </code></pre>
     *
     * Example: In case of the SMB file.
     * <pre><code>
     * {
     *     "filename": "smb://hostname/path/to/any/file.example"
     *    ,"options": {
     *         "account": {
     *             "id"      : "smb-user"
     *            ,"password": "smb-pass"
     *            ,"domain"  : "DOMAIN-NAME"
     *         }
     *        ,"smb:minVersion": "SMB302"
     *     }
     * }
     * </code></pre><pre>Available SMB versions...
     * <code>"SMB1","SMB202","SMB210","SMB300","SMB302","SMB311"</code></pre>
     *
     * @param fsm the {@code FileSystemManager}
     * @return a new {@code FileContext}
     * @throws FileSystemException if filename is invalid
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if invalid value inside {@code fileCtx}
     * @since 1.0.0
     */
    public static FileContext of(JsonObject fileCtx, FileSystemManager fsm) throws FileSystemException {

        Objects.requireNonNull(fileCtx);
        Objects.requireNonNull(fsm);

        final JsonString uri;
        final JsonObject rawOpts;

        try {

            uri = Optional.ofNullable(fileCtx.getJsonString("filename")).orElseThrow(
                    () -> new IllegalArgumentException("No found file name."));

            rawOpts = Optional.ofNullable(fileCtx.getJsonObject("options")).orElse(JsonValue.EMPTY_JSON_OBJECT);

        } catch (ClassCastException ex) {

            throw new IllegalArgumentException("Malformed JSON as file context.", ex);

        }

        FileName name = fsm.resolveURI(uri.getString());
        List<FileOption> opts = resolveFileOptions(rawOpts, fsm.getClass().getClassLoader());

        return new Builder(name).withOptions(opts).build();
    }

    private static List<FileOption> resolveFileOptions(JsonObject rawOpts, ClassLoader cl) {

        Map<String, FileOption.Resolver> resolvers = ServiceLoader.load(FileOption.Resolver.class, cl).stream()
                .map(ServiceLoader.Provider::get).collect(toUnmodifiableMap(FileOption.Resolver::getName, identity()));

        Function<Map.Entry<String, JsonValue>, FileOption> resolve = e -> resolvers.get(e.getKey()).newInstance(e.getValue());

        List<String> unknowns = rawOpts.keySet().stream().filter(not(resolvers::containsKey)).toList();

        if (!unknowns.isEmpty()) {
            throw new IllegalArgumentException("Unknown file system property. " + unknowns.toString());
        }

        return rawOpts.entrySet().stream().map(resolve).toList();
    }

    protected FileContext(AbstractBuilder<?, ?> builder) {
        this.name = builder.name;
        this.options = builder.options;
    }

    /**
     * Get file name.
     *
     * @return the {@code FileName}
     * @since 1.0.0
     */
    public FileName getName() {

        return name;
    }

    /**
     * Get file system configurations.
     *
     * @return file system configurations
     * @since 1.0.0
     */
    public List<FileOption> getOptions() {

        return options;
    }

    /**
     * Returns the absolute URI of this file.
     *
     * @return URI of this file
     * @since 1.0.0
     */
    public URI toUri() {

        return URI.create(name.getURI());
    }

    /**
     * Build a new {@code FileObject} from this file context.
     *
     * @param fsm the {@code FileSystemManager}
     * @return the {@code FileObject} from this file context
     * @throws FileSystemException if occurs an error during the {@code FileObject} building
     * @since 1.0.0
     */
    public FileObject toFileObject(FileSystemManager fsm) throws FileSystemException {

        Objects.requireNonNull(fsm);

        var fsOpts = new FileSystemOptions();

        for (FileOption o : options) {
            o.apply(fsOpts);
        }

        return fsm.resolveFile(name.getURI(), fsOpts);
    }

    /**
     * Returns a JSON representation of this file information.
     *
     * @return a JSON representation of this file information
     * @since 1.0.0
     */
    public JsonObject toJsonObject() {

        JsonObject optsJson = options.stream().map(o -> Map.entry(o.getName(), o.getValue()))
                .collect(JsonCollectors.toJsonObject());

        return Json.createObjectBuilder().add("filename", name.getURI()).add("options", optsJson).build();
    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value
     * @since 1.0.0
     */
    @Override
    public int hashCode() {

        return Objects.hash(name, options);

    }

    /**
     * Indicates that other object is equal to this one.
     *
     * @param other an any object
     * @return {@code true} if equals otherwise {@code false}
     * @since 1.0.0
     */
    @Override
    public boolean equals(Object other) {

        return other instanceof FileContext o && Objects.equals(name, o.name) && Objects.equals(options, o.options);

    }

    /**
     * Returns a JSON representation of this file information.
     *
     * @return a JSON representation of this file information
     * @since 1.0.0
     */
    @Override
    public String toString() {

        return toJsonObject().toString();
    }

    protected static class AbstractBuilder<V extends FileContext, B extends AbstractBuilder<V, B>> {

        protected final Class<B> builderType;
        protected final FileName name;
        protected List<FileOption> options = List.of();

        AbstractBuilder(Class<B> builderType, FileName name) {

            this.builderType = Objects.requireNonNull(builderType);
            this.name = Objects.requireNonNull(name);
        }

        /**
         * Set list of {@code FileOption}.
         *
         * @param options list of {@code FileOption}
         * @return updated this
         * @throws NullPointerException if {@code options} is {@code null}, or it contain any {@code null} elements.
         * @since 1.0.0
         */
        public B withOptions(List<FileOption> options) {

            this.options = List.copyOf(options);
            return builderType.cast(this);
        }
    }

    /**
     * Builder of the {@link FileContext}.
     *
     * <p>
     * Implementation requirements.
     * <ul>
     * <li>This class is mutable and non thread-safe.</li>
     * </ul>
     *
     * @author riru
     * @version 1.0.0
     * @since 1.0.0
     */
    public static class Builder extends AbstractBuilder<FileContext, Builder> {

        /**
         * Construct with {@code FileName}.
         *
         * @param name the {@code FileName}
         * @throws NullPointerException if {@code name} is {@code null}
         * @since 1.0.0
         */
        public Builder(FileName name) {

            super(Builder.class, name);
        }

        /**
         * Build a new instance.
         *
         * @return new instance
         * @since 1.0.0
         */
        public FileContext build() {

            return new FileContext(this);
        }
    }
}
