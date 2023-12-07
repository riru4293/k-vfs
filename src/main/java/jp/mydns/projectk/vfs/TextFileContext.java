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
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;

/**
 * This is a {@code FileContext} for text file. It has properties for character code and end-of-line character.
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
public class TextFileContext extends FileContext {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String DEFAULT_LINE_END = "\n";

    private final Charset charset;
    private final String lineEnd;

    /**
     * Construct a new instance from JSON.
     *
     * @param fileCtx a JSON representation of the {@code TextFileContext}
     * <p>
     * JSON Format<pre><code>
     * {
     *     "filename": "A URI representation file"
     *    ,"options": {
     *         "An option name": "An option value"
     *     }
     *    ,"charset": "character code name"
     *    ,"lineEnd": "line ending characters"
     * }
     * </code></pre>
     *
     * @param fsm the {@code FileSystemManager}
     * @return a new {@code TextFileContext}
     * @throws FileSystemException if filename is invalid
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if invalid value inside {@code fileCtx}
     * @since 1.0.0
     */
    public static TextFileContext of(JsonObject fileCtx, FileSystemManager fsm) throws FileSystemException {

        Objects.requireNonNull(fileCtx);
        Objects.requireNonNull(fsm);

        FileContext base = FileContext.of(fileCtx, fsm);

        try {

            Charset charset = Optional.ofNullable(fileCtx.getJsonString("charset"))
                    .map(JsonString::getString).map(Charset::forName).orElse(DEFAULT_CHARSET);

            String lineEnd = Optional.ofNullable(fileCtx.getJsonString("lineEnd"))
                    .map(JsonString::getString).orElse(DEFAULT_LINE_END);

            return new Builder(base.getName()).withOptions(base.getOptions())
                    .withCharset(charset).withLineEnd(lineEnd).build();

        } catch (ClassCastException ex) {

            throw new IllegalArgumentException("Malformed JSON as text file context.", ex);

        } catch (IllegalCharsetNameException | UnsupportedCharsetException ex) {

            throw new IllegalArgumentException("Invalid character code name.", ex);

        }
    }

    protected TextFileContext(AbstractBuilder<?, ?> builder) {

        super(builder);

        this.charset = builder.charset;
        this.lineEnd = builder.lineEnd;
    }

    /**
     * Get character code.
     *
     * @return character code
     * @since 1.0.0
     */
    public Charset getCharset() {

        return charset;

    }

    /**
     * Get line end characters.
     *
     * @return line end characters
     * @since 1.0.0
     */
    public String getLineEnd() {

        return lineEnd;

    }

    /**
     * Returns a JSON representation.
     *
     * @return a JSON representation
     * @since 1.0.0
     */
    @Override
    public JsonObject toJsonObject() {

        return Json.createObjectBuilder(super.toJsonObject())
                .add("charset", charset.name()).add("lineEnd", lineEnd).build();

    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value
     * @since 1.0.0
     */
    @Override
    public int hashCode() {

        return Objects.hash(getName(), getOptions(), charset, lineEnd);

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

        return other instanceof TextFileContext o && super.equals(other)
                && Objects.equals(charset, o.charset) && Objects.equals(lineEnd, o.lineEnd);

    }

    protected static class AbstractBuilder<C extends TextFileContext, B extends AbstractBuilder<C, B>>
            extends FileContext.AbstractBuilder<C, B> {

        protected Charset charset = DEFAULT_CHARSET;
        protected String lineEnd = DEFAULT_LINE_END;

        AbstractBuilder(Class<B> b, FileName name) {
            super(b, name);
        }

        /**
         * Set a file character code.
         *
         * @param charset the {@code Charset}
         * @return updated this
         * @throws NullPointerException if {@code charset} is {@code null}
         * @since 1.0.0
         */
        public B withCharset(Charset charset) {
            this.charset = Objects.requireNonNull(charset);
            return builderType.cast(this);
        }

        /**
         * Set line ending characters.
         *
         * @param lineEnd line ending characters
         * @return updated this
         * @throws NullPointerException if {@code lineEnd} is {@code null}
         * @since 1.0.0
         */
        public B withLineEnd(String lineEnd) {
            this.lineEnd = Objects.requireNonNull(lineEnd);
            return builderType.cast(this);
        }
    }

    /**
     * Builder of the {@link TextFileContext}.
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
    public static class Builder extends AbstractBuilder<TextFileContext, Builder> {

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
        public TextFileContext build() {
            return new TextFileContext(this);
        }
    }
}
