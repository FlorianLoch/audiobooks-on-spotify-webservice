package ch.fdlo.hoerbuchspion.webservice

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals

class UnitTest {
  @Test
  fun welcome() {
    val controller = Controller()
    assertEquals("Welcome to Jooby!", controller.sayHi())
  }
}
