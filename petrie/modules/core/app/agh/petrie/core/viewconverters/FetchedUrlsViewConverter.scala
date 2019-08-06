package agh.petrie.core.viewconverters

import javax.inject.Singleton
import agh.petrie.core.model.view.FetchedUrlsView
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.FetchedUrls
import io.scalaland.chimney.dsl._

@Singleton
class FetchedUrlsViewConverter {

  def toView(fetchedUrls: FetchedUrls) = {
    fetchedUrls.transformInto[FetchedUrlsView]
  }
}
