package modules.cake

import services.UserServiceInMemory

/**
 * Provides the user service
 */
trait UserServiceModule {

  lazy val userService = new UserServiceInMemory

}