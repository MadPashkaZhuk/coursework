# Микросервис, отвечающий за работу больницы.
Для запуска проекта необходимо создать .env файл, куда поместить следующие переменные: DB_URL, DB_USERNAME, DB_PASSWORD, 
ADMIN_USERNAME, ADMIN_PASSWORD, DOC1_USERNAME, DOC1_PASSWORD, DOC2_USERNAME, DOC2_PASSWORD, NURSE1_USERNAME, 
NURSE1_PASSWORD, NURSE2_USERNAME, NURSE2_PASSWORD, MEDICATION_URL, MEDICATION_USERNAME, MEDICATION_PASSWORD.<br>
Они используются для заполнения БД пользователей через миграции.

### Сущности
Есть три основные сущности: User (отвечающий за пользователя), Department (отвечающий за отделение больницы) и Task (отвечающий за назначение врача).

### Роли:
Имеются три основные роли: Admin, Nurse, Doctor.<br>
Администратор имеет доступ к UserController, где есть все базовые CRUD-операции для работы с пользователями, 
и имеет доступ ко всему остальному функционалу приложения.<br>
Доктор имеет возможность создавать назначения для пациентов и изменять их по своему усмотрению.<br>
Медсестра может видеть список задач (назначений) на сегодня, чтобы выполнять свою работу.

### Взаимодействие с внешним сервисом:
Когда доктор добавляет назначение для пациента, то происходит обращение к складу (второму микросервису), 
и если количества, запрошенного врачом, достаточно, то выполняется PATCH-метод к складу, изменяется количество препарата
и успешно создаются назначения в БД. Если же препаратов не хватает - выбрасывается ошибка.

### Документация 
Микросервис задокументирован через Swagger, его можно получить перейдя по ссылке на swagger ui.

### Тесты:
Процент тестового покрытия через Jacoco: 88%

### Технологии:
Использованные технологии можно увидеть в readme всего курсового проекта.
