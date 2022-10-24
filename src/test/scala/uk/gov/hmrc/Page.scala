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

package uk.gov.hmrc

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.http.request.builder.HttpRequestBuilder

abstract class Page {
  def name: String
  def url: String
  def requests(next: Option[Page]): List[HttpRequestBuilder]
}

object Page {

  final case class ContentPage(name: String, url: String) extends Page {
    override def requests(next: Option[Page]): List[HttpRequestBuilder] = List(
      http(name)
        .get(s"$baseUrl$route/$url")
        .check(status.is(200))
    )
  }

  final case class DownloadPdf(name: String, url: String) extends Page {
    override def requests(next: Option[Page]): List[HttpRequestBuilder] = List(
      http(name)
        .get(s"$baseUrl$route/$url")
        .check(status.is(200))
    )
  }

  final case class FormPage(name: String, url: String, data: (String, String)*) extends Page {
    override def requests(next: Option[Page]): List[HttpRequestBuilder] = {

      require(next.isDefined, "Form pages must have a next page in the journey")

      List(
        http(s"Get $name")
          .get(s"$baseUrl$route/$url")
          .check(status.is(200))
          .check(css("input[name=csrfToken]", "value").saveAs("csrfToken")),
        data.foldLeft(http(s"Post $name")
          .post(s"$baseUrl$route/$url")
          .formParam("csrfToken", s"$${csrfToken}")) { case (builder, (key, value)) =>
          builder.formParam(key, value)
        }.check(status.is(303))
          .check(header("Location").is(s"$route/${next.get.url}").saveAs(next.toString))
      )
    }
  }
}
