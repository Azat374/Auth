## Аутентификация
/api/v1/auth/forgot-password - востановление пароля

/api/v1/auth/login - вход

/api/v1/auth/register - регистрация

/api/v1/auth/reset-password/{email} - это придеть почту и обновление пароля

## TODO

/api/v1/todo - получение todo(можно применять фильры)

/api/v1/todo/TodoByName/{name} - поиск todo

/api/v1/todo/add-todo - добавление todo

/api/v1/todo/add-todos - добовление несколько todo

/api/v1/todo/delete/{id}  - удаление через айди

/api/v1/todo/overdue - незашершенные вчерашние todo

/api/v1/todo/status-change/{id} - обновление статуса

/api/v1/todo/today - сегоднешние todo

/api/v1/todo/update - изменение todo

## Для админа

/api/v1/admin/allTodos

/api/v1/admin/listTodoByUsername/{userId}

/api/v1/admin/users

/api/v1/admin/{id}
