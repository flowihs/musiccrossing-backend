## Why

Текущая система имеет заготовку для работы с аудиозаписями (сущность `Sound` в пакете `music`), но функционал сохранения файлов в S3 хранилище и управления ими не реализован. Необходимо реализовать полный жизненный цикл аудиозаписей: загрузку файлов, хранение метаданных в БД, управление доступом и получение ссылок для прослушивания.

## What Changes

- Добавить поля `trackNumber` и `fileType` в сущность `Sound` (duration не хранится)
- Расширить enum `FileType` поддержкой аудиоформатов (mp3, wav, aac, flac, ogg)
- Реализовать сервис `SoundService` с методом `save()` для сохранения файлов в S3 и метаданных в БД
- Добавить методы для получения аудиозаписей с генерацией прямых URL
- Реализовать систему управления доступом на основе владения альбомом
- Обновить `SoundController` для обработки загрузки файлов
- Генерировать S3 URL на лету в сервисе на основе `id` + `fileType` (не хранить URL в БД)

## Capabilities

### New Capabilities
- **audio-recording-save**: Загрузка и сохранение аудиофайлов в S3 с сохранением метаданных в PostgreSQL
- **audio-recording-access**: Управление доступом к аудиозаписям и генерация прямых URLs для загрузки из S3
- **audio-file-types**: Поддержка различных аудиоформатов через расширяемый enum FileType

### Modified Capabilities
- `music-sound`: Расширение сущности Sound полями trackNumber, fileType
- `music-album-access`: Использование владения альбомом для контроля доступа к аудиозаписям

## Impact

**Affected Code:**
- `src/main/java/ru/github/musiccrossing/music/entity/Sound.java` — добавить поля
- `src/main/java/ru/github/musiccrossing/storage/FileType.java` — расширить перечислением
- `src/main/java/ru/github/musiccrossing/music/dto/request/SoundDto.java` — обновить DTO
- `src/main/java/ru/github/musiccrossing/music/service/SoundService.java` — реализовать логику
- `src/main/java/ru/github/musiccrossing/music/controller/SoundController.java` — добавить endpoint
- `src/main/java/ru/github/musiccrossing/storage/service/S3StorageService.java` — добавить методы для прямых URLs

**Dependencies:**
- Используется existing `S3Config` и `S3Template` из `spring-cloud-aws-starter-s3`
- Используется existing `Album` entity для access control

**Breaking Changes:** None
