package ecdcpipeline

import ecdcpipeline.ImageRemovalFilter

class ImageRemovalFilterTest extends GroovyTestCase {
  def sampleImages = [
    'id1': 'repo1/image1:tag1',
    'id2': 'repo1/image1:tag2',
    'id3': 'repo1/image2:tag3',
    'id4': 'repo2/image3:tag4',
    'id5': '<none>:<none>'
  ]

  def imageNamesToKeep = ['repo1/image1:tag2', 'repo2/image3:tag4']

  void testImageRemoval() {
    def irf = new ImageRemovalFilter(imageNamesToKeep)
    def imagesToRemove = irf.getFilteredImageIDs(sampleImages)

    assertEquals(imagesToRemove.size(), 3)
    assertTrue('id1' in imagesToRemove)
    assertTrue('id3' in imagesToRemove)
    assertTrue('id5' in imagesToRemove)
  }

  void testImageRemovalWithEmptyKeepList() {
    def irf = new ImageRemovalFilter([])
    def imagesToRemove = irf.getFilteredImageIDs(sampleImages)

    assertEquals(imagesToRemove.size(), 5)
    assertTrue('id2' in imagesToRemove)
    assertTrue('id4' in imagesToRemove)
  }

}
