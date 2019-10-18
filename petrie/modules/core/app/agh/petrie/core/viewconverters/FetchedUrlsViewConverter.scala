package agh.petrie.core.viewconverters

import javax.inject.Singleton
import agh.petrie.core.model.view.FetchedDataView
import agh.petrie.scraping.actors.receptionist.SimpleReceptionist.FetchedData
import io.scalaland.chimney.dsl._

@Singleton
class FetchedUrlsViewConverter {

  def toView(fetchedUrls: FetchedData) =
    fetchedUrls.transformInto[FetchedDataView]
}
