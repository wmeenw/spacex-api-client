# Клиент SpaceX API с кешем

Домашнее задание 4 курса по Java. Консольное приложение, которое получает данные о запусках SpaceX через HTTP.

## Что сделано в качестве ДЗ

- HTTP-клиент к SpaceX API (`SpaceXHttpClient`).
- Разбор и формирование JSON без внешних библиотек (`JsonParser`, `JsonBuilder`).
- Файловый кеш ответов с метаданными (`CacheManager`, `cache/cache_meta.json`).
- Консольное меню (`SpaceXMenu`) и модель запуска (`Launch`).
- Юнит-тесты на кеш, JSON и HTTP-клиент (`src/test`) с тестовыми ответами в `src/test/resources`.

## Сборка и тесты

В репозитории папка с кодом называется `hmework4` (так она и была в исходной ветке).

```bash
cd hmework4
mvn clean test
```

Исходный код: ветка `homework4` репозитория [wmeenw/JAVAhomework](https://github.com/wmeenw/JAVAhomework).
