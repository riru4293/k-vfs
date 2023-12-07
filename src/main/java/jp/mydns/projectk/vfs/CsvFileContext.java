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
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.JsonValue;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;

/**
 * This is a {@code TextFileContext} for CSV file.
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
public class CsvFileContext extends TextFileContext {

    private static final char DEFAULT_SEPARATOR = ',';
    private static final char DEFAULT_QUOTE = '"';
    private static final char DEFAULT_ESCAPE = '\\';

    private final char separator;
    private final char quote;
    private final char escape;
    private final List<List<String>> headers;

    /**
     * Construct a new instance from JSON.
     *
     * @param fileCtx a JSON representation of the {@code CsvFileContext}
     * <p>
     * JSON Format<pre><code>
     * {
     *     "filename": "A URI representation file"
     *    ,"options": {
     *         "An option name": "An option value"
     *     }
     *    ,"charset"    : "charset name"
     *    ,"lineEnd"    : "line end characters"
     *    ,"separator"  : "CSV separator char"
     *    ,"quote"      : "CSV quote char"
     *    ,"escape"     : "CSV escape char"
     *    ,"headers"    : [
     *        [ "hdr-r1-c1", "hdr-r1-c2" ]
     *       ,[ "hdr-r2-c1", "hdr-r2-c2" ]
     *    ]
     * }
     * </code></pre>
     *
     * @param fsm the {@code FileSystemManager}
     * @return a new {@code FileContext}
     * @throws FileSystemException if filename is invalid.
     * @throws NullPointerException if any argument is {@code null}
     * @throws IllegalArgumentException if invalid value inside {@code fileCtx}
     * @since 1.0.0
     */
    public static CsvFileContext of(JsonObject fileCtx, FileSystemManager fsm) throws FileSystemException {

        Objects.requireNonNull(fileCtx);
        Objects.requireNonNull(fsm);

        TextFileContext base = TextFileContext.of(fileCtx, fsm);

        try {

            char separator = resolveChar(fileCtx, "separator", DEFAULT_SEPARATOR);
            char quote = resolveChar(fileCtx, "quote", DEFAULT_QUOTE);
            char escape = resolveChar(fileCtx, "escape", DEFAULT_ESCAPE);

            JsonArray headersJson = fileCtx.getJsonArray("headers");

            Function<JsonArray, List<String>> toHeader = a -> a.stream().map(JsonString.class::cast)
                    .map(JsonString::getString).toList();

            List<List<String>> headers = Optional.ofNullable(headersJson).map(List::stream)
                    .map(s -> s.map(JsonValue::asJsonArray)).map(s -> s.map(toHeader)).map(Stream::toList)
                    .orElseGet(List::of);

            return new Builder(base.getName()).withOptions(base.getOptions()).withCharset(base.getCharset())
                    .withLineEnd(base.getLineEnd()).withSeparator(separator).withQuote(quote)
                    .withEscape(escape).withHeaders(headers).build();

        } catch (ClassCastException ex) {

            throw new IllegalArgumentException("Malformed JSON as CSV file context.", ex);

        }
    }

    private static char resolveChar(JsonObject json, String key, char defalutValue) {

        try {

            return Optional.ofNullable(json.getJsonString(key)).map(JsonString::getString)
                    .map(s -> s.charAt(0)).orElse(defalutValue);

        } catch (IndexOutOfBoundsException ex) {

            throw new IllegalArgumentException("No found CSV %s.".formatted(key), ex);

        }
    }

    protected CsvFileContext(AbstractBuilder<?, ?> builder) {
        super(builder);
        this.separator = builder.separator;
        this.escape = builder.escape;
        this.quote = builder.quote;
        this.headers = builder.headers;
    }

    /**
     * Get CSV separator.
     *
     * @return CSV separator
     * @since 1.0.0
     */
    public char getSeparator() {
        return separator;
    }

    /**
     * Get CSV quote character.
     *
     * @return CSV quote
     * @since 1.0.0
     */
    public char getQuote() {
        return quote;
    }

    /**
     * Get CSV escape character.
     *
     * @return CSV escape character
     * @since 1.0.0
     */
    public char getEscape() {
        return escape;
    }

    /**
     * Get CSV header column names.
     *
     * @return CSV headers
     * @since 1.0.0
     */
    public List<List<String>> getHeaders() {
        return headers.stream().map(List::copyOf).toList();
    }

    /**
     * Returns a JSON representation.
     *
     * @return a JSON representation
     * @since 1.0.0
     */
    @Override
    public JsonObject toJsonObject() {

        return Json.createObjectBuilder(super.toJsonObject()).add("separator", String.valueOf(separator))
                .add("quote", String.valueOf(quote)).add("escape", String.valueOf(escape))
                .add("headers", Json.createArrayBuilder(headers)).build();

    }

    /**
     * Returns a hash code value.
     *
     * @return a hash code value
     * @since 1.0.0
     */
    @Override
    public int hashCode() {

        return Objects.hash(getName(), getOptions(), getCharset(), getLineEnd(), separator, quote, escape, headers);

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

        return other instanceof CsvFileContext o && super.equals(other)
                && Objects.equals(separator, o.separator) && Objects.equals(quote, o.quote)
                && Objects.equals(escape, o.escape) && Objects.equals(headers, o.headers);

    }

    protected static class AbstractBuilder<C extends CsvFileContext, B extends AbstractBuilder<C, B>>
            extends TextFileContext.AbstractBuilder<C, B> {

        protected char separator = DEFAULT_SEPARATOR;
        protected char quote = DEFAULT_QUOTE;
        protected char escape = DEFAULT_ESCAPE;
        protected List<List<String>> headers = List.of();

        AbstractBuilder(Class<B> b, FileName name) {
            super(b, name);
        }

        /**
         * Set CSV separator.
         *
         * @param separator CSV separator
         * @return updated this
         * @since 1.0.0
         */
        public final B withSeparator(char separator) {
            this.separator = separator;
            return builderType.cast(this);
        }

        /**
         * Set CSV quote character.
         *
         * @param quote quote character
         * @return updated this
         * @since 1.0.0
         */
        public final B withQuote(char quote) {
            this.quote = quote;
            return builderType.cast(this);
        }

        /**
         * Set CSV escape character.
         *
         * @param escape escape character
         * @return updated this
         * @since 1.0.0
         */
        public final B withEscape(char escape) {
            this.escape = escape;
            return builderType.cast(this);
        }

        /**
         * Set CSV headers.
         *
         * @param headers header column names
         * @return updated this
         * @throws NullPointerException if {@code headers} is {@code null}, or it contain any {@code null} elements.
         * @since 1.0.0
         */
        public B withHeaders(List<List<String>> headers) {

            this.headers = Objects.requireNonNull(headers).stream().map(List::copyOf).toList();
            return builderType.cast(this);

        }
    }

    /**
     * Builder of the {@link CsvFileContext}.
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
    public static class Builder extends AbstractBuilder<CsvFileContext, Builder> {

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
        public CsvFileContext build() {
            return new CsvFileContext(this);
        }
    }
}
