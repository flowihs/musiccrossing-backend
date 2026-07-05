## Purpose

Спецификация для управления альбомами: создание, открытие, обновление, удаление и управление доступом.

## Requirements

### Requirement: Open album by ID
Система SHALL позволять пользователям открывать альбом по ID.

#### Scenario: Owner opens album
- **WHEN** владелец альбома отправляет GET /album/{id}
- **THEN** система возвращает AlbumFullDto с полными данными альбома и всеми звуками

#### Scenario: Public album access
- **WHEN** любой аутентифицированный пользователь открывает public альбом (publicContent=true)
- **THEN** система возвращает AlbumFullDto с полными данными альбома и всеми звуками

#### Scenario: Access denied
- **WHEN** пользователь пытается открыть private альбом, не являясь его владельцем
- **THEN** система возвращает 403 AccessDeniedException

#### Scenario: Album not found
- **WHEN** пользователь пытается открыть несуществующий альбом
- **THEN** система возвращает 404 AlbumNotFoundException

### Requirement: Get user's albums
Система SHALL возвращать все альбомы текущего пользователя.

#### Scenario: Get albums list
- **WHEN** аутентифицированный пользователь отправляет GET /album/by-user
- **THEN** система возвращает Page<AlbumSummaryDto> с альбомами пользователя

#### Scenario: Empty list
- **WHEN** пользователь не имеет альбомов
- **THEN** система возвращает пустую страницу Page<AlbumSummaryDto> с totalElements=0

### Requirement: Get public albums
Система SHALL возвращать все public альбомы.

#### Scenario: Get public albums list
- **WHEN** аутентифицированный пользователь отправляет GET /album/public
- **THEN** система возвращает Page<AlbumSummaryDto> со всеми public альбомами

#### Scenario: No public albums
- **WHEN** нет public альбомов
- **THEN** система возвращает пустую страницу Page<AlbumSummaryDto> с totalElements=0

### Requirement: Update album
Система SHALL позволять владельцу обновлять artistName и albumName альбома.

#### Scenario: Update album
- **WHEN** владелец отправляет PUT /album/{id} с новыми данными
- **THEN** система обновляет альбом и возвращает AlbumFullDto с обновленными данными

#### Scenario: Update with duplicate name
- **WHEN** владелец пытается обновить альбом с именем, которое уже есть у него
- **THEN** система возвращает ошибку уникальности

#### Scenario: Update denied
- **WHEN** не-владелец пытается обновить альбом
- **THEN** система возвращает 403 AccessDeniedException

#### Scenario: Album not found
- **WHEN** пытаемся обновить несуществующий альбом
- **THEN** система возвращает 404 AlbumNotFoundException

### Requirement: Delete album
Система SHALL позволять владельцу удалять альбом.

#### Scenario: Delete album
- **WHEN** владелец отправляет DELETE /album/{id}
- **THEN** система удаляет альбом и возвращает 204 No Content

#### Scenario: Delete denied
- **WHEN** не-владелец пытается удалить альбом
- **THEN** система возвращает 403 AccessDeniedException

#### Scenario: Album not found
- **WHEN** пытаемся удалить несуществующий альбом
- **THEN** система возвращает 404 AlbumNotFoundException

### Requirement: Create album
Система SHALL позволять создавать альбом с полем publicContent.

#### Scenario: Create album with publicContent
- **WHEN** пользователь создает альбом с publicContent=true
- **THEN** альбом создается с publicContent=true

#### Scenario: Create album with default publicContent
- **WHEN** пользователь создает альбом без указания publicContent
- **THEN** альбом создается с publicContent=false (по умолчанию)
