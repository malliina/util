package com.mle.jdbc.tests

import com.mle.jdbc.{Database, DefaultSettings}

/**
 *
 * @author mle
 */
object TestDb extends Database(DefaultSettings.connProvider)