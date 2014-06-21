package com.kufli.log

/**
 * Mixin trait with a single `log` member providing a simple logging API
 * using slf4j for actual logging.
 */

trait Logging {
  /** Instance logger object */
  protected lazy val log = Logger(getClass)
}
