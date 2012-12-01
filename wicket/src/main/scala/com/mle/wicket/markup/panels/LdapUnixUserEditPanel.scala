package com.mle.wicket.markup.panels

import org.apache.wicket.model.IModel
import com.mle.wicket.markup.AbstractUsers.EditableUser

/**
 *
 * @author mle
 */
abstract class LdapUnixUserEditPanel(id: String,
                                     editModel: IModel[EditableUser],
                                     updating: IModel[Boolean])
  extends UserEditPanel(id, editModel, updating)
  with LdapHostsEditing