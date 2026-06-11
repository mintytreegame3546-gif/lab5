package client.gui.i18n;

import java.util.ListResourceBundle;

public class Messages_hu extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                {"app.title", "Szervezet kliens"},
                {"auth.title", "Hitelesítés"},
                {"auth.username", "Felhasználónév"},
                {"auth.password", "Jelszó"},
                {"auth.login", "Bejelentkezés"},
                {"auth.register", "Regisztráció"},
                {"auth.language", "Nyelv"},
                {"main.user", "Felhasználó: {0}"},
                {"main.filter", "Szűrő"},
                {"main.refresh", "Frissítés"},
                {"main.add", "Hozzáadás"},
                {"main.edit", "Szerkesztés"},
                {"main.delete", "Törlés"},
                {"main.execute", "Futtatás"},
                {"main.command", "Parancs"},
                {"main.status.ready", "Kész"},
                {"main.status.updated", "Gyűjtemény frissítve"},
                {"table.id", "ID"},
                {"table.name", "Név"},
                {"table.x", "X"},
                {"table.y", "Y"},
                {"table.created", "Létrehozva"},
                {"table.turnover", "Forgalom"},
                {"table.type", "Típus"},
                {"table.street", "Utca"},
                {"table.zip", "Irányítószám"},
                {"table.owner", "Tulajdonos"},
                {"dialog.details", "Szervezet adatai"},
                {"dialog.organization", "Szervezet"},
                {"dialog.save", "Mentés"},
                {"dialog.cancel", "Mégse"},
                {"dialog.error", "Hiba"},
                {"dialog.select", "Előbb válasszon szervezetet"},
                {"dialog.notOwner", "Csak saját szervezeteit szerkesztheti vagy törölheti"},
                {"field.name", "Név"},
                {"field.x", "X"},
                {"field.y", "Y"},
                {"field.turnover", "Éves forgalom"},
                {"field.type", "Típus"},
                {"field.street", "Utca"},
                {"main.history", "Parancselőzmények"},
                {"history.command", "Parancs"},
                {"history.arguments", "Argumentumok"},
                {"history.executedAt", "Végrehajtva"},
                {"theme.menu", "Téma"},
                {"theme.light", "Világos mód"},
                {"theme.dark", "Sötét mód"},
                {"field.zip", "Irányítószám"}
        };
    }
}
