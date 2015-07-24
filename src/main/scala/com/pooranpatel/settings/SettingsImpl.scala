package com.pooranpatel
package settings

import akka.actor.Extension
import com.typesafe.config.Config

/**
 * Settings of an application
 */
class SettingsImpl(config: Config) extends Extension {
  val eventSourceConnectionPort = config.getInt("app.event-source-connection-port")
  val userClientConnectionPort = config.getInt("app.user-client-connection-port")
  val eventsSeparator = config.getString("app.events-separator")
  val eventFieldsSeparator = config.getString("app.event-fields-separator")
}