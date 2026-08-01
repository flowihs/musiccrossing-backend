## ADDED Requirements

### Requirement: Audio access control based on album ownership
The system SHALL enforce access control on audio files based on album ownership (user who created the album).

#### Scenario: Owner access
- **WHEN** album belongs to the authenticated user
- **AND** user requests access to audio in that album
- **THEN** system grants access and allows operations (upload, retrieval)
- **AND** system returns HTTP 200 OK with audio metadata

#### Scenario: Non-owner access denied
- **WHEN** album does NOT belong to the authenticated user
- **AND** user attempts to access or modify audio in that album
- **THEN** system denies access
- **AND** system returns HTTP 403 Forbidden

#### Scenario: Playlist access
- **WHEN** user requests sounds from a playlist
- **AND** playlist contains sounds from various albums
- **THEN** system filters sounds based on user's ownership of each album
- **AND** system only returns sounds user owns (created)

### Requirement: Presigned URL with S3 format
The system SHALL generate S3 URLs in a specific format for audio file access.

#### Scenario: URL format
- **WHEN** system generates URL for audio file
- **THEN** URL follows format: `s3Endpoint/bucket/{id}{extension}`
- **AND** URL uses flat structure (no prefixes)
- **AND** URL allows direct access to S3 object

#### Scenario: URL usage
- **WHEN** client receives URL in response
- **THEN** client can use URL to download audio directly from S3
- **AND** client must have appropriate permissions to access S3 bucket
