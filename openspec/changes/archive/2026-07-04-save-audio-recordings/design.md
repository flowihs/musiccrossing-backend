## Context

**Current State:**
- Сущность `Sound` существует с полями: id, name, trackNumber, fileType, album, playlists
- Сущность `Sound` не содержит поле duration (не хранится в БД)
- `SoundController` и `SoundService` реализованы
- `FileType` расширен поддержкой аудиоформатов (mp3, wav, aac, flac, ogg)
- `S3StorageService` реализован для аудио
- Контроль доступа основан на владении альбомом (owner check)

**Constraints:**
- Аудиофайлы должны сохраняться в S3, метаданные — в PostgreSQL
- Загрузка через бэкенд (multipart form data с file attachment)
- Доступ к аудио определяется владением альбома (album.owner == authenticated user)
- Клиент получает прямую ссылку на S3 для загрузки аудио
- Одновременное сохранение в S3 и БД в транзакции
- trackNumber автоматически рассчитывается как max(trackNumber) + 1 для альбома

## Goals / Non-Goals

**Goals:**
- Реализовать полный жизненный цикл аудиозаписей
- Обеспечить безопасность через access control по owner
- Масштабируемая архитектура с прямой загрузкой из S3
- Поддержка основных аудиоформатов (mp3, wav, aac, flac, ogg)
- Автоматическое присвоение trackNumber

**Non-Goals:**
- Прямая загрузка клиент- S3 (на данном этапе)
- Метаданные аудио (ID3 tags, etc.)
- Транскодирование аудио
- Статистика прослушивания
- CDN для аудио
- Хранение duration в БД

## Decisions

### 1. Архитектура загрузки (Server Proxy with Multipart)
**Decision:** Клиент отправляет multipart form data с file attachment → Backend сохраняет в S3 → Сохраняет в DB

**Rationale:**
- Безопасность: валидация перед сохранением
- Транзакционность: S3 + DB в одной транзакции
- Упрощение клиента (один запрос)
- Прямая загрузка из S3 без проксирования

**Alternatives Considered:**
- Presigned URL с клиента: сложнее с access control и валидацией
- Бинарная загрузка: сложнее с multipart и валидацией

### 2. S3 Key Structure
**Decision:** `{id}{extension}` (например, `a1b2c3d4-uuid.mp3`)

**Rationale:**
- Уникальность через UUID
- Flat структура без префиксов для максимальной масштабируемости
- S3 оптимизирован для хранения миллиардов объектов в одной bucket
- Нет overhead на префиксы

**Alternative Considered:** `sound/{id}{extension}`
**Rationale for rejection:** Ненужный overhead, flat структура лучше масштабируется

### 3. Access Control
**Decision:** Использовать владение альбомом (album.owner == authenticated user)

**Rationale:**
- Упрощение архитектуры
- Ясные права доступа
- Строгая безопасность (только owner может работать с альбомом)

### 4. Presigned URL Generation
**Decision:** Генерировать прямую URL без expiration

**Rationale:**
- Простота: клиент загружает напрямую из S3
- Прямой доступ к файлу
- URL формируется как: `s3Endpoint/bucket/{id}{extension}`

### 5. FileType Extension
**Decision:** Добавить `.mp3`, `.wav`, `.aac`, `.flac`, `.ogg`

**Rationale:**
- Популярные форматы
- Поддержка как проприетарных, так и open-source
- Можно расширять в будущем
- Тип извлекается из `originalFilename` multipart file

### 6. DTO Structure and S3 URL Generation
**Decision:** Генерировать S3 URL на лету в сервисе на основе `id` + `FileType`

**Rationale:**
- Нет дублирования данных: URL восстанавливается из `id` и `FileType`
- Гибкость: при смене S3 endpoint'а не нужно обновлять БД
- Простота: меньше полей в сущности

**Implementation:**
- `Sound` entity хранит `id`, `name`, `trackNumber`, `fileType`, `album`
- `SoundService` генерирует URL: `s3Endpoint/bucket/{id}{extension}` (flat structure)
- `SoundResponseDto` включает `s3Url` (генерируется сервисом)

**Request (SoundDto):** `albumId`, `name`  
**Response (SoundResponseDto):** `id`, `name`, `trackNumber`, `fileType`, `s3Url`, `albumId`, `albumName`

### 7. TrackNumber Auto-Calculation
**Decision:** Автоматически рассчитывать trackNumber как max(trackNumber) + 1 для альбома

**Rationale:**
- Упрощение API: клиент не должен передавать trackNumber
- Автоматическое упорядочивание треков
- Гарантия уникальности внутри альбома

**Implementation:**
- Если album.getSounds() == null: trackNumber = 1
- Иначе: trackNumber = max(album.getSounds().trackNumber) + 1

### 8. Transaction Strategy
**Decision:** `@Transactional` в `SoundService.save()` с `S3Template.upload()` + `SoundRepository.save()`

**Rationale:**
- Either both S3 and DB succeed, or both fail
- Spring transaction management
- Rollback через исключения

## Risks / Trade-offs

**[S3 Failure after DB Save]** → Use try-catch with manual rollback or S3 delete on failure  
**[Large Files]** → Consider chunked upload or presigned URL approach for very large files (>100MB)  
**[Access Control Bypass]** → Always validate access in controller before generating URL  
**[S3 Endpoint Changes]** → External S3 endpoint configuration may change; use Spring properties  
**[File Extension Extraction]** → Vulnerable to malicious filenames (e.g., `file.mp3.exe`) - should add validation
