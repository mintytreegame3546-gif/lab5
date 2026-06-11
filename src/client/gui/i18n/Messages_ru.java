package client.gui.i18n;

import java.util.ListResourceBundle;

public class Messages_ru extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][] {
                {"app.title", "Клиент организаций"},
                {"auth.title", "Аутентификация"},
                {"auth.username", "Имя пользователя"},
                {"auth.password", "Пароль"},
                {"auth.login", "Войти"},
                {"auth.register", "Зарегистрироваться"},
                {"auth.language", "Язык"},
                {"main.user", "Пользователь: {0}"},
                {"main.filter", "Фильтр"},
                {"main.refresh", "Обновить"},
                {"main.add", "Добавить"},
                {"main.edit", "Изменить"},
                {"main.delete", "Удалить"},
                {"main.execute", "Выполнить"},
                {"main.command", "Команда"},
                {"main.status.ready", "Готово"},
                {"main.status.updated", "Коллекция обновлена"},
                {"table.id", "ID"},
                {"table.name", "Название"},
                {"table.x", "X"},
                {"table.y", "Y"},
                {"table.created", "Создано"},
                {"table.turnover", "Оборот"},
                {"table.type", "Тип"},
                {"table.street", "Улица"},
                {"table.zip", "Индекс"},
                {"table.owner", "Владелец"},
                {"dialog.details", "Сведения об организации"},
                {"dialog.organization", "Организация"},
                {"dialog.save", "Сохранить"},
                {"dialog.cancel", "Отмена"},
                {"dialog.error", "Ошибка"},
                {"dialog.select", "Сначала выберите организацию"},
                {"dialog.notOwner", "Можно изменять или удалять только свои организации"},
                {"field.name", "Название"},
                {"field.x", "X"},
                {"field.y", "Y"},
                {"field.turnover", "Годовой оборот"},
                {"field.type", "Тип"},
                {"field.street", "Улица"},
                {"field.zip", "Индекс"}
        };
    }
}
