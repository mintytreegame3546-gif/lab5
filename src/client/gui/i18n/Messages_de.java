package client.gui.i18n;

import java.util.ListResourceBundle;

public class Messages_de extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                {"app.title", "Organisations-Client"},
                {"auth.title", "Anmeldung"},
                {"auth.username", "Benutzername"},
                {"auth.password", "Passwort"},
                {"auth.login", "Anmelden"},
                {"auth.register", "Registrieren"},
                {"auth.language", "Sprache"},
                {"main.user", "Benutzer: {0}"},
                {"main.filter", "Filter"},
                {"main.refresh", "Aktualisieren"},
                {"main.add", "Hinzufügen"},
                {"main.edit", "Bearbeiten"},
                {"main.delete", "Löschen"},
                {"main.execute", "Ausführen"},
                {"main.command", "Befehl"},
                {"main.status.ready", "Bereit"},
                {"main.status.updated", "Sammlung aktualisiert"},
                {"table.id", "ID"},
                {"table.name", "Name"},
                {"table.x", "X"},
                {"table.y", "Y"},
                {"table.created", "Erstellt"},
                {"table.turnover", "Umsatz"},
                {"table.type", "Typ"},
                {"table.street", "Straße"},
                {"table.zip", "PLZ"},
                {"table.owner", "Eigentümer"},
                {"dialog.details", "Organisationsdetails"},
                {"dialog.organization", "Organisation"},
                {"dialog.save", "Speichern"},
                {"dialog.cancel", "Abbrechen"},
                {"dialog.error", "Fehler"},
                {"dialog.select", "Bitte zuerst eine Organisation auswählen"},
                {"dialog.notOwner", "Sie können nur eigene Organisationen bearbeiten oder löschen"},
                {"field.name", "Name"},
                {"field.x", "X"},
                {"field.y", "Y"},
                {"field.turnover", "Jahresumsatz"},
                {"field.type", "Typ"},
                {"field.street", "Straße"},
                {"main.history", "Befehlsverlauf"},
                {"history.command", "Befehl"},
                {"history.arguments", "Argumente"},
                {"history.executedAt", "Ausgeführt um"},
                {"theme.menu", "Design"},
                {"theme.light", "Heller Modus"},
                {"theme.dark", "Dunkler Modus"},
                {"field.zip", "Postleitzahl"}
        };
    }
}
