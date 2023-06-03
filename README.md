# java-kanban
---
#### Приложение трекер задач

Приложение имеет следующий функционал:
1. Создание и хранение задач разных типов (обычная задача, задача с подзадачами, подзадачи);
2. Обновлять данные задач (изменение статуса прогегресса, добавление и редактирование описание задачи);
3. Хранить историю задач и просмотров;
4. Сохраненять (автосохранять) данные в формате CSV, а также читать документов;
5. Использовать для хранения данных отдельный сервер.

Для удобства использоавния проекта был реализован REST API на базе HttpServer.

Также логика проекта направлена на правльную работу с задачами. 
Например, пользователь не сможет завершить задачу без выполнения всех связанных с ней подзадач.
Для оптимизации процесса хранения истории использован кастомный двухсвязный список.

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

