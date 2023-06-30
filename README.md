Подключаемся к Postgresql
Запустить проект
HTTP запрос для регистрации
POST http://localhost:8080/api/v1/auth/register
Content-Type: application/json

{
"firstname": "Azat",
"lastname": "Balgali",
"email": "azatbalgali@gmai.com",
"password": "123456"
}
HTTP запрос для аутентификации
POST http://localhost:8080/api/v1/auth/authenticate
Content-Type: application/json

{
"email": "azatbalgali@gmai.com",
"password": "123456"
}
