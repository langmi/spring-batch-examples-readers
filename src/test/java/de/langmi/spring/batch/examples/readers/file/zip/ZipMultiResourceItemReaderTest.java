/**
 * Copyright 2011 Michael R. Lange <michael.r.lange@langmi.de>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.langmi.spring.batch.examples.readers.file.zip;

import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.PassThroughLineMapper;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * ZipMultiResourceItemReaderTest, without Spring context.
 *
 * @author Michael R. Lange <michael.r.lange@langmi.de>
 */
public class ZipMultiResourceItemReaderTest {

    private final Logger LOG = LoggerFactory.getLogger(getClass());
    private static final String ZIP_INPUT_SINGLE_FILE = "src/test/resources/input/archive/input.txt.zip";
    private static final String ZIP_INPUT_MULTIPLE_FILES = "src/test/resources/input/archive/input-mixed-files.zip";
    private static final String ZIP_INPUT_NESTED_DIRS = "src/test/resources/input/archive/input-nested-dir.zip";

    /**
     * Test with one ZIP file containing one text file with 20 lines.
     *
     * @throws Exception 
     */
    @Test
    public void testOneZipFile() throws Exception {
        LOG.debug("testOneZipFile");
        ZipMultiResourceItemReader<String> mReader = new ZipMultiResourceItemReader<String>();
        // setup multResourceReader
        mReader.setArchives(new Resource[]{new FileSystemResource(ZIP_INPUT_SINGLE_FILE)});

        // call general setup last
        generalMultiResourceReaderSetup(mReader);

        // open with mock context
        mReader.open(MetaDataInstanceFactory.createStepExecution().getExecutionContext());

        // read
        try {
            String item = null;
            int count = 0;
            do {
                item = mReader.read();
                if (item != null) {
                    assertEquals(String.valueOf(count), item);
                    count++;
                }
            } while (item != null);
            assertEquals(20, count);
        } catch (Exception e) {
            throw e;
        } finally {
            mReader.close();
        }
    }

    /**
     * Test with zip file with nested directories, contains 4 text files with
     * 20 lines each.
     * 
     * @throws Exception 
     */
    @Test
    public void testOneZipFileNestedDirs() throws Exception {
        LOG.debug("testOneZipFileNestedDirs");
        ZipMultiResourceItemReader<String> mReader = new ZipMultiResourceItemReader<String>();
        // setup multResourceReader
        mReader.setArchives(new Resource[]{new FileSystemResource(ZIP_INPUT_NESTED_DIRS)});

        // call general setup last
        generalMultiResourceReaderSetup(mReader);

        // open with mock context
        mReader.open(MetaDataInstanceFactory.createStepExecution().getExecutionContext());

        // read
        try {
            String item = null;
            int count = 0;
            do {
                item = mReader.read();
                if (item != null) {
                    count++;
                }
            } while (item != null);
            assertEquals(80, count);
        } catch (Exception e) {
            throw e;
        } finally {
            mReader.close();
        }
    }

    /**
     * Test with multiple zip files, together they contain 6 text files with 20 
     * lines each.
     *
     * @throws Exception 
     */
    @Test
    public void testMultipleZipFiles() throws Exception {
        LOG.debug("testMultipleTarFiles");
        ZipMultiResourceItemReader<String> mReader = new ZipMultiResourceItemReader<String>();
        // setup multResourceReader
        mReader.setArchives(
                new Resource[]{
                    new FileSystemResource(ZIP_INPUT_MULTIPLE_FILES),
                    new FileSystemResource(ZIP_INPUT_SINGLE_FILE),
                    new FileSystemResource(ZIP_INPUT_NESTED_DIRS)});

        // call general setup last
        generalMultiResourceReaderSetup(mReader);

        // open with mock context
        mReader.open(MetaDataInstanceFactory.createStepExecution().getExecutionContext());

        // read
        try {
            String item = null;
            int count = 0;
            do {
                item = mReader.read();
                if (item != null) {
                    count++;
                }
            } while (item != null);
            assertEquals(140, count);
        } catch (Exception e) {
            throw e;
        } finally {
            mReader.close();
        }
    }

    /**
     * Helper method to setup the used MultiResourceItemReader.
     *
     * @param mReader
     * @throws Exception 
     */
    private void generalMultiResourceReaderSetup(ZipMultiResourceItemReader<String> mReader) throws Exception {
        // setup delegate
        FlatFileItemReader<String> reader = new FlatFileItemReader<String>();
        reader.setLineMapper(new PassThroughLineMapper());
        mReader.setDelegate(reader);
    }
}
