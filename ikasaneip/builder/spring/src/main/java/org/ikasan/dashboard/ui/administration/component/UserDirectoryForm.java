package org.ikasan.dashboard.ui.administration.component;

import com.vaadin.flow.templatemodel.TemplateModel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.polymertemplate.PolymerTemplate;

/**
 * A Designer generated component for the user-directory-form.html template.
 *
 * Designer will add and remove fields with @Id mappings but
 * does not overwrite or otherwise change this file.
 */
@Tag("user-directory-form")
@HtmlImport("ikasaneip/visualisation-dashboard/user-directory-form.html")
public class UserDirectoryForm extends PolymerTemplate<UserDirectoryForm.UserDirectoryFormModel> {

    /**
     * Creates a new UserDirectoryForm.
     */
    public UserDirectoryForm() {
        // You can initialise any data required for the connected UI components here.
    }

    /**
     * This model binds properties between UserDirectoryForm and user-directory-form.html
     */
    public interface UserDirectoryFormModel extends TemplateModel {
        // Add setters and getters for template properties here.
    }
}
