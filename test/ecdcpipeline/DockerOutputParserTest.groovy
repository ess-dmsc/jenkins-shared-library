package ecdcpipeline

import ecdcpipeline.DockerOutputParser

class DockerOutputParserTest extends GroovyTestCase {
  def sampleImagesOutput = (
    "repo1/image1:tag1\n"
    + "repo1/image1:tag2\n"
    + "repo1/image2:tag3\n"
    + "repo2/image3:tag4"
  )

  void testParseImages() {
    def dop = new DockerOutputParser()
    def images = dop.parseImages(sampleImagesOutput)

    assertEquals(images.size(), 4)
    assertTrue(images.contains('repo1/image1:tag1'))
    assertTrue(images.contains('repo1/image1:tag2'))
    assertTrue(images.contains('repo1/image2:tag3'))
    assertTrue(images.contains('repo2/image3:tag4'))
  }

  void testParseImagesWithEmptyInput() {
    def dop = new DockerOutputParser()
    def images = dop.parseImages("")

    assertEquals(images.size(), 0)
  }
}
