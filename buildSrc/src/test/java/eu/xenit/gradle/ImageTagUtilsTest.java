package eu.xenit.gradle;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

import org.junit.jupiter.api.Test;

class ImageTagUtilsTest {

    @Test
    void toTags() {
        assertThat(ImageTagUtils.toTags("7.15.132"), containsInAnyOrder("7", "7.15", "7.15.132"));
        assertThat(ImageTagUtils.toTags(""), empty());
        assertThat(ImageTagUtils.toTags(null), empty());
    }

}