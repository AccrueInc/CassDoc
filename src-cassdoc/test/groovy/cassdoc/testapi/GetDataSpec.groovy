package cassdoc.testapi

import spock.lang.Specification

class GetDataSpec extends Specification {
    void "test if get resource from classpath works"() {
        when:
        String doc = this.class.classLoader.getResourceAsStream('cassdoc/testdata/SimpleDoc.json')?.getText()
        then:
        doc != null
    }
}
