/*
 * Copyright 2022 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov

import org.scalacheck.Gen
import uk.gov.hmrc.Page.{ContentPage, DownloadPdf, FormPage}
import uk.gov.hmrc.performance.conf.ServicesConfiguration

package object hmrc extends ServicesConfiguration {

  val baseUrl: String = baseUrlFor("declare-interests-in-joint-property-frontend")
  val route: String = "/fill-online/declare-interests-in-joint-property"

  private val answerYes = "value" -> "true"
  private val answerNo = "value" -> "false"

  private val arbitraryNino: Gen[String] =
    for {
      firstChar <- Gen.oneOf('A', 'C', 'E', 'H', 'J', 'L', 'M', 'O', 'P', 'R', 'S', 'W', 'X', 'Y').map(_.toString)
      secondChar <-
        Gen
          .oneOf('A', 'B', 'C', 'E', 'G', 'H', 'J', 'K', 'L', 'M', 'N', 'P', 'R', 'S', 'T', 'W', 'X', 'Y', 'Z')
          .map(_.toString)
      digits <- Gen.listOfN(6, Gen.numChar)
      lastChar <- Gen.oneOf('A', 'B', 'C', 'D')
    } yield firstChar ++ secondChar ++ digits :+ lastChar

  private val nino = arbitraryNino.sample.get

  val journey: List[Page] = List(
    ContentPage("Navigate To Start Page", ""),
    FormPage("Applicant Name", "your-name", "firstName" -> "first", "lastName" -> "last"),
    FormPage("Applicant Nino", "your-national-insurance-number", "value" -> nino),
    FormPage("Applicant Has UTR", "you-have-unique-taxpayer-reference", answerNo),
    FormPage("Partner Name", "partner-name", "firstName" -> "first", "lastName" -> "last"),
    FormPage("Partner Nino", "partner-national-insurance-number", "value" -> nino),
    FormPage("Partner Has UTR", "partner-has-unique-taxpayer-reference", answerNo),
    FormPage("Current Address in UK", "current-address-in-uk", answerYes),
    FormPage("Current Address", "current-uk-address", "line1" -> "1 Test Street", "townOrCity" -> "Test Town", "postcode" -> "ZZ1 1ZZ"),
    FormPage("Property Address", "property-address/1", "line1" -> "1 Test Street", "townOrCity" -> "Test Town", "postcode" -> "ZZ1 1ZZ"),
    FormPage("Share of Property", "share-of-property/1", "value" -> "55"),
    ContentPage("Check Property Details", "check-property/1"),
    FormPage("Add Property", "add-property", answerNo),
    ContentPage("Check Your Answers", "check-your-answers"),
    ContentPage("Next steps", "next-steps"),
    DownloadPdf("Download PDF", "print-form")
  )
}
