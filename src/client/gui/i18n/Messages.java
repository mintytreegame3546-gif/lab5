package client.gui.i18n;

import java.util.ListResourceBundle;

public class Messages extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                {"app.title", "Organization Client"},
                {"auth.title", "Authentication"},
                {"auth.username", "Username"},
                {"auth.password", "Password"},
                {"auth.login", "Login"},
                {"auth.register", "Register"},
                {"auth.language", "Language"},
                {"main.user", "User: {0}"},
                {"main.filter", "Filter"},
                {"main.refresh", "Refresh"},
                {"main.add", "Add"},
                {"main.edit", "Edit"},
                {"main.delete", "Delete"},
                {"main.execute", "Execute"},
                {"main.command", "Command"},
                {"main.status.ready", "Ready"},
                {"main.status.updated", "Collection updated"},
                {"table.id", "ID"},
                {"table.name", "Name"},
                {"table.x", "X"},
                {"table.y", "Y"},
                {"table.created", "Created"},
                {"table.turnover", "Turnover"},
                {"table.type", "Type"},
                {"table.street", "Street"},
                {"table.zip", "Zip"},
                {"table.owner", "Owner"},
                {"dialog.details", "Organization details"},
                {"dialog.organization", "Organization"},
                {"dialog.save", "Save"},
                {"dialog.cancel", "Cancel"},
                {"dialog.error", "Error"},
                {"dialog.select", "Select an organization first"},
                {"dialog.notOwner", "You can edit or delete only your own organizations"},
                {"field.name", "Name"},
                {"field.x", "X"},
                {"field.y", "Y"},
                {"field.turnover", "Annual turnover"},
                {"field.type", "Type"},
                {"field.street", "Street"},
                {"field.zip", "Zip code"}
        };
    }
}
