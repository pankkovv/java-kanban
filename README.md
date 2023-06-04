# java-kanban
## Приложение трекер задач

Для удобства использоавния проекта был реализован REST API на базе HttpServer.

Логика проекта направлена на правльную работу с задачами. 
Например, пользователь не сможет завершить задачу без выполнения всех связанных с ней подзадач.
Для оптимизации процесса хранения истории был использован кастомный двухсвязный список.

Приложение имеет следующие функции:
1. Создание и хранение задач разных типов (обычная задача, задача с подзадачами, подзадача);
2. Обновление данных задачи (изменение статуса прогресса, добавление и редактирование описания задачи);
3. Хранение истории просмотров задач;
4. Сохранение (автосохранение) данных в формате CSV, а также чтение документов;
5. Использование отдельного сервера для хранения данных.

### Примеры запросов
Tasks:
1. Получние списка всех существующих задач: GET http://localhost:8078/tasks

Task:
1. Создание новой задачи: POST http://localhost:8078/tasks/task, в Request Body json с данными задачи.
2. Обновление задачи: POST http://localhost:8078/tasks/uptask?id=, в Request Body json с данными задачи.
3. Удаление задачи по id: DELETE http://localhost:8078/tasks/task?id=.
4. Получение задачи по id: GET http://localhost:8078/tasks/task?id=.

Epic:
1. Создание новой epic-задачи: POST http://localhost:8078/tasks/epic, в Request Body json с данными задачи.
2. Обновление задачи: POST http://localhost:8078/tasks/upepic?id=, в Request Body json с данными задачи.
3. Удаление задачи по id: DELETE http://localhost:8078/tasks/epic?id=.
4. Получение задачи по id: GET http://localhost:8078/tasks/epic?id=.
5. Получение списка подзадач epic-задачи по id: GET http://localhost:8078/tasks/allsubepic/?id=.

Subtask:
1. Создание новой подзадачи: POST http://localhost:8078/tasks/subtask, в Request Body json с данными задачи.
2. Обновление подзадачи: POST http://localhost:8078/tasks/upsubtaskc?id=, в Request Body json с данными задачи.
3. Удаление подзадачи по id: DELETE http://localhost:8078/tasks/subtask?id=.
4. Получение подзадачи по id: GET http://localhost:8078/tasks/subtask?id=.

History:
1. Получение списка просмотренных задач: GET http://localhost:8078/history.

Load:
1. Чтение данных из бэкап файла: GET http://localhost:8078/load
----
Приложение написано на Java и протестировано с помощью JUnit. Пример кода:
```java
class MethodHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

            switch (endpoint) {
                case GET_TASKS:
                    getPrioritizedTasks(exchange);
                    break;

                case GET_TASK:
                    getTaskHandler(exchange);
                    break;

                case GET_EPIC:
                    getEpicHandler(exchange);
                    break;

                case GET_SUB:
                    getSubHandler(exchange);
                    break;
                    ...
                    ...
}
```
------
Приложение создано в рамках прохождения курса Java-разработчик от [Яндекс-Практикум](https://practicum.yandex.ru/java-developer/ "Тут учат Java!")

