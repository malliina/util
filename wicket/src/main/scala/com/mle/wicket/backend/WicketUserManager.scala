package com.mle.wicket.backend

import com.mle.auth.UserManager

/**
 *
 * @author mle
 */
trait WicketUserManager extends UserManager[String] with UserProvider