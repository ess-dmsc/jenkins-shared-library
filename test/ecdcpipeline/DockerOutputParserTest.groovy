package ecdcpipeline

import ecdcpipeline.DockerOutputParser

class DockerOutputParserTest extends GroovyTestCase {
  def sampleImagesOutput = (
    "id1;repo1/image1:tag1\n"
    + "id2;repo1/image1:tag2\n"
    + "id3;repo1/image2:tag3\n"
    + "id4;repo2/image3:tag4\n"
    + "id5;<none>:<none>"
  )

  void testParseImages() {
    def dop = new DockerOutputParser()
    def images = dop.parseImages(sampleImagesOutput)

    assertEquals(images.size(), 5)
    assertTrue(images.containsKey('id1'))
    assertTrue(images.containsKey('id2'))
    assertTrue(images.containsKey('id3'))
    assertTrue(images.containsKey('id4'))
    assertTrue(images.containsKey('id5'))
    assertEquals(images['id1'], 'repo1/image1:tag1')
    assertEquals(images['id2'], 'repo1/image1:tag2')
    assertEquals(images['id3'], 'repo1/image2:tag3')
    assertEquals(images['id4'], 'repo2/image3:tag4')
    assertEquals(images['id5'], '<none>:<none>')
  }

  void testParseImagesWithEmptyInput() {
    def dop = new DockerOutputParser()
    def images = dop.parseImages("")

    assertEquals(images.size(), 0)
  }
}
