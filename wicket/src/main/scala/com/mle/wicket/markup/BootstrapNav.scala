package com.mle.wicket.markup

import de.agilecoders.wicket.markup.html.bootstrap.navbar.{NavbarDropDownButton, NavbarButton, Navbar}
import com.mle.wicket.model.{LDModel, ReadOnlyModel}
import de.agilecoders.wicket.markup.html.bootstrap.navbar.Navbar.ButtonPosition
import org.apache.wicket.model.Model
import org.apache.wicket.{Page, MarkupContainer}
import com.mle.wicket.markup.Pages._
import de.agilecoders.wicket.Bootstrap
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.ajax.AjaxRequestTarget
import de.agilecoders.wicket.markup.html.bootstrap.button.{ButtonBehavior, ButtonType}
import com.mle.util.Log
import collection.JavaConversions._

/**
 * test: tooltibhehavior, popoverbehavior
 * @author Mle
 */
trait BootstrapNav extends MarkupContainer with Log {
  val themesModel = LDModel(bootstrapSettings.getThemeProvider.available().map(_.name()))
  val navbar = new Navbar("navBar")
  navbar.fluid()
  navbar.brandName(ReadOnlyModel("My app"))
  navbar.addButton(ButtonPosition.LEFT,
    navButton(classOf[Home], "Home"),
    navButton(classOf[WebSocketsPage], "Web Sockets"),
    navButton(classOf[AtmospherePage], "Atmosphere"),
    navButton(classOf[SortPage], "Sorting"),
    navButton(classOf[SettingsPage], "Settings"),
    navButton(classOf[BootstrapPage], "Bootstrap")
  )
  navbar.addButton(ButtonPosition.RIGHT,
    new NavbarDropDownButton("button", Model.of("Themes"))
      .addButtons(themesModel.getObject.map(themeDropDownButton): _*)
  )
  add(navbar)

  def navButton[T <: Page](pageClass: Class[T], label: String) = {
    new NavbarButton(pageClass, Model.of(label))
  }

  def themeDropDownButton(themeName: String) = dropDownButton(themeName)(target => {
    bootstrapSettings.getActiveThemeProvider.setActiveTheme(themeName)
    target add getPage
  })

  def dropDownButton(label: String)(onClicked: AjaxRequestTarget => Unit) = {
    new AjaxLink("button", Model.of(label)) {
      override def onInitialize() {
        super.onInitialize()
        setBody(getDefaultModel)
        add(new ButtonBehavior(ButtonType.Menu))
      }

      def onClick(target: AjaxRequestTarget) {
        onClicked(target)
      }
    }
  }

  def bootstrapSettings = Bootstrap.getSettings(getApplication)
}
