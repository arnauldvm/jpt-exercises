package jpt.app01

import scala.concurrent.duration._

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._

class RecordedSimulation extends Simulation {

    val httpProtocol = http
        .baseURL("http://localhost:7666")
        .inferHtmlResources(WhiteList(""".*7666.*"""), BlackList())

    val uri1 = "http://localhost:7666"

    val scn = scenario("RecordedSimulation")
        .exec(http("Display login form")
            .get("/")
            .check(
                status.is(200),
                currentLocationRegex(".*/login"),
                regex("(?i)<form\\s+action=['\"]login['\"]")
                )
                        )
        //.pause(11)
        .exec(http("Submit credentials")
            .post("/login")
            .formParam("userid", "scott")
            .formParam("password", "tiger")
            .check(
                status.is(200),
                currentLocationRegex(".*/index.html"),
                substring("Scott&nbsp;Tiger"),
                regex("(?i)<form\\s+action=['\"]search['\"]")
                )
                        )
                .repeat(100) {
            //pause(3).
            randomSwitch(
                80d -> exec(http("Search")
                        .get("/search?key=java")
                        .check(
                            status.is(200),
                            substring("List of languages matching <B>java</B>"),
                            substring("<A href=\"/language?name=JavaScript\"><B>JavaScript</B></A>")
                                 // "<A href=\"/language?name=JavaScript\"><B>JavaScript</B></A>"
                            )
                                    )
                    //.pause(2)
                    .exec(http("Display language")
                        .get("/language?name=JavaScript")
                        .check(
                            status.is(200),
                            substring("<B>JavaScript</B>")
                            )
                                    )
                    ,
                20d -> exec(http("Search & Display language")
                        .get("/search?key=javascript")
                        .check(
                            status.is(200),
                            substring("<B>JavaScript</B>")
                            )
                        )
                )
         }

    //setUp(scn.inject(atOnceUsers(1))).protocols(httpProtocol)
    //setUp(scn.inject(constantUsersPerSec(100) during(5 minutes))).throttle(
    setUp(scn.inject(rampUsers(100) over (1 minutes))).throttle(
          reachRps(10) in (60 seconds),
          holdFor(3 minute)
        ).protocols(httpProtocol)
        
}
