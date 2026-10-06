# Домашнее задание 4 — клиент SpaceX API с кешем

Консольное приложение, которое получает данные о запусках SpaceX через HTTP, разбирает JSON и кеширует ответы в файл.

- `SpaceXHttpClient` — запросы к API.
- `JsonParser`, `JsonBuilder` — работа с JSON.
- `CacheManager` — файловый кеш (`cache/cache_meta.json`).
- `SpaceXMenu`, `Launch`, `Core` — меню и логика.
- `src/test` — тесты, тестовые JSON-файлы в `src/test/resources`.

> В репозитории папка с кодом называется `hmework4` (без буквы «о») — так она и была в исходной ветке.

## Сборка и тесты

```bash
cd hmework4
mvn clean test
```

Исходный код взят из ветки `homework4` репозитория [wmeenw/JAVAhomework](https://github.com/wmeenw/JAVAhomework).
