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

import java.net.URI;
import org.apache.commons.vfs2.FileName;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.FileSystemManagerParameterResolver;

/**
 * Test of class FileNameUtils.
 *
 * @author riru
 * @version 1.0.0
 * @since 1.0.0
 */
@ExtendWith(FileSystemManagerParameterResolver.class)
class FileNameUtilsTest {

    /**
     * Test of fromUri method.
     *
     * @since 1.0.0
     */
    @Test
    void testFromUri(FileSystemManager fsm) {

        URI uri = URI.create("file:///home/foo/.profile");

        FileName fn = FileNameUtils.fromUri(uri, fsm);

        assertThat(fn.getURI()).isEqualTo("file:///home/foo/.profile");
    }

    /**
     * Test of fromUri method. If no support scheme.
     *
     * @since 1.0.0
     */
    @Test
    void testFromUri_NoSupportScheme(FileSystemManager fsm) {

        URI noSupport = URI.create("xxx://localhost/file");

        assertThatIllegalArgumentException().isThrownBy(() -> FileNameUtils.fromUri(noSupport, fsm))
                .withMessage("Illegal URI as FileName. [xxx://localhost/file]")
                .withCauseInstanceOf(FileSystemException.class);
    }

    /**
     * Test of resolve method.
     *
     * @since 1.0.0
     */
    @Test
    void testResolve(FileSystemManager fsm) throws Exception {

        FileName expect1 = fsm.resolveURI("file:///home/var/.history");
        FileName baseFn1 = fsm.resolveURI("file:///home/foo/.profile");
        FileName result1 = FileNameUtils.resolve(baseFn1, "../../var/.history", fsm);
        assertThat(result1).isEqualTo(expect1);

        FileName expect2 = fsm.resolveURI("file:///usr/bin/ls");
        FileName baseFn2 = fsm.resolveURI("file:///usr/bin/");
        FileName result2 = FileNameUtils.resolve(baseFn2, "./ls", fsm);
        assertThat(result2).isEqualTo(expect2);

        FileName expect3 = fsm.resolveURI("file:///usr/bin/ls");
        FileName baseFn3 = fsm.resolveURI("file:///bin/ls");
        FileName result3 = FileNameUtils.resolve(baseFn3, "/usr/bin/ls", fsm);
        assertThat(result3).isEqualTo(expect3);

    }

    /**
     * Test of resolve method. If malformed relative name.
     *
     * @since 1.0.0
     */
    @Test
    void testResolve_MalformedName(FileSystemManager fsm) throws Exception {

        FileName baseFn = fsm.resolveURI("file:///var/log/");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileNameUtils.resolve(baseFn, "file:///", fsm))
                .withMessageStartingWith("Can't resolve a relative file name.")
                .withCauseInstanceOf(FileSystemException.class);

    }

    /**
     * Test of getParent method.
     *
     * @since 1.0.0
     */
    @Test
    void testGetParent(FileSystemManager fsm) throws Exception {

        FileName expect = fsm.resolveURI("file:///home");

        FileName baseFn = fsm.resolveURI("file:///home/foo");

        FileName result = FileNameUtils.getParent(baseFn);

        assertThat(result).isEqualTo(expect);

    }

    /**
     * Test of getParent method. If already root directory.
     *
     * @since 1.0.0
     */
    @Test
    void testGetParent_RootDirectory(FileSystemManager fsm) throws Exception {

        FileName root = fsm.resolveURI("file:///");

        assertThatIllegalArgumentException()
                .isThrownBy(() -> FileNameUtils.getParent(root))
                .withMessage("Can't get parent directory. Because already root directory.");

    }

    /**
     * Test of appendSuffix method.
     *
     * @since 1.0.0
     */
    @Test
    void testAppendSuffix(FileSystemManager fsm) throws Exception {

        FileName expect = fsm.resolveURI("file:///tmp/file.ext");

        FileName fn = fsm.resolveURI("file:///tmp/file");

        FileName result = FileNameUtils.appendSuffix(fn, ".ext", fsm);

        assertThat(result).isEqualTo(expect);

    }
}
