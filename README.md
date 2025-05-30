# Cinemania

### Цель

Разработка стримингового сервиса с поддержкой современных протоколов передачи данных, обеспечивающего бесперебойную
передачу контента, ориентированного на потребности современного пользователя, с монетизацией посредством подписки.

### Задачи

1. Разработать клиентские приложения для ОС Android, SmartTV и веб-приложение.
2. Разработать микросервис для загрузки контента в облачное хранилище, конкретно сведений о видеоматериале, его заставок
   и парсинг загружаемых видеороликов в формат адаптивного потокового видео.
3. Разработать микросервис оптимизированной потоковой передачи видеоконтента на клиентские приложения.
4. Разработать микросервис разграничения доступа к созданной ИС по ролям, уровням подписки через аутентификацию.

---

## Используемые технологии

Архитектура приложения: REST API \
Языки: Java, Javascript, CSS (TailwindCSS), HTML, Bash. \
Основные фреймворки: Spring Boot, React.js, React Native \
Базы данных: Yandex Cloud Storage, Postgesql, Mongodb, Redis

1. Микросервис потоковой передачи данных
    - Yandex Object Storage
    - Maven
    - Spring Web
    - Jetty HTTP/3 (web server)
    - HLS (HTTP Live Streaming)
    - Nginx
      
2. Микросервис аутентификации и управления пользователями
    - OAuth 2.0
    - Spring Web
    - Spring Security
    - Maven: Библиотеки криптографии
    - PostgreSQL
      
3. Микросервис менеджмента метаданных изображений и сведений о фильмах
    - Yandex Cloud Storage
    - MongoDB
    - Redis
    - Maven
    - Spring Web
    - Spring Data MongoDB
    - Thumbnailator (бибилотека java для создания заставок)

4. Система уведомлений
    - Kafka
    - SendGrid
    - Push-уведомления
    - Spring Kafka

5. Клиентские приложения (веб, Smart TV и Android)
    - React.js
    - React Native Expo
    - сторонние библиотеки React, React Native Expo
    - Tailwind CSS
    - REST API
    - Zustand
    - Nginx
   
6. Тестирование
    - JUnit, Mockito, Spring Mock Mvc
    - Postman, Curl, Bash
      Деплой
    - Docker
    - Kubernetes

7. Базы данных
    - PostgreSQL
    - MongoDB
    - Yandex Cloud Object Storage (S3)
    - minio (только для тестирования)

---

## Описание функционала

### Клиентские приложения (веб, Smart TV и Android)

1. Веб-клиент (React.js): будет создан отзывчивый веб-клиент с основными функциями, такими как просмотр контента,
   просмотр информации о фильме и воспроизведение видео.
2. Клиенты для Android и Smart TV: используется React Native для приложений для Android и Smart TV, сосредоточившись на
   основных функциях навигации и воспроизведения видео.
3. Унифицированный язык дизайна: поддерживается единый дизайн для всех клиентов, используя Tailwind CSS.
4. Клиентская часть для администраторов с формами для создания и редактирования страниц с видеоматериалами.
5. Дополнительные возмонжности клиентов:
6. Список просмотра/избранное: позволяют пользователям сохранять передачи или фильмы для последующего просмотра. Храните
   эти предпочтения в легкой базе данных или как часть профиля пользователя в микросервисе управления пользователями.
7. Рекомендация контента: используется простые эвристики, например, рекомендации по жанрам, основанные на активности
   пользователей.
8. Возможность выставить рейтинг определённым видеоматериалам.

### Микросервис потоковой передачи данных

1. Хранение и получение видео: Интеграция с Yandex Object Storage для хранения и получения видеофайлов. Прямой доступ к
   предварительно закодированным видео для подготовки к передаче по HTTP/3.
2. Адаптивная потоковая передача: используется HLS для адаптивного потокового вещания, позволяющего передавать качество
   видео в зависимости от пропускной способности канала.
3. Балансировка нагрузки: Балансировка нагрузки с помощью nginx для увеличения производительности и уменьшения времени
   обработки запросов в высоконагруженных системах.

### Микросервис аутентификации и управления пользователями

1. Регистрация и аутентификация пользователей: Реализация аутентификации на основе OAuth 2.0 для безопасного входа и
   создания учетных записей.
2. Управление профилем: позволяет выполнять основные настройки профиля, такие как обновление паролей и просмотр сведений
   об учетной записи.
3. Аутентификация по существующей учётной записи в популярных соцсетях.
4. Состояние подписки: отслеживается состояние подписки, предлагая базовую дифференциацию бесплатного/платного доступа.
5. Возможности управления подпиской пользователем.

### Микросервис менеджмента метаданных изображений и сведений о фильмах

1. Управление изображениями: Хранение изображений на Yandex Cloud Storage, с конечными точками для получения
   предварительных просмотров и миниатюр фильмов.
2. Хранение метаданных: используется база данных типа MongoDB для хранения и управления деталями фильма, такими как
   название, описание, жанр и дата выхода.
3. Удобное управление существующей системой с возможностями обновления данных, заставок, трейлеров, видеоматериалов.
4. Кэширование изображений: реализуется простой слой кэширования (Redis или кэширование в памяти), чтобы ускорить
   загрузку изображений при частых запросах.
5. Базовый поиск существующих видеоматериалов.

### Система уведомлений

• Уведомления по электронной почте и в приложении: используется Kafka для запуска уведомлений через сервис типа SendGrid
для электронной почты и простых уведомлений в приложении для мобильных и веб-приложений.

---

## Эффективность и экономия ресурсов

Использование HTTP/3 как основного протокола передачи данных позволяет значительно
уменьшить задержки, увеличить скорость загрузки и снизить нагрузку на серверы, что приводит к более плавной и
устойчивой
потоковой передаче даже при низком качестве соединения.  \
Это особенно выгодно для экономии ресурсов на стороне сервера и
улучшения пользовательского опыта и достигается с помощью двух протоколов, из которых состоит HTTP/3. \
UDP, который считается более скоростным в сравнении с TCP, но с ним часть сведений может потеряться, и нет механизма
восстановления.

[//]: # (   Так же 3 версия — это единственный из HTTP протоколов, который использует скоростной UDP, а HTTP, это основа всего современного интернета.)

[//]: # (   Это является колоссальным преимуществом перед знаменитыми корпорациями, которые используют старые медленные протоколы передачи данных и на данный момент не могут интегрировать новые из-за большой цены реконструирования собственной инфраструктуры для их внедрения.)
Эту проблему решает QUIC:

- Сокращает установку соединения.
- Берёт от UDP высокую скорость, но контролирует целостность файлов.
- Может передавать несколько файлов параллельно, что тоже ускоряет доставку.
