package ecdcpipeline

import ecdcpipeline.ImageRemovalFilter

class ImageRemovalFilterTest extends GroovyTestCase {
  def sampleImageNames = [
    'repo1/image1:tag1',
    'repo1/image1:tag2',
    'repo1/image2:tag3',
    'repo2/image3:tag4'
  ]

  def imageNamesToKeep = ['repo1/image1:tag2', 'repo2/image3:tag4']

  void testImageRemoval() {
    def irf = new ImageRemovalFilter(imageNamesToKeep)
    def imageNamesToRemove = irf.getFilteredIDsFromImages(sampleImageNames)

    assertEquals(imageNamesToRemove.size(), 2)
    assertTrue('repo1/image1:tag1' in imageNamesToRemove)
    assertTrue('repo1/image2:tag3' in imageNamesToRemove)
  }

  void testImageRemovalWithEmptyKeepList() {
    def irf = new ImageRemovalFilter([])
    def imageNamesToRemove = irf.getFilteredIDsFromImages(sampleImageNames)

    assertEquals(imageNamesToRemove.size(), 4)
    assertTrue('repo1/image1:tag2' in imageNamesToRemove)
    assertTrue('repo2/image3:tag4' in imageNamesToRemove)
  }

}
