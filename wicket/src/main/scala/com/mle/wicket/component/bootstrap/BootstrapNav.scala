package com.mle.wicket.component.bootstrap

import com.mle.wicket.{BasicWebApplication, Bootstrapping}
import de.agilecoders.wicket.markup.html.bootstrap.navbar.{ImmutableNavbarComponent, NavbarButton, Navbar, NavbarDropDownButton}
import org.apache.wicket.{Page, MarkupContainer}
import org.apache.wicket.model.Model
import org.apache.wicket.protocol.http.WebApplication
import com.mle.util.Log
import de.agilecoders.wicket.markup.html.bootstrap.image.{Icon, IconType}
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import de.agilecoders.wicket.markup.html.bootstrap.button.{ButtonType, ButtonBehavior}
import de.agilecoders.wicket.Bootstrap
import de.agilecoders.wicket.markup.html.bootstrap.navbar.Navbar.ComponentPosition
import collection.JavaConversions._
import com.mle.web.wicket.model.ReadOnlyModel

/**
 * test: tooltibhehavior, popoverbehavior
 * @author Mle
 */
trait BootstrapNav extends MarkupContainer with Log {

  val navbar = new Navbar("navBar")
  //  navbar.set
  navbar.fluid()
  navbar.brandName(ReadOnlyModel("My app"))
  navbar.addComponents(toNavbarComps)
  navbar.addComponents(new ImmutableNavbarComponent(new NavbarDropDownButton(Model.of("BootstrapThemes"))
    .addButtons(BootstrapNav.themes.themeNames.map(themeDropDownButton): _*), ComponentPosition.RIGHT))
  add(navbar)

  def toNavbarComps = BasicWebApplication.get.tabs.map(tab => navButton(tab.pageClass, tab.title, tab.icon)).map(comp => new ImmutableNavbarComponent(comp))

  def navButton[T <: Page](pageClass: Class[T], label: String, iconType: Option[IconType]) = {
    val button = new NavbarButton(pageClass, Model.of(label))
    iconType foreach (it => button.setIconType(it))
    button
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

object BootstrapNav {
  val app = WebApplication.get().asInstanceOf[Bootstrapping]
  val themes = app.themeService
}